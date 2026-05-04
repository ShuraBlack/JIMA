package de.shurablack.jima.http;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread-safe metrics tracking for HTTP request statistics.
 *
 * <p><b>Overview:</b></p>
 * RequestMetric collects and tracks statistics about HTTP requests made by the JIMA library.
 * It maintains counters for total requests, retries, and failures using atomic operations
 * to ensure thread-safe concurrent access without explicit synchronization.
 *
 * <p><b>Features:</b></p>
 * <ul>
 *   <li><b>Thread-Safe Counters:</b> Uses AtomicLong for lock-free concurrent updates</li>
 *   <li><b>Simple Increment Operations:</b> Easy-to-use methods for tracking metrics</li>
 *   <li><b>Snapshot Capability:</b> Creates immutable snapshots of current state</li>
 *   <li><b>Zero Initial State:</b> All counters start at zero</li>
 * </ul>
 *
 * <p><b>Metrics Tracked:</b></p>
 * <ul>
 *   <li><b>Total Requests:</b> Count of all HTTP requests sent (including retries)</li>
 *   <li><b>Retries:</b> Count of requests that had to be retried (HTTP 429, timeouts, etc.)</li>
 *   <li><b>Failures:</b> Count of requests that ultimately failed after retries</li>
 * </ul>
 *
 * <p><b>Thread Safety:</b></p>
 * This class is fully thread-safe. All counter increments are atomic operations that:
 * <ul>
 *   <li>Do not require explicit synchronization</li>
 *   <li>Guarantee visibility across threads</li>
 *   <li>Prevent lost updates from concurrent threads</li>
 *   <li>Efficient under high contention</li>
 * </ul>
 *
 * @see RequestMetricSnapshot
 * @see de.shurablack.jima.http.RequestManager
 */
public class RequestMetric {

    /**
     * Total number of HTTP requests attempted (including retries).
     * Uses AtomicLong for thread-safe increment operations without synchronization.
     */
    private final AtomicLong totalRequests;

    /**
     * Number of times a request had to be retried.
     * Incremented when a request fails and is scheduled for retry
     * (e.g., HTTP 429 rate limit, timeout, temporary network error).
     */
    private final AtomicLong retries;

    /**
     * Number of requests that ultimately failed after all retries.
     * Only incremented when a request is abandoned (not retried again).
     */
    private final AtomicLong failures;

    /**
     * Creates a new RequestMetric instance with all counters initialized to zero.
     *
     * <p>This constructor initializes three AtomicLong counters for tracking
     * total requests, retries, and failures. The metric is ready to track
     * statistics immediately after creation.</p>
     */
    public RequestMetric() {
        totalRequests = new AtomicLong(0);
        retries = new AtomicLong(0);
        failures = new AtomicLong(0);
    }

    /**
     * Increments the total request counter by one.
     *
     * <p>This method is called when an HTTP request is sent to the API,
     * regardless of whether it succeeds or fails. This includes both
     * initial requests and retried requests.</p>
     *
     * <p><b>Thread Safety:</b></p>
     * This operation is atomic and thread-safe. Multiple threads can safely
     * call this method concurrently without data corruption or lost updates.
     *
     * <p><b>Usage:</b></p>
     * Called by RequestManager after sending each HTTP request.
     *
     * @see #getSnapshot()
     */
    public void incrementTotalRequests() {
        totalRequests.incrementAndGet();
    }

    /**
     * Increments the retry counter by one.
     *
     * <p>This method is called when a request is scheduled for retry due to:
     * <ul>
     *   <li>HTTP 429 (Too Many Requests / Rate Limited)</li>
     *   <li>HTTP 5xx (Server errors)</li>
     *   <li>Network timeouts or connection errors</li>
     *   <li>Other temporary failures that warrant retry</li>
     * </ul>
     * </p>
     *
     * <p><b>Thread Safety:</b></p>
     * This operation is atomic and thread-safe. Multiple threads can safely
     * call this method concurrently.
     *
     * <p><b>Note:</b></p>
     * A retried request will also increment the totalRequests counter
     * (since it's a new attempt), so retries <= totalRequests always.
     *
     * @see #incrementTotalRequests()
     * @see #getSnapshot()
     */
    public void incrementRetries() {
        retries.incrementAndGet();
    }

    /**
     * Increments the failure counter by one.
     *
     * <p>This method is called when a request ultimately fails and will not
     * be retried, including cases where:
     * <ul>
     *   <li>Maximum retry attempts have been exceeded</li>
     *   <li>A permanent error occurs (HTTP 4xx auth errors, etc.)</li>
     *   <li>The request is cancelled or aborted</li>
     *   <li>The application is shutting down</li>
     * </ul>
     * </p>
     *
     * <p><b>Thread Safety:</b></p>
     * This operation is atomic and thread-safe. Multiple threads can safely
     * call this method concurrently.
     *
     * <p><b>Note:</b></p>
     * A failed request won't increment retries (unless it was retried but
     * ultimately failed). failures <= totalRequests always.
     *
     * @see #getSnapshot()
     */
    public void incrementFailures() {
        failures.incrementAndGet();
    }

    /**
     * Creates an immutable snapshot of the current metric values.
     *
     * <p><b>Purpose:</b></p>
     * Returns a snapshot (RequestMetricSnapshot) containing the current values
     * of all three counters at the moment this method is called. The snapshot
     * is immutable, providing a consistent view of metrics that won't change
     * even if other threads continue updating the counters.</p>
     *
     * <p><b>Usage:</b></p>
     * Use this method when you need to log, report, or analyze the current metrics.
     * <pre>
     * RequestMetric.RequestMetricSnapshot snapshot = metrics.getSnapshot();
     * System.out.println("Total Requests: " + snapshot.getTotalRequests());
     * System.out.println("Retries: " + snapshot.getRetries());
     * System.out.println("Failures: " + snapshot.getFailures());
     * </pre>
     * </p>
     *
     * <p><b>Thread Safety:</b></p>
     * This method is thread-safe. Concurrent calls from multiple threads will
     * each get an accurate snapshot of the current state (though different
     * threads may see slightly different values depending on timing).</p>
     *
     * <p><b>Performance:</b></p>
     * Creating a snapshot is very efficient - it simply reads the current
     * values from the atomic counters and wraps them in an immutable object.
     * It does not block or affect the atomic counters in any way.</p>
     *
     * @return An immutable RequestMetricSnapshot containing current counter values
     * @see RequestMetricSnapshot
     */
    public RequestMetricSnapshot getSnapshot() {
        return new RequestMetricSnapshot(totalRequests.get(), retries.get(), failures.get());
    }

    /**
     * Immutable snapshot of request metrics at a specific point in time.
     *
     * <p><b>Overview:</b></p>
     * RequestMetricSnapshot represents a frozen view of the metrics collected
     * by a RequestMetric instance. Once created, the values never change,
     * making it safe to share between threads and use for reports or comparisons.</p>
     *
     * <p><b>Properties:</b></p>
     * <ul>
     *   <li><b>Immutable:</b> All fields are final and publicly read-only via Lombok @Getter</li>
     *   <li><b>Thread-Safe:</b> Can be safely shared and accessed from multiple threads</li>
     *   <li><b>Lightweight:</b> Contains only three long values</li>
     * </ul>
     *
     * <p><b>Usage Patterns:</b></p>
     * <pre>
     * // Get and analyze metrics
     * RequestMetric.RequestMetricSnapshot snapshot = metrics.getSnapshot();
     *
     * // Access individual metrics
     * long total = snapshot.getTotalRequests();
     * long retried = snapshot.getRetries();
     * long failed = snapshot.getFailures();
     *
     * // Calculate derived metrics
     * long successful = total - failed;
     * double successRate = (successful / (double) total) * 100;
     * double retryRate = (retried / (double) total) * 100;
     *
     * // Log for monitoring/debugging
     * LOGGER.info("Requests - Total: {}, Successful: {}, Failed: {}, Retries: {}",
     *     total, successful, failed, retried);
     * </pre>
     * </p>
     *
     * <p><b>Derived Metrics:</b></p>
     * From a snapshot, you can calculate:
     * <ul>
     *   <li><b>Successful Requests:</b> totalRequests - failures</li>
     *   <li><b>Success Rate:</b> (totalRequests - failures) / totalRequests * 100 %</li>
     *   <li><b>Failure Rate:</b> failures / totalRequests * 100 %</li>
     *   <li><b>Retry Rate:</b> retries / totalRequests * 100 %</li>
     * </ul>
     * </p>
     *
     * @see RequestMetric
     * @author JIMA Contributors
     * @version 2.1.0
     */
    @Getter
    @AllArgsConstructor
    public static class RequestMetricSnapshot {

        /**
         * Total number of HTTP requests that were attempted.
         * This is the sum of both successful requests and requests that were
         * ultimately failed. It includes all retried attempts.
         */
        private final long totalRequests;

        /**
         * Number of times a request was retried.
         * This represents how many requests required more than one attempt.
         * Always less than or equal to totalRequests.
         */
        private final long retries;

        /**
         * Number of requests that ultimately failed after retries exhausted.
         * These are requests that were not successful and were not retried
         * further (either max retries reached or permanent error).
         * Always less than or equal to totalRequests.
         */
        private final long failures;

    }

}