package de.shurablack.jima.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.shurablack.jima.http.serialization.ApiObjectMapper;
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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Singleton class responsible for handling HTTP requests with rate-limiting and queuing.
 * It provides capability to enqueue requests, process them asynchronously, and handle rate limits.
 */
public class RequestManager {

    private static final Logger LOGGER = LogManager.getLogger(RequestManager.class);
    private static final RequestManager INSTANCE = new RequestManager();

    private volatile boolean shuttingDown = false;
    private final Set<CompletableFuture<?>> pendingRequests = ConcurrentHashMap.newKeySet();

    private static final String X_RATE_LIMIT_REMAINING = "X-RateLimit-Remaining";
    private static final String X_RATE_LIMIT_RESET = "X-RateLimit-Reset";

    private final HttpClient client = HttpClient.newHttpClient();
    @Getter
    private final ObjectMapper mapper = new ApiObjectMapper();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private long usageLimit = 0;

    /**
     * Private constructor to enforce singleton pattern.
     * Initializes the Configurator and sets up a shutdown hook to cleanly stop the scheduler.
     */
    private RequestManager() {
        Configurator.getInstance(); // Ensure Configurator is initialized
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Shutdown detected – stopping RequestManager...");
            shuttingDown = true;

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
     * Enqueues an HTTP request to the specified endpoint with given query parameters, request parameters, and an authorization token.
     *
     * @param endpoint  The API endpoint to send the request to.
     * @param query     A map of query parameters to be included in the URL.
     * @param parameter A map of request parameters to be included in the URL.
     * @param type      The class type of the expected response data.
     * @param token     The authorization token to be included in the request headers.
     * @param <T>       The type of the response data.
     * @return A CompletableFuture that will complete with the response data.
     */
    public <T> CompletableFuture<Response<T>> enqueueRequest(Endpoint endpoint, Map<String, String> query, Map<String, String> parameter, Class<T> type, String token) {
        if (token == null) {
            return new CompletableFuture<>() {{
                complete(new Response<>(ResponseCode.UNAUTHORIZED, null, "No valid token available"));
            }};
        }

        String url = buildUrl(endpoint, query, parameter);
        return sendRequest(url, type, token);
    }

    /**
     * Sends an HTTP request asynchronously and processes the response.
     * Handles rate-limiting and schedules retries if necessary.
     *
     * @param url   The URL to send the request to.
     * @param type  The class type of the expected response data.
     * @param token The authorization token to be included in the request headers.
     * @param <T>   The type of the response data.
     * @return A CompletableFuture that will complete with the response data.
     */
    private <T> CompletableFuture<Response<T>> sendRequest(String url, Class<T> type, String token) {
        if (shuttingDown) {
            return CompletableFuture.completedFuture(
                    new Response<>(ResponseCode.BAD_REQUEST, null, "Application shutting down")
            );
        }

        CompletableFuture<Response<T>> future = new CompletableFuture<>();
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
                    if (ex != null) future.completeExceptionally(ex);
                    else future.complete(result);
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
            long reset = response.headers().firstValue(X_RATE_LIMIT_RESET).map(Long::parseLong).orElse(Instant.now().getEpochSecond() + 60);

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
}