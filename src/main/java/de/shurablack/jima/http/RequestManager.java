package de.shurablack.jima.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import de.shurablack.jima.http.serialization.ApiObjectMapper;
import de.shurablack.jima.model.EndpointUpdate;
import de.shurablack.jima.util.Configurator;
import de.shurablack.jima.util.TokenStore;
import lombok.Getter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * Singleton class responsible for handling HTTP requests with rate-limiting and queuing.
 * It provides capability to enqueue requests, process them asynchronously, and handle rate limits.
 */
public class RequestManager {

    private static final Logger LOGGER = LogManager.getLogger(RequestManager.class);
    private static final RequestManager INSTANCE = new RequestManager();

    private volatile boolean shuttingDown = false;
    private final Set<CompletableFuture<?>> pendingRequests = ConcurrentHashMap.newKeySet();
    private final BlockingQueue<RequestGroup> groupQueue = new LinkedBlockingQueue<>();

    private static final String X_RATE_LIMIT_REMAINING = "X-RateLimit-Remaining";
    private static final String X_RATE_LIMIT_RESET = "X-RateLimit-Reset";

    private final HttpClient client = HttpClient.newHttpClient();
    private static Cache<String, EndpointUpdate> ENDPOINT_CACHE;
    private static final ConcurrentHashMap<String, CompletableFuture<?>> IN_FLIGHT = new ConcurrentHashMap<>();

    @Getter
    private final ObjectMapper mapper = new ApiObjectMapper();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final ExecutorService groupExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "JIMA-RequestGroupProcessor");
        t.setDaemon(true);
        return t;
    });

    private long usageLimit = 0;

    /**
     * Private constructor to enforce singleton pattern.
     * Initializes the Configurator and sets up a shutdown hook to cleanly stop the scheduler.
     */
    private RequestManager() {
        Configurator.getInstance(); // Ensure Configurator is initialized
        
        // Start request group processor
        groupExecutor.execute(this::processRequestGroups);
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Shutdown detected – stopping RequestManager...");
            shuttingDown = true;

            // Stop group executor
            groupExecutor.shutdownNow();
            
            // Scheduler stoppen
            scheduler.shutdownNow();

            // Alle offenen Requests abbrechen
            pendingRequests.forEach(f -> f.completeExceptionally(
                    new CancellationException("Application shutting down")
            ));
            pendingRequests.clear();
        }));
    }

    /**
     * Retrieves the singleton instance of the RequestManager.
     *
     * @return The singleton instance of RequestManager.
     */
    public static RequestManager getInstance() {
        return INSTANCE;
    }

    /**
     * Sets the log level for the RequestManager.
     *
     * @param level The log level to set (e.g., DEBUG, INFO, WARN, ERROR).
     */
    public static void setLogLevel(Level level) {
        org.apache.logging.log4j.core.config.Configurator.setLevel(RequestManager.class, level);
        LogManager.getLogger(LogManager.class).info("Log level set to {}", level);
    }


    /**
     * Enables endpoint caching with optional statistics recording.
     *
     * @param recordStats If true, cache statistics (hits, misses, evictions) are recorded for monitoring.
     */
    public static void enableEndpointCaching(boolean recordStats) {
         ENDPOINT_CACHE = recordStats
                ? Caffeine.newBuilder().recordStats().build()
                : Caffeine.newBuilder().build();
    }

    /**
     * Retrieves cache statistics for the endpoint cache.
     *
     * @return The CacheStats containing hit counts, miss counts, and eviction data.
     * @throws IllegalStateException if endpoint caching has not been enabled via {@link #enableEndpointCaching(boolean)}.
     */
    public static CacheStats getCacheRecords() {
        if (ENDPOINT_CACHE == null) {
            throw new IllegalStateException("Endpoint caching is not enabled");
        }

        return ENDPOINT_CACHE.stats();
    }

    /**
     * Retrieves cached endpoint data if available and not expired.
     * Returns null if caching is disabled, type is incompatible, cache miss occurs, or data is expired.
     *
     * @param url  The cache key (endpoint URL).
     * @param type The expected response data type.
     * @param <T>  The type of the cached data.
     * @return The cached data if valid and not expired, otherwise null.
     */
    private <T> T getCacheData(String url, Class<T> type) {
        if (ENDPOINT_CACHE == null) {
            return null;
        }

        if (!EndpointUpdate.class.isAssignableFrom(type)) {
            return null;
        }

        EndpointUpdate value = ENDPOINT_CACHE.getIfPresent(url);
        if (value == null || value.isExpired(LocalDateTime.now())) {
            return null;
        }

        return (T) value;
    }

    /**
     * Sets the usage limit for requests.
     * The usage limit must be a non-negative value.
     *
     * @param minimum The minimum number of requests allowed to be left.
     * @throws IllegalArgumentException if the minimum is negative.
     */
    public void setUsageLimit(long minimum) {
        if (minimum < 0) {
            throw new IllegalArgumentException("Usage limit must be non-negative");
        }
        this.usageLimit = minimum;
    }

    /**
     * Enqueues an HTTP request to the specified endpoint with given query parameters and request parameters.
     *
     * @param endpoint  The API endpoint to send the request to.
     * @param query     A map of query parameters to be included in the URL.
     * @param parameter A map of request parameters to be included in the URL.
     * @param type      The class type of the expected response data.
     * @param <T>       The type of the response data.
     * @return A CompletableFuture that will complete with the response data.
     */
    public <T> CompletableFuture<Response<T>> enqueueRequest(Endpoint endpoint, Map<String, String> query, Map<String, String> parameter, Class<T> type) {
        return enqueueRequest(endpoint, query, parameter, type, TokenStore.getInstance().getToken());
    }

    /**
     * Enqueues an HTTP request for the given endpoint using a specific authorization token.
     * <p>
     * This overload lets callers provide an explicit token (for example when managing
     * multiple tokens or when testing). The method validates the token, builds the
     * request URL (replacing path placeholders and appending query parameters), checks
     * the endpoint cache for a valid cached response, and if none is found delegates to
     * {@link #sendRequest(String, Class, String)} to perform the actual network call.
     * </p>
     *
     * @param endpoint  The API endpoint to send the request to.
     * @param query     Map of path parameters to replace in the endpoint path (may be null).
     * @param parameter Map of URL query parameters to append to the request (may be null).
     * @param type      The expected response data type used for JSON deserialization.
     * @param token     The bearer token to use for authorization. If null, an immediate
     *                  completed Response with {@link ResponseCode#UNAUTHORIZED} is returned.
     * @param <T>       The type of the response data.
     * @return A CompletableFuture that will complete with a {@link Response} containing
     * the deserialized data on success or an error code/message on failure.
     */
    public <T> CompletableFuture<Response<T>> enqueueRequest(
            Endpoint endpoint,
            Map<String, String> query,
            Map<String, String> parameter,
            Class<T> type,
            String token
    ) {
        if (token == null) {
            return CompletableFuture.completedFuture(
                    new Response<>(ResponseCode.UNAUTHORIZED, null, "No valid token available")
            );
        }

        String url = buildUrl(endpoint, query, parameter);
        T cached = getCacheData(url, type);
        if (cached != null) {
            return CompletableFuture.completedFuture(
                    new Response<>(ResponseCode.SUCCESS, cached, null)
            );
        }

        return sendRequest(url, type, token);
    }


    /**
     * Sends an HTTP request while de-duplicating concurrent calls for the same URL.
     * If a request for the same URL is already in progress, the existing future is reused.
     * Otherwise, the request is executed asynchronously, the response is processed,
     * and the result is cached when applicable.
     *
     * @param url   The request URL.
     * @param type  The expected response type.
     * @param token The authorization token.
     * @param <T>   The response payload type.
     * @return A future that completes with the processed response.
     */
    private <T> CompletableFuture<Response<T>> sendRequest(String url, Class<T> type, String token) {
        CompletableFuture<?> inFlight = IN_FLIGHT.get(url);
        if (inFlight != null) {
            return (CompletableFuture<Response<T>>) inFlight;
        }

        CompletableFuture<Response<T>> future = new CompletableFuture<>();

        CompletableFuture<?> existing = IN_FLIGHT.putIfAbsent(url, future);
        if (existing != null) {
            return (CompletableFuture<Response<T>>) existing;
        }

        if (shuttingDown) {
            IN_FLIGHT.remove(url);
            return CompletableFuture.completedFuture(
                    new Response<>(ResponseCode.BAD_REQUEST, null, "Application shutting down")
            );
        }

        pendingRequests.add(future);

        HttpRequest request = buildRequest(url, token);

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenCompose(response -> {
                    if (shuttingDown) {
                        return CompletableFuture.failedFuture(
                                new CancellationException("Application shutting down")
                        );
                    }
                    return handleResponse(response, url, type, token);
                })
                .whenComplete((result, ex) -> {
                    pendingRequests.remove(future);
                    IN_FLIGHT.remove(url);

                    if (ex != null) {
                        future.completeExceptionally(ex);
                    } else {
                        future.complete(result);

                        if (ENDPOINT_CACHE != null && result.isSuccessful() && result.getData() instanceof EndpointUpdate) {
                            EndpointUpdate update = (EndpointUpdate) result.getData();
                            ENDPOINT_CACHE.put(url, update);
                            LOGGER.debug("Cached endpoint update for URL: {}", url);
                        }
                    }
                });

        return future;
    }


    /**
     * Builds an HTTP GET request with the specified URL and authorization token.
     *
     * @param url   The URL to send the request to.
     * @param token The authorization token to be included in the request headers.
     * @return The constructed HttpRequest object.
     */
    private HttpRequest buildRequest(String url, String token) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .setHeader("Accept", "application/json")
                .setHeader("User-Agent", getUserAgent())
                .setHeader("Authorization", "Bearer " + token)
                .GET();

        return builder.build();
    }

    /**
     * Handles the HTTP response, processes rate-limiting headers, and schedules retries if necessary.
     *
     * @param response The HTTP response received.
     * @param url      The URL of the request.
     * @param type     The class type of the expected response data.
     * @param token    The authorization token used in the request.
     * @param <T>      The type of the response data.
     * @return A CompletableFuture that will complete with the processed response data.
     */
    private <T> CompletableFuture<Response<T>> handleResponse(HttpResponse<String> response, String url, Class<T> type, String token) {
        try {
            String remaining = response.headers().firstValue(X_RATE_LIMIT_REMAINING).orElse("-1");
            long reset = response.headers().firstValue(X_RATE_LIMIT_RESET)
                .flatMap(value -> {
                    try {
                        return Optional.of(Long.parseLong(value));
                    } catch (NumberFormatException e) {
                        return Optional.empty();
                    }
                })
                .orElseGet(() -> Instant.now().getEpochSecond() + 60);


            TokenStore.getInstance().updateToken(token, Integer.parseInt(remaining), reset);

            if (Long.parseLong(remaining) < usageLimit) {
                LOGGER.warn("Usage limit of {} requests reached ({} remaining). Scheduling retry...", usageLimit, remaining);
                return scheduleRetry(url, type, token, reset);
            }

            if (response.statusCode() == 429) {
                LOGGER.warn("Received 429 Too Many Requests. Scheduling retry...");
                return scheduleRetry(url, type, token, reset);
            }

            if ("0".equals(remaining)) {
                LOGGER.warn("Rate limit reached! Scheduling retry...");
                return scheduleRetry(url, type, token, reset);
            }

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                T data = mapper.readValue(response.body(), type);
                return CompletableFuture.completedFuture(new Response<>(ResponseCode.fromCode(response.statusCode()), data, null));
            } else {
                return CompletableFuture.completedFuture(new Response<>(ResponseCode.fromCode(response.statusCode()), null, response.body()));
            }
        } catch (Exception e) {
            return CompletableFuture.completedFuture(new Response<>(ResponseCode.BAD_REQUEST, null, e.getMessage()));
        }
    }

    /**
     * Schedules a retry for the HTTP request after the rate limit reset time.
     *
     * @param url   The URL of the request.
     * @param type  The class type of the expected response data.
     * @param token The authorization token used in the request.
     * @param reset The rate limit reset time in seconds since the epoch.
     * @param <T>   The type of the response data.
     * @return A CompletableFuture that will complete with the response data after the retry.
     */
    private <T> CompletableFuture<Response<T>> scheduleRetry(String url, Class<T> type, String token, long reset) {
        long delay = Math.max(10, reset - Instant.now().getEpochSecond() + 1);
        LOGGER.info("Scheduling retry in {} second/s for URL: {}", delay, url);
        CompletableFuture<Response<T>> future = new CompletableFuture<>();
        scheduler.schedule(() -> sendRequest(url, type, token).thenAccept(future::complete), delay, TimeUnit.SECONDS);
        return future;
    }

    /**
     * Gets the number of requests currently pending (in-flight).
     * Useful for monitoring and determining if it's safe to shutdown.
     *
     * @return The number of pending requests.
     */
    public int getPendingRequestCount() {
        return pendingRequests.size();
    }

    /**
     * Waits for all pending requests to complete with a specified timeout.
     * Useful for graceful shutdown to ensure all in-flight requests finish before terminating.
     *
     * @param timeout The maximum time to wait for pending requests to complete.
     * @param unit    The time unit of the timeout parameter.
     * @return true if all pending requests completed within the timeout, false if timeout was exceeded.
     * @throws InterruptedException if the waiting thread is interrupted.
     */
    public boolean waitForPending(long timeout, TimeUnit unit) throws InterruptedException {
        long deadline = System.currentTimeMillis() + unit.toMillis(timeout);
        while (!pendingRequests.isEmpty() && System.currentTimeMillis() < deadline) {
            Thread.sleep(100);
        }
        return pendingRequests.isEmpty();
    }

    /**
     * Builds the full URL for the request by replacing placeholders and appending query parameters.
     *
     * @param endpoint  The API endpoint to send the request to.
     * @param query     A map of query parameters to be included in the URL.
     * @param parameter A map of request parameters to be included in the URL.
     * @return The constructed URL as a string.
     */
    private String buildUrl(Endpoint endpoint, Map<String, String> query, Map<String, String> parameter) {
        String url = endpoint.getPath();
        if (query != null && !query.isEmpty()) {
            for (Map.Entry<String, String> entry : query.entrySet()) {
                url = url.replace("{" + entry.getKey() + "}", URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            }
        }
        StringBuilder fullUrl = new StringBuilder(url);
        if (parameter != null && !parameter.isEmpty()) {
            fullUrl.append("?");
            parameter.forEach((key, value) -> fullUrl.append(key).append("=").append(URLEncoder.encode(value, StandardCharsets.UTF_8)).append("&"));
            fullUrl.setLength(fullUrl.length() - 1);
        }
        return fullUrl.toString();
    }

    /**
     * Retrieves the User-Agent string for the HTTP requests.
     *
     * @return The User-Agent string.
     */
    private String getUserAgent() {
        return Configurator.getInstance().get("APPLICATION_NAME") + "/" +
                Configurator.getInstance().get("APPLICATION_VERSION") + " (Contact: " +
                Configurator.getInstance().get("CONTACT_EMAIL") + ")";
    }

    /**
     * Enqueues a group of requests to be executed with controlled delays and token management.
     * Requests are processed sequentially with automatic stalling when tokens drop below the threshold.
     *
     * <p><b>How It Works:</b></p>
     * <ul>
     *   <li>Submits the request group to the internal queue</li>
     *   <li>Background processor handles sequential execution</li>
     *   <li>Each request stalls if tokens drop below minimum</li>
     *   <li>Delays are applied between requests</li>
     * </ul>
     *
     * @param group The RequestGroup containing requests and configuration
     * @return A list of CompletableFutures for each request in the group
     */
    public List<CompletableFuture<?>> enqueueRequestGroup(RequestGroup group) {
        LOGGER.info("Enqueueing request group '{}' with {} requests, delay={}ms, minTokens={}",
                group.getGroupId(), group.size(), group.getDelayMs(), group.getMinTokensAllowed());
        
        try {
            groupQueue.put(group);
        } catch (InterruptedException e) {
            LOGGER.error("Failed to enqueue request group: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
        
        return group.getFutures();
    }

    /**
     * Background process that handles sequential execution of request groups with stalling.
     * Runs continuously until shutdown, monitoring token availability and applying delays.
     */
    private void processRequestGroups() {
        while (!shuttingDown) {
            try {
                RequestGroup group = groupQueue.poll(1, TimeUnit.SECONDS);
                if (group == null) continue;

                if (group.isCancelled()) {
                    LOGGER.warn("Skipping cancelled request group '{}'", group.getGroupId());
                    continue;
                }

                LOGGER.debug("Processing request group '{}'", group.getGroupId());
                processGroup(group);

            } catch (InterruptedException e) {
                if (!shuttingDown) {
                    LOGGER.warn("RequestGroup processor interrupted: {}", e.getMessage());
                }
                Thread.currentThread().interrupt();
                break;
            }
        }
        LOGGER.info("RequestGroup processor stopped");
    }

    /**
     * Processes a single request group sequentially with batching support and token stalling.
     *
     * @param group The request group to process
     */
    private void processGroup(RequestGroup group) {
        List<Supplier<CompletableFuture<?>>> requests = group.getRequests();
        int batchSize = group.getBatchSize();
        
        // Process requests in batches
        for (int batchStart = 0; batchStart < requests.size(); batchStart += batchSize) {
            if (group.isCancelled()) {
                LOGGER.warn("Request group '{}' cancelled, stopping processing", group.getGroupId());
                break;
            }

            int batchEnd = Math.min(batchStart + batchSize, requests.size());
            int batchNumber = (batchStart / batchSize) + 1;
            int totalBatches = (requests.size() + batchSize - 1) / batchSize;

            LOGGER.info("Processing batch {} of {} in group '{}' (requests {}-{})",
                    batchNumber, totalBatches, group.getGroupId(), batchStart + 1, batchEnd);

            // Process each request in this batch
            for (int i = batchStart; i < batchEnd; i++) {
                if (group.isCancelled()) {
                    LOGGER.warn("Request group '{}' cancelled, stopping processing", group.getGroupId());
                    break;
                }

                // Stall until tokens are available
                if (group.getMinTokensAllowed() > 0) {
                    waitForTokenAvailability(group.getGroupId(), group.getMinTokensAllowed());
                }

                // Execute the request
                try {
                    Supplier<CompletableFuture<?>> requestSupplier = requests.get(i);
                    CompletableFuture<?> future = requestSupplier.get();
                    group.addFuture(future);

                    LOGGER.debug("Executing request {} of {} in group '{}'",
                            i + 1, requests.size(), group.getGroupId());

                    // Wait for request to complete before applying delay
                    future.join();

                } catch (Exception e) {
                    LOGGER.error("Error executing request {} in group '{}': {}",
                            i + 1, group.getGroupId(), e.getMessage());
                    if (group.isCancelled()) break;
                }

                // Apply delay between requests within a batch (except after the last one)
                if (i < batchEnd - 1 && group.getDelayMs() > 0) {
                    try {
                        LOGGER.debug("Applying {}ms delay in group '{}'", group.getDelayMs(), group.getGroupId());
                        Thread.sleep(group.getDelayMs());
                    } catch (InterruptedException e) {
                        LOGGER.warn("Delay interrupted in group '{}': {}", group.getGroupId(), e.getMessage());
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }

            // Apply wait between batches if this is not the last batch
            if (batchEnd < requests.size() && group.getWaitMsBetweenBatches() > 0) {
                try {
                    LOGGER.info("Batch {} completed. Waiting {}ms before next batch in group '{}'",
                            batchNumber, group.getWaitMsBetweenBatches(), group.getGroupId());
                    Thread.sleep(group.getWaitMsBetweenBatches());
                } catch (InterruptedException e) {
                    LOGGER.warn("Batch wait interrupted in group '{}': {}", group.getGroupId(), e.getMessage());
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        LOGGER.info("Request group '{}' processing completed", group.getGroupId());
    }

    /**
     * Waits for token availability until the minimum threshold is met.
     * Blocks until tokens are restored or shutdown is initiated.
     *
     * @param groupId The request group ID (for logging)
     * @param minTokensRequired Minimum number of tokens required
     */
    private void waitForTokenAvailability(String groupId, int minTokensRequired) {
        long startTime = System.currentTimeMillis();
        long maxWaitMs = 60000; // Max 60 second wait
        
        while (!shuttingDown && (System.currentTimeMillis() - startTime) < maxWaitMs) {
            // Try to estimate current token availability by checking TokenStore
            int estimatedTokens = getEstimatedTokenCount();

            if (estimatedTokens >= minTokensRequired) {
                LOGGER.info("Token availability restored for group '{}' ({}>={})",
                        groupId, estimatedTokens, minTokensRequired);
                return;
            }

            LOGGER.warn("Stalling group '{}': tokens {} < minimum {}. Will retry in 5 seconds...",
                    groupId, estimatedTokens, minTokensRequired);

            try {
                Thread.sleep(5000); // Check every 5 seconds
            } catch (InterruptedException e) {
                LOGGER.warn("Token wait interrupted for group '{}': {}", groupId, e.getMessage());
                Thread.currentThread().interrupt();
                return;
            }
        }

        if (System.currentTimeMillis() - startTime >= maxWaitMs) {
            LOGGER.error("Token wait timeout for group '{}' after {}ms. Proceeding anyway.",
                    groupId, maxWaitMs);
        }
    }

    /**
     * Estimates the current token count across all tokens in the TokenStore.
     * This reflects the most conservative estimate (minimum remaining across all tokens).
     *
     * @return Minimum remaining tokens across all tokens in the store
     */
    private int getEstimatedTokenCount() {
        return TokenStore.getInstance().getMinRemainingTokens();
    }

}