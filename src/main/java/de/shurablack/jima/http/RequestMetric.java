package de.shurablack.jima.http;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread-safe metrics tracking for HTTP request statistics.
 *
 * <p><b>Overview:</b></p>
 * RequestMetric collects and tracks statistics about HTTP requests made by the JIMA library.
 * It maintains counters for in-flight, total requests, retries, and failures using atomic operations
 * to ensure thread-safe concurrent access without explicit synchronization.
 *
 * <p><b>Metrics Tracked:</b></p>
 * <ul>
 *   <li><b>In-flight: Count of currently processing requests</b></li>
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
     * Number of HTTP requests currently in flight (being processed).
     * Incremented when a request is sent and decremented when the response is received.
     * Uses AtomicLong for thread-safe increment and decrement operations without synchronization.
     */
    private final AtomicLong inFlight;

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
     */
    public RequestMetric() {
        inFlight = new AtomicLong(0);
        totalRequests = new AtomicLong(0);
        retries = new AtomicLong(0);
        failures = new AtomicLong(0);
    }

    /**
     * Increments the in-flight request counter by one.
     *
     * @see #getSnapshot()
     */
    public void incrementInFlight() {
        inFlight.incrementAndGet();
    }

    /**
     * Decrements the in-fight request counter by one.
     */
    public void decrementInFlight() {
        inFlight.decrementAndGet();
    }

    /**
     * Increments the total request counter by one.
     *
     * <p>This method is called when an HTTP request is sent to the API,
     * regardless of whether it succeeds or fails. This includes both
     * initial requests and retried requests.</p>
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
     *
     * <p><b>Note:</b> A retried request will also increment the totalRequests counter
     * (since it's a new attempt), so retries &lt;= totalRequests always.</p>
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
     *
     * <p><b>Note:</b> A failed request won't increment retries (unless it was retried but
     * ultimately failed). failures &lt;= totalRequests always.</p>
     *
     * @see #getSnapshot()
     */
    public void incrementFailures() {
        failures.incrementAndGet();
    }

     /**
      * Creates an immutable snapshot of the current metric values.
      *
      * <p><b>Purpose:</b> Returns a snapshot (RequestMetricSnapshot) containing the current values
      * of all counters at the moment this method is called. The snapshot
      * is immutable, providing a consistent view of metrics that won't change
      * even if other threads continue updating the counters.</p>
      *
      * <p><b>Usage:</b> Use this method when you need to log, report, or analyze the current metrics.</p>
      * <pre>
      * RequestMetric.RequestMetricSnapshot snapshot = metrics.getSnapshot();
      * System.out.println("Total Requests: " + snapshot.getTotalRequests());
      * System.out.println("Retries: " + snapshot.getRetries());
      * System.out.println("Failures: " + snapshot.getFailures());
      * </pre>
      *
      * <p><b>Thread Safety:</b> This method is thread-safe. Concurrent calls from multiple threads will
      * each get an accurate snapshot of the current state (though different
      * threads may see slightly different values depending on timing).</p>
      *
      * <p><b>Performance:</b> Creating a snapshot is very efficient - it simply reads the current
      * values from the atomic counters and wraps them in an immutable object.
      * It does not block or affect the atomic counters in any way.</p>
      *
      * @return An immutable RequestMetricSnapshot containing current counter values
      * @see RequestMetricSnapshot
      */
    public RequestMetricSnapshot getSnapshot() {
        return new RequestMetricSnapshot(inFlight.get(), totalRequests.get(), retries.get(), failures.get());
    }

     /**
      * Immutable snapshot of request metrics at a specific point in time.
      *
      * <p><b>Overview:</b> RequestMetricSnapshot represents a frozen view of the metrics collected
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
      * <p><b>Derived Metrics:</b> From a snapshot, you can calculate:</p>
      * <ul>
      *   <li><b>Successful Requests:</b> totalRequests - failures</li>
      *   <li><b>Success Rate:</b> (totalRequests - failures) / totalRequests * 100 %</li>
      *   <li><b>Failure Rate:</b> failures / totalRequests * 100 %</li>
      *   <li><b>Retry Rate:</b> retries / totalRequests * 100 %</li>
      * </ul>
      *
      * @see RequestMetric
      */
    @Getter
    @AllArgsConstructor
    public static class RequestMetricSnapshot {

        /**
         * Number of HTTP requests currently in flight (being processed).
         */
        private final long inFlight;

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