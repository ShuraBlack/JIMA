package de.shurablack.jima.util;

import lombok.Getter;

import java.time.Instant;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Represents a single API token with rate-limiting and request queuing capabilities.
 *
 * <p><b>Overview:</b></p>
 * This class manages a single API token's request quota and reset time. It uses atomic operations
 * to ensure thread-safe access to shared state and implements a queue-based waiting mechanism
 * for requests that exceed the rate limit. When a token's quota is exhausted, requests are
 * automatically queued and served when the quota resets.
 *
 * <p><b>Features:</b></p>
 * <ul>
 *   <li><b>Rate Limit Tracking:</b> Maintains remaining requests and maximum quota using atomic integers</li>
 *   <li><b>Automatic Reset:</b> Automatically resets remaining quota when reset time is reached</li>
 *   <li><b>Thread-Safe:</b> Uses atomic operations and synchronized blocks for safe multi-threaded access</li>
 *   <li><b>Queue-Based Waiting:</b> Queues requests when token is exhausted, serves them asynchronously when available</li>
 *   <li><b>Optimistic Locking:</b> Uses compareAndSet operations for lock-free quota updates</li>
 *   <li><b>Hidden Key Display:</b> Provides masked token representation for safe logging and debugging</li>
 *   <li><b>Configurable Quota:</b> Supports custom maximum request limits per rate period</li>
 * </ul>
 *
 * <p><b>How It Works:</b></p>
 * <ol>
 *   <li>When a request needs a token via {@link #acquire()}, it attempts to decrement the remaining count</li>
 *   <li>If remaining > 0, the request is immediately approved via a completed CompletableFuture</li>
 *   <li>If remaining ≤ 0, the request is queued (added to waiters) and returned as uncompleted future</li>
 *   <li>When reset time is reached or updated, remaining is reset to max and queued requests are processed</li>
 *   <li>The drainWaiters() method attempts to fulfill queued requests using the newly available quota</li>
 * </ol>
 *
 * <p><b>Thread Safety Model:</b></p>
 * This class uses a combination of thread-safe mechanisms:
 * <ul>
 *   <li><b>Atomic Operations:</b> {@code remaining}, {@code max}, and {@code resetAt} use atomic types for visibility</li>
 *   <li><b>Synchronized Blocks:</b> {@code updateLock} protects the critical path when checking and updating reset time</li>
 *   <li><b>Compare-And-Swap (CAS):</b> {@code compareAndSet} operations on remaining provide lock-free quota updates</li>
 *   <li><b>Concurrent Collections:</b> {@code ConcurrentLinkedQueue} for thread-safe waiter management</li>
 * </ul>
 *
 * <p><b>Rate Limit Reset Behavior:</b></p>
 * <ul>
 *   <li>When a request is made, the current time is checked against {@code resetAt}</li>
 *   <li>If current time ≥ resetAt, the remaining quota is automatically reset to max</li>
 *   <li>This ensures no manual reset call is needed; the token self-heals on next access</li>
 *   <li>The resetAt field is set to 0 after reset to indicate no active reset timer</li>
 * </ul>
 *
 * <p><b>Example Usage:</b></p>
 * <pre>
 * // Create a new token with default max of 20 requests per minute
 * Token token = new Token("sk_live_abc123xyz789");
 *
 * // Or specify a custom max (e.g., 100 requests)
 * Token token = new Token("sk_live_abc123xyz789", 100);
 *
 * // Acquire a token slot for a request (non-blocking, returns immediately)
 * token.acquire().thenRun(() -> {
 *     System.out.println("Slot acquired, making API request...");
 *     // Make your API request here
 * }).exceptionally(ex -> {
 *     System.err.println("Failed to acquire: " + ex.getMessage());
 *     return null;
 * });
 *
 * // Update token status after receiving API response headers
 * token.updateFromResponse(15, 1234567890L); // 15 remaining, reset at Unix timestamp
 *
 * // Check remaining requests
 * int remaining = token.getRemaining();
 * System.out.println("Remaining requests: " + remaining);
 *
 * // Get hidden key for safe logging (e.g., "***...xyz789")
 * System.out.println("Token: " + token.getHiddenKey());
 *
 * // Check time until reset
 * long secondsUntilReset = token.secondsUntilReset();
 * System.out.println("Rate limit resets in: " + secondsUntilReset + " seconds");
 * </pre>
 *
 * <p><b>Performance Considerations:</b></p>
 * <ul>
 *   <li>The compareAndSet operation is lock-free and typically faster than synchronization</li>
 *   <li>Waiter queuing uses ConcurrentLinkedQueue for efficient concurrent access</li>
 *   <li>Time checks use Instant.now().getEpochSecond() which is cached by the JVM</li>
 *   <li>Reading current state (getRemaining) performs time-based auto-reset checks</li>
 * </ul>
 *
 * @see TokenPool
 * @see de.shurablack.jima.http.RequestManager
 * @author JIMA Contributors
 */
@Getter
public class Token {

    /**
     * Default API max remaining per API Token
     */
    public static final int DEFAULT_MAX_REMAINING = 20;

    /**
     * The API token/key string (immutable).
     * This is the actual credential used for authentication.
     * All other fields are mutable to track token state changes.
     */
    private final String key;

    /**
     * The number of remaining requests within the current rate limit period.
     * Uses AtomicInteger for thread-safe updates without explicit synchronization.
     * Value is decremented with each acquire() and reset to max when reset time is reached.
     */
    private final AtomicInteger remaining = new AtomicInteger(0);

    /**
     * The maximum number of requests allowed per rate limit period.
     * Typically received from API rate limit headers (e.g., X-RateLimit-Limit).
     * Used to reset the remaining counter when the rate period expires.
     */
    private final AtomicInteger max = new AtomicInteger(0);

    /**
     * The Unix timestamp (seconds since epoch) when the current rate limit period resets.
     * Received from API rate limit headers (e.g., X-RateLimit-Reset).
     * A value of 0 indicates no active reset timer (already reset or not yet set).
     */
    private final AtomicLong resetAt = new AtomicLong(0);

    /**
     * Queue of CompletableFutures representing requests waiting for available quota.
     * These futures complete when a quota slot becomes available after reset or throttle.
     * Uses ConcurrentLinkedQueue to support thread-safe concurrent access.
     */
    private final Queue<CompletableFuture<Void>> waiters = new ConcurrentLinkedQueue<>();

    /**
     * Lock object used to synchronize critical sections during state updates.
     * Protects the atomic check-and-set operations for reset time and remaining quota.
     * Alternative to full object synchronization on specific critical paths.
     */
    private final Object updateLock = new Object();

    /**
     * Creates a new Token with the specified API key and custom maximum quota.
     *
     * <p>This constructor allows specifying a custom rate limit maximum, which
     * is typically determined from the API's rate limit headers.</p>
     *
     * @param key The API token/key string (e.g., "sk_live_xxxxxxxxxxx")
     * @param max The maximum number of requests allowed per rate limit period (e.g., 100)
     * @throws NullPointerException If key parameter is null
     */
    public Token(String key, int max) {
        this.key = key;
        this.max.set(max);
    }

    /**
     * Creates a new Token with the specified API key and default maximum of 20 requests.
     *
     * <p>This is a convenience constructor that uses a default rate limit of 20 requests.
     * This default can be overridden later by calling {@link #updateMax(int)}.</p>
     *
     * @param key The API token/key string (e.g., "sk_live_xxxxxxxxxxx")
     * @throws NullPointerException If key parameter is null
     */
    public Token(String key) {
        this.key = key;
        this.max.set(DEFAULT_MAX_REMAINING);
    }

    /**
     * Attempts to acquire a token slot for making a request.
     *
     * <p><b>Behavior:</b></p>
     * <ul>
     *   <li>If remaining requests > 0: Returns immediately with a completed future</li>
     *   <li>If remaining requests ≤ 0: Queues the request and returns an uncompleted future</li>
     *   <li>Automatically checks if reset time has passed and resets remaining quota</li>
     *   <li>Uses lock-free compareAndSet for decrementing remaining count</li>
     * </ul>
     *
     * <p><b>Thread Safety:</b></p>
     * This method is thread-safe. Multiple threads can safely call this concurrently.
     * The compareAndSet loop ensures only one thread successfully decrements the counter.
     *
     * <p><b>Return Value:</b></p>
     * Returns a CompletableFuture<Void> that:
     * <ul>
     *   <li>Is already completed if a slot is immediately available</li>
     *   <li>Will be completed later when a slot becomes available after reset or throttle</li>
     * </ul>
     *
     * @param usageLimit The minimum number of requests left per rate limit period before queuing (e.g., 0 to queue when exhausted)
     * @return A CompletableFuture that completes when a token slot is acquired
     */
    public CompletableFuture<Void> acquire(int usageLimit) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        tryAcquire(future, usageLimit);
        return future;
    }

    /**
     * Internal method to attempt acquiring a token slot or queue the request.
     *
     * <p><b>Algorithm:</b></p>
     * <ol>
     *   <li>Check if reset time has passed; if so, reset remaining to max</li>
     *   <li>Loop until either acquiring a slot or queueing the request:
     *       <ul>
     *           <li>Get current remaining value</li>
     *           <li>If ≤ 0, add request to waiters queue and return (wait for reset)</li>
     *           <li>If > 0, try to atomically decrement using compareAndSet</li>
     *           <li>If successful, complete the future and return</li>
     *           <li>If failed (contention), retry the loop (CAS spin-lock pattern)</li>
     *       </ul>
     *   </li>
     * </ol>
     *
     * <p><b>Thread Safety:</b></p>
     * The compareAndSet loop implements a spin-lock pattern that is lock-free and efficient.
     * Under contention, the loop retries to update the atomic counter. This is generally
     * more efficient than using explicit synchronization for this operation.
     *
     * <p><b>Reset Logic:</b></p>
     * The synchronized updateLock ensures atomicity when checking and resetting the time.
     * This prevents a race condition where multiple threads could observe the same expired
     * reset time and attempt to reset simultaneously.
     *
     * @param future The CompletableFuture to complete when a slot is available
     * @param usageLimit The minimum number of requests left per rate limit period
     */
    private void tryAcquire(CompletableFuture<Void> future, int usageLimit) {
        long nowSeconds = Instant.now().getEpochSecond();

        synchronized (updateLock) {
            if (resetAt.get() > 0 && nowSeconds >= resetAt.get()) {
                remaining.set(max.get());
                resetAt.set(0);
            }
        }

        while (true) {
            int before = remaining.get();

            if (before <= usageLimit) {
                waiters.add(future);
                return;
            }

            if (remaining.compareAndSet(before, before - 1)) {
                future.complete(null);
                return;
            }
        }
    }

    /**
     * Updates the token status based on API response rate limit headers.
     *
     * <p>This method should be called after each API request to update the token's
     * remaining request count and reset time based on the response headers from the API.</p>
     *
     * <p><b>Usage in RequestManager:</b></p>
     * The RequestManager typically calls this method after receiving an HTTP response:
     * <pre>
     * int remaining = response.headers().firstValue("X-RateLimit-Remaining");
     * long resetAt = response.headers().firstValue("X-RateLimit-Reset");
     * token.updateFromResponse(remaining, resetAt);
     * </pre>
     *
     * <p><b>Side Effects:</b></p>
     * After updating the state, any queued requests are processed via {@link #drainWaiters()}.
     * Waiters are completed if the newly available quota allows it.
     *
     * <p><b>Thread Safety:</b></p>
     * This method is thread-safe due to synchronized access to the update lock during state changes.
     *
     * @param newRemaining The number of remaining requests from the API response header
     * @param newResetAt The Unix timestamp when the rate limit resets from the API response header
     */
    public void updateFromResponse(int newRemaining, long newResetAt) {
        synchronized (updateLock) {
            remaining.set(newRemaining);
            resetAt.set(newResetAt);
        }

        drainWaiters();
    }

    /**
     * Updates only the reset time of the token.
     *
     * <p>This method can be used to update the reset time without changing the remaining count.
     * Useful when the reset time needs to be updated independently, such as from
     * a rate limit header that arrives separately from the remaining count.</p>
     *
     * <p><b>Side Effects:</b></p>
     * After updating, queued requests are processed via {@link #drainWaiters()}.
     *
     * @param newResetAt The Unix timestamp when the rate limit resets
     */
    public void updateFromResponse(long newResetAt) {
        synchronized (updateLock) {
            resetAt.set(newResetAt);
        }

        drainWaiters();
    }

    /**
     * Updates the maximum number of requests allowed per rate limit period.
     *
     * <p>This method is typically called during token initialization to set the rate limit
     * maximum from the API's rate limit headers (X-RateLimit-Limit header).</p>
     *
     * <p><b>Thread Safety:</b></p>
     * This method is thread-safe due to synchronized access to the update lock.
     *
     * @param newMax The new maximum quota (e.g., 100 for 100 requests per period)
     */
    public void updateMax(int newMax) {
        synchronized (updateLock) {
            max.set(newMax);
        }
    }

    /**
     * Internal method to drain the queue of waiting requests when slots become available.
     *
     * <p><b>Algorithm:</b></p>
     * <ol>
     *   <li>While waiters queue is not empty:
     *       <ul>
     *           <li>Peek at the first waiter in queue (without removing)</li>
     *           <li>If queue is empty, stop</li>
     *           <li>Check if remaining quota is available (> 0)</li>
     *           <li>If not available, stop (cannot fulfill more waiters)</li>
     *           <li>Try to atomically decrement remaining using compareAndSet</li>
     *           <li>If successful: poll and complete the waiter, continue to next</li>
     *           <li>If failed (contention): retry the compareAndSet for same waiter</li>
     *       </ul>
     *   </li>
     * </ol>
     *
     * <p><b>Purpose:</b></p>
     * This method is called after updating the token state (remaining or reset time).
     * It attempts to fulfill as many queued requests as possible with the newly available quota.
     *
     * <p><b>Thread Safety:</b></p>
     * Uses the same lock-free compareAndSet pattern as tryAcquire. Multiple threads can
     * call this concurrently; only one will successfully decrement the counter at a time.
     */
    private void drainWaiters() {
        while (true) {
            CompletableFuture<Void> waiter = waiters.peek();
            if (waiter == null) return;

            int before = remaining.get();
            if (before <= 0) return;

            if (remaining.compareAndSet(before, before - 1)) {
                waiters.poll();
                waiter.complete(null);
            }
        }
    }

    /**
     * Gets a masked representation of the token key for safe logging and display.
     *
     * <p><b>Masking Algorithm:</b></p>
     * Shows only the last 10 characters of the token, with all other characters replaced by asterisks.
     *
     * <p><b>Examples:</b></p>
     * <ul>
     *   <li>Input: "sk_live_1234567890abcdefghij" (28 chars)</li>
     *   <li>Output: "*****************abcdefghij" (18 asterisks + last 10 chars)</li>
     * </ul>
     *
     * <p><b>Use Case:</b></p>
     * This method is used in logging, debugging, and UI display to avoid exposing
     * the full API token in logs or error messages, while still providing enough
     * information to identify which token is being used.
     *
     * <p><b>Security Note:</b></p>
     * While this provides obfuscation, it should not be relied upon as the sole security measure.
     * Always ensure tokens are not logged in production environments without careful consideration.
     *
     * @return The hidden/masked representation of the token (asterisks + last 10 characters)
     */
    public String getMaskedKey() {
        if (key.length() < 10) {
            return "*".repeat(key.length());
        }
        return "*".repeat(key.length() - 10) + key.substring(key.length() - 10);
    }

    /**
     * Gets the current number of remaining requests for this token.
     *
     * <p><b>Behavior:</b></p>
     * <ul>
     *   <li>Automatically checks if the reset time has passed</li>
     *   <li>If reset time is reached, remaining is reset to max</li>
     *   <li>Returns the current remaining quota after potential auto-reset</li>
     * </ul>
     *
     * <p><b>Thread Safety:</b></p>
     * This method is thread-safe. It uses synchronized block to protect the time check
     * and reset operation. Multiple threads can safely call this concurrently.
     *
     * <p><b>Performance:</b></p>
     * Under normal conditions (no reset needed), this method runs in O(1) time without
     * blocking. Only when reset needs to occur is the synchronized lock acquired.
     *
     * @return The number of remaining requests available for this token (0 or positive)
     */
    public int getRemaining() {
        long nowSeconds = Instant.now().getEpochSecond();

        synchronized (updateLock) {
            if (resetAt.get() > 0 && nowSeconds >= resetAt.get()) {
                remaining.set(max.get());
                resetAt.set(0);
            }
        }

        return remaining.get();
    }

    /**
     * Gets the number of seconds until the rate limit resets.
     *
     * <p><b>Return Values:</b></p>
     * <ul>
     *   <li>Positive integer: Number of seconds until reset occurs</li>
     *   <li>Zero: Reset time has already passed or reset time not set</li>
     * </ul>
     *
     * <p><b>Performance:</b></p>
     * This method runs in O(1) time with no synchronization.
     * It performs a simple subtraction of current time from resetAt timestamp.
     *
     * <p><b>Use Cases:</b></p>
     * <ul>
     *   <li>Determining how long to wait before making next request attempt</li>
     *   <li>Logging or monitoring rate limit status</li>
     *   <li>Implementing backoff strategies</li>
     * </ul>
     *
     * @return Number of seconds until rate limit resets (0 if already reset or not set)
     */
    public long secondsUntilReset() {
        return Math.max(0, resetAt.get() - Instant.now().getEpochSecond());
    }
}