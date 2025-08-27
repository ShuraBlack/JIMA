package de.shurablack.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.shurablack.http.serialization.HexColorDeserializer;
import de.shurablack.http.serialization.LocalDateTimeDeserializer;
import de.shurablack.util.Configurator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    // Flag indicating whether the Requester is running
    private volatile boolean running = true;

    // HTTP client for sending requests
    private final HttpClient client = HttpClient.newHttpClient();

    // ObjectMapper for JSON serialization and deserialization
    private final ObjectMapper mapper = new ObjectMapper();
    {
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

        SimpleModule module = new SimpleModule();
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        module.addDeserializer(Color.class, new HexColorDeserializer());
        mapper.registerModule(module);
    }

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
            Response<T> response = get(fullUrl.toString(), type);
            future.complete(response);
            return null;
        });
        return future;
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

            if (response.statusCode() == 429) {
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
                .firstValue("X-RateLimit-Reset")
                .orElse("0");

        long resetEpoch = Long.parseLong(resetHeader) + 1;
        rateLimitReset = Instant.ofEpochSecond(resetEpoch);

        LOGGER.warn("Rate limit exceeded. Next request will be sent at: " + rateLimitReset);
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