package de.shurablack.jima.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.shurablack.jima.http.serialization.ApiObjectMapper;
import de.shurablack.jima.util.Configurator;
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
import java.util.concurrent.*;

/**
 * Singleton class responsible for handling HTTP requests with rate-limiting and queuing.
 * It provides capability to enqueue requests, process them asynchronously, and handle rate limits.
 */
public class RequestManager {

    // Logger instance for logging messages
    private static final Logger LOGGER = LogManager.getLogger(RequestManager.class);

    // Singleton instance of the Requester class
    private static final RequestManager INSTANCE = new RequestManager();

    // HTTP header constants for rate limiting
    public static final String X_RATE_LIMIT_REMAINING = "X-RateLimit-Remaining";

    // HTTP header constant for rate limit reset time
    public static final String X_RATE_LIMIT_RESET = "X-RateLimit-Reset";

    // Flag indicating whether the Requester is running
    private volatile boolean running = true;

    // HTTP client for sending requests
    private final HttpClient client = HttpClient.newHttpClient();

    // ObjectMapper for JSON serialization and deserialization
    private final ObjectMapper mapper = new ApiObjectMapper();

    // Timestamp indicating when the rate limit will reset
    private volatile Instant rateLimitReset = Instant.now();

    // Queue for storing HTTP request tasks
    private final BlockingQueue<Callable<?>> requestQueue = new LinkedBlockingQueue<>();

    // Executor service for processing the request queue
    private final ExecutorService worker = Executors.newSingleThreadExecutor();

    /**
     * Private constructor to initialize the Requester.
     * Starts the worker thread and sets up a shutdown hook.
     */
    private RequestManager() {
        worker.submit(this::processQueue);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Shutdown detected – stopping Requester...");
            stop();
        }));
    }

    /**
     * Retrieves the singleton instance of the Requester class.
     *
     * @return the singleton instance of Requester
     */
    public static RequestManager getInstance() {
        return INSTANCE;
    }

    /**
     * Processes the request queue by executing tasks while the Requester is running.
     * Waits for rate limits to reset before sending requests.
     */
    private void processQueue() {
        while (running || !requestQueue.isEmpty()) {
            try {
                Callable<?> task = requestQueue.poll(500, TimeUnit.MILLISECONDS);
                if (task != null) {
                    waitIfRateLimited();
                    task.call();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                LOGGER.error("Error processing request", e);
            }
        }
        LOGGER.info("Worker stopped.");
    }

    /**
     * Enqueues an HTTP GET request to be processed asynchronously.
     * @param endpoint the API endpoint to send the request to
     * @param query the path parameters to replace in the endpoint URL
     * @param parameter the query parameters to append to the URL
     * @param type the class type of the expected response data
     * @return a CompletableFuture that will be completed with the response
     * @param <T> the type of the response data
     */
    public <T> CompletableFuture<Response<T>> enqueueRequest(Endpoint endpoint, Map<String, String> query, Map<String, String> parameter, Class<T> type) {
        CompletableFuture<Response<T>> future = new CompletableFuture<>();
        requestQueue.add(() -> {
            String url = buildUrl(endpoint, query, parameter);
            Response<T> response = get(url, type);
            future.complete(response);
            return null;
        });
        return future;
    }

    /**
     * Builds the full URL for the API request by replacing path parameters and appending query parameters.
     *
     * @param endpoint  the API endpoint containing the path
     * @param query     a map of path parameters to replace in the endpoint path
     * @param parameter a map of query parameters to append to the URL
     * @return the constructed full URL as a string
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
     * Sends an HTTP GET request to the specified URL and processes the response.
     *
     * @param url  the URL to send the request to
     * @param type the class type of the expected response data
     * @param <T>  the type of the response data
     * @return a Response object containing the response data or error
     */
    private <T> Response<T> get(String url, Class<T> type) {
        waitIfRateLimited();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .setHeader("Authorization", "Bearer " + Configurator.getInstance().get("API_KEY"))
                    .setHeader("Accept", "application/json")
                    .setHeader("User-Agent", Configurator.getInstance().get("APPLICATION_NAME") + "/" +
                            Configurator.getInstance().get("APPLICATION_VERSION") + " (Contact: " +
                            Configurator.getInstance().get("CONTACT_EMAIL") + ")")
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            String remaining = response.headers().firstValue(X_RATE_LIMIT_REMAINING).orElse("0");
            if (remaining.equals("0")) {
                LOGGER.warn("Rate limit reached! Stall requests until reset.");
                handleRateLimit(response);
                return get(url, type);
            }

            if (response.statusCode() == 429) {
                LOGGER.warn("Received 429 Too Many Requests! Stall requests until reset.");
                handleRateLimit(response);
                return get(url, type);
            }

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                T data = mapper.readValue(response.body(), type);
                return new Response<>(ResponseCode.fromCode(response.statusCode()), data, null);
            } else {
                return new Response<>(ResponseCode.fromCode(response.statusCode()), null, response.body());
            }
        } catch (Exception e) {
            return new Response<>(ResponseCode.BAD_REQUEST, null, e.getMessage());
        }
    }

    /**
     * Handles rate-limiting by parsing the reset time from the response headers.
     *
     * @param response the HTTP response containing rate limit information
     */
    private void handleRateLimit(HttpResponse<String> response) {
        String resetHeader = response.headers()
                .firstValue(X_RATE_LIMIT_RESET)
                .orElse("0");

        long resetEpoch = Long.parseLong(resetHeader) + 1;
        rateLimitReset = Instant.ofEpochSecond(resetEpoch);
        LOGGER.info("Rate limit will reset at {}", rateLimitReset);
    }

    /**
     * Waits until the rate limit reset time has passed before proceeding.
     */
    private void waitIfRateLimited() {
        Instant now = Instant.now();
        if (now.isBefore(rateLimitReset)) {
            long millisToWait = rateLimitReset.toEpochMilli() - now.toEpochMilli();
            try {
                Thread.sleep(millisToWait);
            } catch (InterruptedException ignored) {}
        }
    }

    /**
     * Stops the Requester by shutting down the worker thread and clearing the queue.
     */
    public void stop() {
        running = false;
        worker.shutdown();
        try {
            if (!worker.awaitTermination(5, TimeUnit.SECONDS)) {
                worker.shutdownNow();
            }
        } catch (InterruptedException e) {
            worker.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}