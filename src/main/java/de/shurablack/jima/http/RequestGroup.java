package de.shurablack.jima.http;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CancellationException;
import java.util.function.Supplier;

/**
 * Represents a group of requests to be executed with controlled delays and token thresholds.
 * Requests are processed sequentially or in batches with configurable delays and token management.
 *
 * <p><b>Features:</b></p>
 * <ul>
 *   <li>Sequential or batch-based execution with delays between requests</li>
 *   <li>Configurable batch size - execute N requests then wait</li>
 *   <li>Automatic stalling when token count drops below the minimum threshold</li>
 *   <li>Transparent resumption when tokens are restored</li>
 *   <li>Customizable waits between batches</li>
 *   <li>Preserves request order and returns results in order</li>
 * </ul>
 *
 * <p><b>Example - Sequential with delays:</b></p>
 * <pre>
 * RequestGroup group = new RequestGroup()
 *     .withDelay(1000)
 *     .withMinTokensAllowed(15)
 *     .addRequest(() -> Requester.inspectItem("id1"))
 *     .addRequest(() -> Requester.inspectItem("id2"))
 *     .addRequest(() -> Requester.inspectItem("id3"));
 * </pre>
 *
 * <p><b>Example - Batch execution:</b></p>
 * <pre>
 * RequestGroup group = new RequestGroup()
 *     .withBatchSize(10)                      // Execute 10 at a time
 *     .withWaitMsBetweenBatches(5000)         // Wait 5 seconds between batches
 *     .withMinTokensAllowed(5);               // Ensure 5 tokens available before each request
 * 
 * for (int i = 0; i < 100; i++) {
 *     group.addRequest(() -> Requester.inspectItem("id" + i));
 * }
 * // This will execute: 10 requests, wait 5s, 10 requests, wait 5s, etc.
 * </pre>
 */
public class RequestGroup {

    private final List<Supplier<CompletableFuture<?>>> requests = new ArrayList<>();
    private final List<CompletableFuture<?>> futures = Collections.synchronizedList(new ArrayList<>());
    
    private long delayMs = 0;
    private int minTokensAllowed = 0;
    private int batchSize = Integer.MAX_VALUE;  // Default: no batching (execute all as one batch)
    private long waitMsBetweenBatches = 0;
    private String groupId = UUID.randomUUID().toString();
    private volatile boolean cancelled = false;

    /**
     * Sets the delay in milliseconds between each request in the group.
     *
     * @param delayMs Delay in milliseconds (0 = no delay)
     * @return This RequestGroup for method chaining
     */
    public RequestGroup withDelay(long delayMs) {
        this.delayMs = Math.max(0, delayMs);
        return this;
    }

    /**
     * Sets the minimum number of tokens that must remain available.
     * Requests will stall until this threshold is met.
     *
     * @param minTokens Minimum token count (0 = no minimum)
     * @return This RequestGroup for method chaining
     */
    public RequestGroup withMinTokensAllowed(int minTokens) {
        this.minTokensAllowed = Math.max(0, minTokens);
        return this;
    }

    /**
     * Sets the batch size for grouped execution.
     * When set, requests are executed in batches of this size with a wait between batches.
     * For example, if batchSize=10, the group will execute 10 requests, then wait (if configured),
     * then execute the next 10 requests, and so on.
     *
     * <p><b>Default:</b> Integer.MAX_VALUE (no batching - all requests are one batch)</p>
     *
     * @param batchSize Number of requests to execute before waiting (must be > 0)
     * @return This RequestGroup for method chaining
     * @throws IllegalArgumentException if batchSize is less than 1
     */
    public RequestGroup withBatchSize(int batchSize) {
        if (batchSize < 1) {
            throw new IllegalArgumentException("Batch size must be >= 1");
        }
        this.batchSize = batchSize;
        return this;
    }

    /**
     * Sets the wait time in milliseconds between batch executions.
     * After executing a batch of requests, the processor will wait this duration
     * before starting the next batch.
     *
     * <p><b>Only applicable when batch size is set.</b></p>
     * <p><b>Default:</b> 0ms (no wait between batches)</p>
     *
     * @param waitMs Wait time in milliseconds (0 = no wait)
     * @return This RequestGroup for method chaining
     */
    public RequestGroup withWaitMsBetweenBatches(long waitMs) {
        this.waitMsBetweenBatches = Math.max(0, waitMs);
        return this;
    }

    /**
     * Sets a custom group identifier for logging and tracking.
     *
     * @param groupId Unique identifier for this group
     * @return This RequestGroup for method chaining
     */
    public RequestGroup withGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    /**
     * Adds a request to this group.
     *
     * @param requestSupplier A supplier that produces a CompletableFuture when called
     * @return This RequestGroup for method chaining
     */
    public RequestGroup addRequest(Supplier<CompletableFuture<?>> requestSupplier) {
        this.requests.add(requestSupplier);
        return this;
    }

    /**
     * Adds a blocking response-based request to this group.
     * Useful for wrapping standard Requester methods that return Response<T>.
     *
     * <p><b>Example:</b></p>
     * <pre>
     * group.addResponseRequest(() -> Requester.getAuthentication())
     *      .addResponseRequest(() -> Requester.getWorldBosses())
     *      .addResponseRequest(() -> Requester.getDungeons());
     * </pre>
     *
     * @param requestSupplier A supplier that produces a Response when called
     * @return This RequestGroup for method chaining
     */
    public RequestGroup addResponseRequest(Supplier<Response<?>> requestSupplier) {
        // Wrap the Response-based supplier in a CompletableFuture
        this.requests.add(() -> CompletableFuture.completedFuture(requestSupplier.get()));
        return this;
    }

    /**
     * Adds multiple requests to this group.
     *
     * @param requestSuppliers Collection of request suppliers
     * @return This RequestGroup for method chaining
     */
    public RequestGroup addRequests(Collection<Supplier<CompletableFuture<?>>> requestSuppliers) {
        this.requests.addAll(requestSuppliers);
        return this;
    }

    /**
     * Adds multiple blocking response-based requests to this group.
     * Useful for wrapping multiple standard Requester methods that return Response<T>.
     *
     * @param requestSuppliers Collection of response-based request suppliers
     * @return This RequestGroup for method chaining
     */
    public RequestGroup addResponseRequests(Collection<Supplier<Response<?>>> requestSuppliers) {
        // Wrap each Response-based supplier in a CompletableFuture
        requestSuppliers.forEach(supplier -> 
            this.requests.add(() -> CompletableFuture.completedFuture(supplier.get()))
        );
        return this;
    }

    /**
     * Gets the number of requests in this group.
     *
     * @return Number of requests
     */
    public int size() {
        return requests.size();
    }

    /**
     * Gets the delay between requests in milliseconds.
     *
     * @return Delay in milliseconds
     */
    public long getDelayMs() {
        return delayMs;
    }

    /**
     * Gets the minimum tokens allowed threshold.
     *
     * @return Minimum token count
     */
    public int getMinTokensAllowed() {
        return minTokensAllowed;
    }

    /**
     * Gets the batch size for execution.
     *
     * @return Batch size (requests per batch)
     */
    public int getBatchSize() {
        return batchSize;
    }

    /**
     * Gets the wait time in milliseconds between batches.
     *
     * @return Wait time in milliseconds
     */
    public long getWaitMsBetweenBatches() {
        return waitMsBetweenBatches;
    }

    /**
     * Gets the group identifier.
     *
     * @return Group ID
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * Gets all request suppliers in this group.
     *
     * @return List of request suppliers
     */
    protected List<Supplier<CompletableFuture<?>>> getRequests() {
        return new ArrayList<>(requests);
    }

    /**
     * Gets all futures associated with this group's requests.
     *
     * @return List of futures
     */
    protected List<CompletableFuture<?>> getFutures() {
        return new ArrayList<>(futures);
    }

    /**
     * Registers a future for tracking.
     *
     * @param future The future to track
     */
    protected void addFuture(CompletableFuture<?> future) {
        futures.add(future);
    }

    /**
     * Cancels all requests in this group.
     */
    public void cancel() {
        cancelled = true;
        futures.forEach(f -> f.completeExceptionally(new CancellationException("Request group cancelled")));
    }

    /**
     * Checks if this group has been cancelled.
     *
     * @return true if cancelled
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Waits for all requests in this group to complete.
     *
     * @param timeout Maximum time to wait
     * @param unit Time unit
     * @return true if all completed within timeout, false otherwise
     * @throws InterruptedException if waiting thread is interrupted
     */
    public boolean awaitCompletion(long timeout, java.util.concurrent.TimeUnit unit) throws InterruptedException {
        long deadline = System.currentTimeMillis() + unit.toMillis(timeout);
        for (CompletableFuture<?> future : futures) {
            long remaining = deadline - System.currentTimeMillis();
            if (remaining <= 0) return false;
            try {
                future.get(remaining, unit);
            } catch (java.util.concurrent.TimeoutException e) {
                return false;
            } catch (java.util.concurrent.ExecutionException ignored) {
                // Continue even if individual requests failed
            }
        }
        return true;
    }
}
