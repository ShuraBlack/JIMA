package de.shurablack.jima.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import de.shurablack.jima.http.serialization.ApiObjectMapper;
import de.shurablack.jima.model.EndpointUpdate;
import de.shurablack.jima.model.auth.Authentication;
import de.shurablack.jima.util.Configurator;
import de.shurablack.jima.util.Token;
import de.shurablack.jima.util.TokenPool;
import lombok.Getter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Singleton class responsible for managing all HTTP requests with rate-limiting and queuing.
 *
 * <p><b>Overview:</b></p>
 * RequestManager is the central hub for all API communication in JIMA. It handles:
 * <ul>
 *   <li>HTTP requests via Java's HttpClient</li>
 *   <li>Token acquisition and rotation via TokenPool</li>
 *   <li>Rate limit tracking from API response headers</li>
 *   <li>Automatic retry on rate limit (HTTP 429) responses</li>
 *   <li>In-flight request deduplication to prevent duplicate requests</li>
 *   <li>Optional endpoint response caching with expiration tracking</li>
 *   <li>Batch request processing with configurable delays and token requirements</li>
 *   <li>Graceful shutdown coordination</li>
 * </ul>
 *
 * <p><b>Key Features:</b></p>
 * <ul>
 *   <li><b>Token Management:</b> Manages a TokenPool for automatic token rotation and rate-limit awareness</li>
 *   <li><b>Request Throttling:</b> Uses tokens to throttle requests and respect rate limits</li>
 *   <li><b>Automatic Retries:</b> Schedules retries when rate limits are exceeded (HTTP 429)</li>
 *   <li><b>In-Flight Deduplication:</b> Prevents sending duplicate requests for the same URL simultaneously</li>
 *   <li><b>Optional Caching:</b> Can cache endpoint responses with expiration time tracking</li>
 *   <li><b>Batch Processing:</b> Processes batches of requests with configurable delays</li>
 *   <li><b>Async/Sync Support:</b> Supports both asynchronous (CompletableFuture) and synchronous request patterns</li>
 *   <li><b>Shutdown Handling:</b> Gracefully handles application shutdown with request cancellation</li>
 * </ul>
 *
 * <p><b>Request Flow:</b></p>
 * <ol>
 *   <li>Request enters via {@link #enqueueRequest(Endpoint, Map, Map, Class)} or related methods</li>
 *   <li>Check cache if enabled; return cached data if available and not expired</li>
 *   <li>Check if identical request is already in-flight; if so, return existing future</li>
 *   <li>Acquire a token from TokenPool (waits if all tokens exhausted)</li>
 *   <li>Send HTTP request with token in Authorization header</li>
 *   <li>Handle response:
 *       <ul>
 *           <li>If HTTP 429 (rate limited): Schedule retry after reset time</li>
 *           <li>If HTTP 2xx (success): Cache result (if enabled), return data</li>
 *           <li>If HTTP error: Return error response</li>
 *       </ul>
 *   </li>
 *   <li>Update token state based on rate limit response headers</li>
 *   <li>Remove from in-flight map when complete</li>
 * </ol>
 *
 * <p><b>Token Initialization:</b></p>
 * On startup, RequestManager bootstraps tokens by:
 * <ol>
 *   <li>Reading tokens from configuration:
 *       <ul>
 *           <li>If USE_ROTATING_TOKENS=true: Load from jima-tokens.txt (one per line)</li>
 *           <li>Otherwise: Use API_KEY from jima-config.properties</li>
 *       </ul>
 *   </li>
 *   <li>Authenticating each token to get rate limit info</li>
 *   <li>Adding authenticated tokens to TokenPool</li>
 * </ol>
 *
 * <p><b>Thread Safety:</b></p>
 * This class is thread-safe:
 * <ul>
 *   <li>Uses ConcurrentHashMap for in-flight requests tracking</li>
 *   <li>TokenPool is thread-safe internally</li>
 *   <li>ExecutorServices handle concurrent request processing</li>
 *   <li>All shared state is either immutable or protected by concurrent collections</li>
 * </ul>
 *
 * <p><b>Configuration:</b></p>
 * RequestManager reads from jima-config.properties and environment variables:
 * <ul>
 *   <li>API_KEY: Single token (if USE_ROTATING_TOKENS is false)</li>
 *   <li>USE_ROTATING_TOKENS: Enable multi-token rotation</li>
 *   <li>APPLICATION_NAME: User agent identification</li>
 *   <li>APPLICATION_VERSION: User agent identification</li>
 *   <li>CONTACT_EMAIL: User agent identification</li>
 * </ul>
 *
 * <p><b>Example Usage:</b></p>
 * <pre>
 * // Get singleton instance (initialized automatically)
 * RequestManager manager = RequestManager.getInstance();
 *
 * // Enqueue a simple request
 * CompletableFuture&lt;Response&lt;WorldBosses&gt;&gt; future = manager.enqueueRequest(
 *     Endpoint.WORLD_BOSSES,
 *     null,
 *     null,
 *     WorldBosses.class
 * );
 *
 * // Block until result
 * Response&lt;WorldBosses&gt; response = future.join();
 *
 * // Configure logging
 * RequestManager.setLogLevel(Level.DEBUG);
 *
 * // Enable endpoint caching with stats
 * RequestManager.enableEndpointCaching(true);
 *
 * // Check cache statistics
 * CacheStats stats = RequestManager.getCacheRecords();
 * System.out.println("Cache hit rate: " + stats.hitRate());
 *
 * // Wait for all pending requests to complete
 * manager.waitForPending(30, TimeUnit.SECONDS).join();
 * </pre>
 *
 * @see TokenPool
 * @see Token
 * @see Response
 * @see RequestGroup
 * @author JIMA Contributors
 * @version 2.1.0
 */
public class RequestManager {

    /** Logger for RequestManager operations and diagnostics. */
    private static final Logger LOGGER = LogManager.getLogger(RequestManager.class);

    /** Singleton instance of RequestManager. */
    private static final RequestManager INSTANCE = new RequestManager();

    /** Pool of tokens for automatic rotation and rate-limit management. */
    private final TokenPool tokenPool;

    /** Flag indicating if application is shutting down. Uses volatile for visibility across threads. */
    private volatile boolean shuttingDown = false;

    /** Queue for incoming RequestGroup objects awaiting batch processing. */
    private final BlockingQueue<RequestGroup> groupQueue = new LinkedBlockingQueue<>();

    /** Header name for rate limit remaining requests. */
    private static final String X_RATE_LIMIT_REMAINING = "X-RateLimit-Remaining";

    /** Header name for rate limit reset time. */
    private static final String X_RATE_LIMIT_RESET = "X-RateLimit-Reset";

    /** HTTP client for making requests (thread-safe, reusable connection pool). */
    private final HttpClient client = HttpClient.newHttpClient();

    /** Cache for endpoint responses with expiration tracking. Null if caching not enabled. */
    private static Cache<String, EndpointUpdate> ENDPOINT_CACHE;

    /** Map tracking in-flight requests to prevent duplicate simultaneous requests for same URL. */
    private static final ConcurrentHashMap<String, CompletableFuture<?>> IN_FLIGHT = new ConcurrentHashMap<>();

    /** Jackson ObjectMapper configured for API responses and authentication. */
    @Getter
    private final ObjectMapper mapper = new ApiObjectMapper();

    /** Scheduler for delayed operations (e.g., rate limit retries). */
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /** Executor for processing request groups asynchronously. */
    private final ExecutorService groupExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "JIMA-RequestGroupProcessor");
        t.setDaemon(true);
        return t;
    });

    /**
     * Private constructor for singleton pattern.
     *
     * <p><b>Initialization Steps:</b></p>
     * <ol>
     *   <li>Initialize Configurator to load configuration</li>
     *   <li>Start RequestGroup processor thread</li>
     *   <li>Register shutdown hook for graceful cleanup</li>
     *   <li>Initialize TokenPool</li>
     *   <li>Bootstrap tokens (load, authenticate, register)</li>
     * </ol>
     */
    private RequestManager() {
        Configurator.getInstance();

        groupExecutor.execute(this::processRequestGroups);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Shutdown detected – stopping RequestManager...");
            shuttingDown = true;

            groupExecutor.shutdownNow();

            scheduler.shutdownNow();

            IN_FLIGHT.forEach((url, future) -> future.completeExceptionally(new CancellationException("Application shutting down")));
            IN_FLIGHT.clear();
        }));

        this.tokenPool = new TokenPool();
        bootstrap(Configurator.getInstance().has("USE_ROTATING_TOKENS") && Boolean.parseBoolean(Configurator.getInstance().get("USE_ROTATING_TOKENS")));
    }

    /**
     * Bootstraps the RequestManager by loading and authenticating tokens.
     *
     * <p><b>Process:</b></p>
     * <ol>
     *   <li>Determine token loading mode:
     *       <ul>
     *           <li>If useRotating=true: Load multiple tokens from jima-tokens.txt</li>
     *           <li>Otherwise: Use single token from API_KEY config</li>
     *       </ul>
     *   </li>
     *   <li>Create Token objects for each loaded token</li>
     *   <li>Authenticate each token to obtain rate limit information</li>
     *   <li>Register authenticated tokens with TokenPool</li>
     * </ol>
     *
     * <p><b>Error Handling:</b></p>
     * <ul>
     *   <li>If rotating tokens file not found: Logs error and returns</li>
     *   <li>If file is empty: Logs error and returns</li>
     *   <li>If authentication fails: Logs error for that token, continues with others</li>
     * </ul>
     *
     * @param useRotating Whether to load multiple tokens from jima-tokens.txt (true) or single token from config (false)
     */
    private void bootstrap(boolean useRotating) {
        List<Token> tokens = new ArrayList<>();

        if (useRotating) {
            LOGGER.info("TokenPool enabled. Loading tokens from jima-tokens.txt file");
            File file = new File("jima-tokens.txt");
            if (!file.exists() || file.isDirectory()) {
                LOGGER.error("jima-tokens.txt file not found");
                return;
            }

            try {
                List<String> rawTokens = Files.readString(file.toPath()).lines().collect(Collectors.toList());
                if (rawTokens.isEmpty()) {
                    LOGGER.error("jima-tokens.txt file is empty");
                    return;
                }
                tokens.addAll(rawTokens.stream()
                        .filter(line -> !line.isBlank())
                        .map(line -> new Token(line.trim()))
                        .collect(Collectors.toList()));

            } catch (Exception e) {
                LOGGER.error("Failed to load jima-tokens.txt file", e);
            }
        } else {
            tokens.add(new Token(Configurator.getInstance().get("API_KEY")));
        }

        LOGGER.info("Starting authentication for {} token(s)", tokens.size());

        for (Token token : tokens) {
            authenticateToken(token);
        }

        LOGGER.info("All tokens authenticated and added to TokenPool");
    }

    /**
     * Authenticates a single token by making an authentication request.
     *
     * <p><b>Process:</b></p>
     * <ol>
     *   <li>Build authentication request with token</li>
     *   <li>Send HTTP request to authenticate endpoint</li>
     *   <li>Extract Authentication response and rate limit info</li>
     *   <li>Update token with rate limit maximum and current state</li>
     *   <li>Register token with TokenPool</li>
     * </ol>
     *
     * <p><b>Error Handling:</b></p>
     * Different error scenarios:
     * <ul>
     *   <li>HTTP error: Log error status and message</li>
     *   <li>Parse error: Log parsing exception</li>
     *   <li>Network error: Log exception</li>
     * </ul>
     *
     * @param token The Token object to authenticate
     */
    private void authenticateToken(Token token) {
        try {
            HttpRequest request = buildRequest(Endpoint.AUTHENTICATE.getPath(), token.getKey());
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                try {
                    Authentication auth = mapper.readValue(response.body(), Authentication.class);
                    token.updateMax(auth.getRateLimit());

                    int remaining = response.headers().firstValue(X_RATE_LIMIT_REMAINING).flatMap(v -> safeParseHeader(v, Integer.class)).orElse(-1);
                    long reset = response.headers().firstValue(X_RATE_LIMIT_RESET).flatMap(v -> safeParseHeader(v, Long.class)).orElse(Instant.now().getEpochSecond() + 60);
                    token.updateFromResponse(remaining, reset);

                    tokenPool.initializeToken(token);
                    LOGGER.info("Token {} authenticated successfully with rate limit: {}", token.getHiddenKey(), auth.getRateLimit());
                } catch (Exception e) {
                    LOGGER.error("Error parsing authentication response for token {}: {}", token.getHiddenKey(), e.getMessage(), e);
                }
            } else {
                LOGGER.error("Failed to authenticate token {}: HTTP {} - {}", token.getHiddenKey(), response.statusCode(), response.body());
            }
        } catch (Exception e) {
            LOGGER.error("Exception authenticating token {}: {}", token.getHiddenKey(), e.getMessage(), e);
        }
    }

    /**
     * Gets the singleton RequestManager instance.
     *
     * @return The RequestManager singleton instance
     */
    public static RequestManager getInstance() {
        return INSTANCE;
    }

    /**
     * Sets the logging level for RequestManager and related classes.
     *
     * <p>Useful for debugging request flow and rate limit handling.</p>
     *
     * @param level The Log4j2 Level (e.g., DEBUG, INFO, WARN, ERROR)
     * @see org.apache.logging.log4j.Level
     */
    public static void setLogLevel(Level level) {
        org.apache.logging.log4j.core.config.Configurator.setLevel(RequestManager.class, level);
        LogManager.getLogger(LogManager.class).info("Log level set to {}", level);
    }

    /**
     * Enables endpoint response caching with optional statistics tracking.
     *
     * <p><b>Behavior:</b></p>
     * <ul>
     *   <li>If recordStats=true: Cache records hit/miss statistics accessible via getCacheRecords()</li>
     *   <li>If recordStats=false: Cache operates without recording statistics (lower overhead)</li>
     * </ul>
     *
     * <p><b>Cache Expiration:</b></p>
     * Cached responses include expiration time tracking. Expired entries are not returned
     * even if still in cache; they are treated as cache misses.
     *
     * <p><b>Example:</b></p>
     * <pre>
     * // Enable caching with statistics
     * RequestManager.enableEndpointCaching(true);
     *
     * // Later, check cache statistics
     * CacheStats stats = RequestManager.getCacheRecords();
     * System.out.println("Hit rate: " + stats.hitRate());
     * System.out.println("Hits: " + stats.hitCount());
     * System.out.println("Misses: " + stats.missCount());
     * </pre>
     *
     * @param recordStats Whether to record cache hit/miss statistics
     */
    public static void enableEndpointCaching(boolean recordStats) {
         ENDPOINT_CACHE = recordStats
                ? Caffeine.newBuilder().recordStats().build()
                : Caffeine.newBuilder().build();
    }

    /**
     * Gets cache statistics if caching is enabled.
     *
     * <p><b>Statistics Include:</b></p>
     * <ul>
     *   <li>Hit count: Number of successful cache lookups</li>
     *   <li>Miss count: Number of cache lookups that failed</li>
     *   <li>Hit rate: Percentage of lookups that were hits</li>
     *   <li>Eviction count: Number of entries evicted from cache</li>
     *   <li>Load count: Number of cache loads (direct misses)</li>
     * </ul>
     *
     * @return CacheStats object with statistics
     * @throws IllegalStateException If endpoint caching has not been enabled
     */
    public static CacheStats getCacheRecords() {
        if (ENDPOINT_CACHE == null) {
            throw new IllegalStateException("Endpoint caching is not enabled");
        }

        return ENDPOINT_CACHE.stats();
    }

    /**
     * Attempts to retrieve cached data for a URL if caching is enabled.
     *
     * <p><b>Conditions for Cache Hit:</b></p>
     * <ol>
     *   <li>Caching must be enabled (ENDPOINT_CACHE != null)</li>
     *   <li>Response type must be or extend EndpointUpdate</li>
     *   <li>Cached entry must exist for the URL</li>
     *   <li>Cached entry must not be expired</li>
     * </ol>
     *
     * @param <T> The response data type
     * @param url The full request URL
     * @param type The expected response class type
     * @return The cached data if available and valid, null otherwise
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
     * Enqueues a request without specifying a particular token.
     *
     * <p>The request will automatically acquire a token from the TokenPool based on
     * availability and remaining requests.</p>
     *
     * <p><b>Request Flow:</b></p>
     * <ol>
     *   <li>Check cache (if enabled)</li>
     *   <li>Check in-flight to prevent duplicates</li>
     *   <li>Acquire token from TokenPool</li>
     *   <li>Send HTTP request</li>
     * </ol>
     *
     * @param <T> The response data type
     * @param endpoint The API endpoint to call
     * @param query URL path parameters (e.g., {characterId})
     * @param parameter URL query parameters (e.g., page=1)
     * @param type The response class to deserialize into
     * @return CompletableFuture containing the Response when complete
     */
    public <T> CompletableFuture<Response<T>> enqueueRequest(Endpoint endpoint, Map<String, String> query, Map<String, String> parameter, Class<T> type) {
        return enqueueRequest(endpoint, query, parameter, type, null);
    }

    /**
     * Enqueues a request with a specific token.
     *
     * <p>Allows specifying an exact Token object to use for the request, bypassing
     * the TokenPool selection logic. Useful when a specific token is required.</p>
     *
     * <p><b>Parameters:</b></p>
     * <ul>
     *   <li>endpoint: API endpoint definition (path, method, etc.)</li>
     *   <li>query: URL path parameters to substitute into path template</li>
     *   <li>parameter: URL query string parameters</li>
     *   <li>type: Response data class for deserialization</li>
     *   <li>token: Specific Token to use (null to acquire from pool)</li>
     * </ul>
     *
     * @param <T> The response data type
     * @param endpoint The API endpoint to call
     * @param query URL path parameters (replaced in endpoint path)
     * @param parameter URL query string parameters
     * @param type The response class to deserialize into
     * @param token Specific Token to use, or null to acquire from TokenPool
     * @return CompletableFuture containing the Response when complete
     */
    public <T> CompletableFuture<Response<T>> enqueueRequest(
            Endpoint endpoint,
            Map<String, String> query,
            Map<String, String> parameter,
            Class<T> type,
            Token token
    ) {
        String url = buildUrl(endpoint, query, parameter);

        T cached = getCacheData(url, type);
        if (cached != null) {
            return CompletableFuture.completedFuture(
                    new Response<>(ResponseCode.SUCCESS, cached, null)
            );
        }

        return token == null ? sendAsync(url, type) : sendAsync(url, type, token);
    }

    /**
     * Sends an asynchronous HTTP request without specifying a token.
     *
     * <p><b>In-Flight Deduplication:</b></p>
     * If an identical request is already in progress:
     * <ul>
     *   <li>Returns the existing CompletableFuture instead of creating a new request</li>
     *   <li>Prevents unnecessary duplicate requests to the same endpoint</li>
     *   <li>Both callers receive the same result</li>
     * </ul>
     *
     * <p><b>Token Acquisition:</b></p>
     * Acquires a token from TokenPool, which:
     * <ul>
     *   <li>Selects the token with most remaining requests (greedy)</li>
     *   <li>Waits if all tokens are exhausted until one resets</li>
     * </ul>
     *
     * <p><b>Response Handling:</b></p>
     * Updates token state based on rate limit response headers and handles:
     * <ul>
     *   <li>HTTP 429: Schedules retry after reset time</li>
     *   <li>HTTP 2xx: Returns successful response with data</li>
     *   <li>HTTP error: Returns error response</li>
     * </ul>
     *
     * @param <T> The response data type
     * @param url The full request URL
     * @param type The response class for deserialization
     * @return CompletableFuture containing the Response
     * @throws IllegalStateException If TokenPool is not initialized
     */
    public <T> CompletableFuture<Response<T>> sendAsync(String url, Class<T> type) {
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

        if (!this.tokenPool.isInitialized()) {
            throw new IllegalStateException("TokenPool is not initialized");
        }

        return this.tokenPool.acquire()
                .thenCompose(token -> {
                    HttpRequest request = buildRequest(url, token.getKey());
                    return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                            .thenApply(response -> new AbstractMap.SimpleEntry<>(token, response));
                })
                .thenApply(entry -> handleResponse(entry.getKey(), entry.getValue(), type))
                .whenComplete((response, ex) -> {
                    IN_FLIGHT.remove(url);
                });
    }

    /**
     * Sends a synchronous HTTP request with a specific token.
     *
     * <p><b>Behavior:</b></p>
     * <ul>
     *   <li>Blocks until request completes</li>
     *   <li>Uses provided token (does not acquire from pool)</li>
     *   <li>Useful when a specific token must be used</li>
     * </ul>
     *
     * <p><b>Error Handling:</b></p>
     * Catches all exceptions and returns them as Response errors.
     *
     * @param <T> The response data type
     * @param url The full request URL
     * @param type The response class for deserialization
     * @param token The Token to use for this request
     * @return CompletableFuture containing the Response
     */
    public <T> CompletableFuture<Response<T>> sendAsync(String url, Class<T> type, Token token) {
        return CompletableFuture.supplyAsync(() -> {
            HttpRequest request = buildRequest(url, token.getKey());
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                return handleResponse(token, response, type);
            } catch (Exception e) {
                return new Response<>(ResponseCode.BAD_REQUEST, null, e.getMessage());
            }
        });
    }

    /**
     * Builds an HTTP request with the specified URL and token.
     *
     * <p><b>Headers:</b></p>
     * <ul>
     *   <li>Accept: application/json</li>
     *   <li>User-Agent: Application name and version</li>
     *   <li>Authorization: Bearer + token</li>
     * </ul>
     *
     * <p><b>Method:</b> Always GET</p>
     *
     * @param url The full request URL (query parameters included)
     * @param token The API token for authorization
     * @return HttpRequest ready to send
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
     * Handles an HTTP response, updating token state and processing results.
     *
     * <p><b>Processing:</b></p>
     * <ol>
     *   <li>Extract rate limit info from response headers</li>
     *   <li>Update token with rate limit reset time</li>
     *   <li>Check response status code:
     *       <ul>
     *           <li>HTTP 429: Schedule retry after reset</li>
     *           <li>HTTP 2xx: Deserialize and cache result</li>
     *           <li>HTTP error: Return error response</li>
     *       </ul>
     *   </li>
     * </ol>
     *
     * <p><b>Rate Limit Header Parsing:</b></p>
     * Expects headers:
     * <ul>
     *   <li>X-RateLimit-Remaining: Number of remaining requests</li>
     *   <li>X-RateLimit-Reset: Unix timestamp of reset time</li>
     * </ul>
     *
     * @param <T> The response data type
     * @param token The Token used for this request
     * @param response The HTTP response from the server
     * @param type The response class for deserialization
     * @return Response object with data or error
     */
    private <T> Response<T> handleResponse(Token token, HttpResponse<String> response, Class<T> type) {
        try {
            int remaining = response.headers().firstValue(X_RATE_LIMIT_REMAINING).flatMap(v -> safeParseHeader(v, Integer.class)).orElse(-1);
            long reset = response.headers().firstValue(X_RATE_LIMIT_RESET).flatMap(v -> safeParseHeader(v, Long.class)).orElse(Instant.now().getEpochSecond() + 60);

            token.updateFromResponse(reset);

            if (response.statusCode() == 429) {
                LOGGER.warn("Rate limit hit for token {}. Remaining: {}, Reset at: {}. Scheduling retry.",
                        token.getHiddenKey(), remaining, Instant.ofEpochSecond(reset));
                return scheduleRetry(response.uri().toString(), type, reset).join();
            }

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                T data = mapper.readValue(response.body(), type);
                return new Response<>(ResponseCode.fromCode(response.statusCode()), data, null);
            } else {
                LOGGER.warn("Request to URL {} failed with status {}: {}", token.getKey(), response.statusCode(), response.body());
                return new Response<>(ResponseCode.fromCode(response.statusCode()), null, response.body());
            }
        } catch (Exception e) {
            return new Response<>(ResponseCode.BAD_REQUEST, null, e.getMessage());
        }
    }

    /**
     * Safely parses a response header value into the specified type.
     *
     * <p>Uses Jackson's typeconversion with error handling to prevent crashes
     * from malformed header values.</p>
     *
     * @param <T> The target type
     * @param value The header value string
     * @param type The target class
     * @return Optional containing the parsed value, or empty if parsing fails
     */
    private <T> Optional<T> safeParseHeader(String value, Class<T> type) {
        try {
            return Optional.of(mapper.convertValue(value, type));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    /**
     * Schedules a retry of a request after the rate limit reset time.
     *
     * <p><b>Timing:</b></p>
     * <ul>
     *   <li>Calculates delay as: reset_timestamp - current_time + 1 second buffer</li>
     *   <li>Minimum delay of 10 seconds to avoid tight loops</li>
     *   <li>Scheduled using ScheduledExecutorService</li>
     * </ul>
     *
     * <p><b>Retry Behavior:</b></p>
     * The retry will:
     * <ul>
     *   <li>Make an identical request after the delay</li>
     *   <li>Acquire a fresh token from TokenPool</li>
     *   <li>Return the result via a new CompletableFuture</li>
     * </ul>
     *
     * @param <T> The response data type
     * @param url The URL to retry
     * @param type The response class
     * @param reset Unix timestamp when rate limit resets
     * @return CompletableFuture that completes when retry is done
     */
    private <T> CompletableFuture<Response<T>> scheduleRetry(String url, Class<T> type, long reset) {
        long delay = Math.max(10, reset - Instant.now().getEpochSecond() + 1);
        LOGGER.info("Scheduling retry in {} second/s for URL: {}", delay, url);
        CompletableFuture<Response<T>> future = new CompletableFuture<>();
        scheduler.schedule(() -> sendAsync(url, type).thenAccept(future::complete), delay, TimeUnit.SECONDS);
        return future;
    }

    /**
     * Gets the current number of requests in flight.
     *
     * <p>Useful for monitoring and diagnostics to track request concurrency.</p>
     *
     * @return Number of requests currently being processed
     */
    public int getPendingRequestCount() {
        return IN_FLIGHT.size();
    }

    /**
     * Waits for all pending requests to complete within a timeout.
     *
     * <p><b>Behavior:</b></p>
     * <ul>
     *   <li>Polls in-flight request count every 100ms</li>
     *   <li>Returns true if all requests complete before timeout</li>
     *   <li>Returns false if timeout expires with requests still pending</li>
     * </ul>
     *
     * <p><b>Example:</b></p>
     * <pre>
     * // Wait up to 30 seconds for all pending requests
     * boolean completed = RequestManager.getInstance()
     *     .waitForPending(30, TimeUnit.SECONDS)
     *     .join();
     *
     * if (completed) {
     *     System.out.println("All requests completed");
     * } else {
     *     System.out.println("Timeout: some requests still pending");
     * }
     * </pre>
     *
     * @param timeout Maximum time to wait
     * @param unit TimeUnit for timeout value
     * @return CompletableFuture<Boolean> completing with true if all requests finish, false on timeout
     */
    public CompletableFuture<Boolean> waitForPending(long timeout, TimeUnit unit) {
        long deadline = System.currentTimeMillis() + unit.toMillis(timeout);
        return CompletableFuture.supplyAsync(() -> {
            try {
                while (System.currentTimeMillis() < deadline) {
                    if (getPendingRequestCount() == 0) {
                        return true;
                    }
                    Thread.sleep(100);
                }
                return false;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        });
    }

    /**
     * Builds a complete URL from endpoint, query parameters, and query string parameters.
     *
     * <p><b>URL Construction:</b></p>
     * <ol>
     *   <li>Start with endpoint path</li>
     *   <li>Replace path parameters from query map (e.g., {characterId})</li>
     *   <li>URL-encode each parameter value</li>
     *   <li>Add query string parameters with & and = separators</li>
     *   <li>URL-encode each query string value</li>
     * </ol>
     *
     * <p><b>Example:</b></p>
     * <pre>
     * Endpoint: /api/character/{characterId}/metrics
     * Query: {characterId: "abc123"}
     * Parameters: {page: "1"}
     * Result: https://api.example.com/character/abc123/metrics?page=1
     * </pre>
     *
     * @param endpoint The endpoint definition with path template
     * @param query Path parameters to substitute ({paramName} -&gt; value)
     * @param parameter Query string parameters to append
     * @return Complete URL with all parameters encoded
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
            fullUrl.setLength(fullUrl.length() - 1);  // Remove trailing &
        }

        return fullUrl.toString();
    }

    /**
     * Builds the User-Agent header value from application configuration.
     *
     * <p><b>Format:</b> APPLICATION_NAME/APPLICATION_VERSION (Contact: CONTACT_EMAIL)</p>
     *
     * <p><b>Example:</b> "MyApp/1.0.0 (Contact: support@example.com)"</p>
     *
     * @return User-Agent header value
     */
    public String getUserAgent() {
        return Configurator.getInstance().get("APPLICATION_NAME") + "/" +
                Configurator.getInstance().get("APPLICATION_VERSION") + " (Contact: " +
                Configurator.getInstance().get("CONTACT_EMAIL") + ")";
    }

    /**
     * Enqueues a RequestGroup for batch processing.
     *
     * <p><b>Behavior:</b></p>
     * <ul>
     *   <li>Accepts a RequestGroup containing multiple requests</li>
     *   <li>Adds group to processing queue</li>
     *   <li>Background thread processes batches with configured delays</li>
     *   <li>Returns list of futures representing each request</li>
     * </ul>
     *
     * <p><b>Processing Features:</b></p>
     * <ul>
     *   <li>Batch size limiting: Configurable number of requests per batch</li>
     *   <li>Delays between requests (within batch)</li>
     *   <li>Delays between batches</li>
     *   <li>Minimum token availability checks</li>
     *   <li>Group cancellation support</li>
     * </ul>
     *
     * @param group RequestGroup containing requests and configuration
     * @return List of CompletableFutures representing each request
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
     * Main loop for processing RequestGroups from the queue.
     *
     * <p><b>Process:</b></p>
     * <ol>
     *   <li>Continuously polls groupQueue for new RequestGroups (1 second timeout)</li>
     *   <li>For each group:
     *       <ul>
     *           <li>Check if cancelled; skip if so</li>
     *           <li>Process group requests in batches</li>
     *       </ul>
     *   </li>
     *   <li>Exits when shuttingDown flag is set</li>
     * </ol>
     *
     * <p><b>Threading:</b></p>
     * Runs in dedicated daemon thread (JIMA-RequestGroupProcessor).
     * Handles InterruptedExceptions gracefully for shutdown.
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
     * Processes a single RequestGroup by executing its requests in batches.
     *
     * <p><b>Algorithm:</b></p>
     * <ol>
     *   <li>Break requests into batches based on configured batch size</li>
     *   <li>For each batch:
     *       <ul>
     *           <li>Log batch information</li>
     *           <li>Wait for minimum token availability (if configured)</li>
     *           <li>Execute requests sequentially with delays between</li>
     *           <li>Handle errors and cancellation</li>
     *           <li>Wait between batches (if not last batch)</li>
     *       </ul>
     *   </li>
     * </ol>
     *
     * <p><b>Configuration Options:</b></p>
     * <ul>
     *   <li>batchSize: How many requests per batch</li>
     *   <li>delayMs: Delay between individual requests (within batch)</li>
     *   <li>waitMsBetweenBatches: Delay between batches</li>
     *   <li>minTokensAllowed: Minimum available tokens before processing batch</li>
     * </ul>
     *
     * @param group The RequestGroup to process
     */
    private void processGroup(RequestGroup group) {
        List<Supplier<CompletableFuture<?>>> requests = group.getRequests();
        int batchSize = group.getBatchSize();

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

            for (int i = batchStart; i < batchEnd; i++) {
                if (group.isCancelled()) {
                    LOGGER.warn("Request group '{}' cancelled, stopping processing", group.getGroupId());
                    break;
                }

                if (group.getMinTokensAllowed() > 0) {
                    waitForTokenAvailability(group.getGroupId(), group.getMinTokensAllowed());
                }

                try {
                    Supplier<CompletableFuture<?>> requestSupplier = requests.get(i);
                    CompletableFuture<?> future = requestSupplier.get();
                    group.addFuture(future);

                    LOGGER.debug("Executing request {} of {} in group '{}'",
                            i + 1, requests.size(), group.getGroupId());

                    future.join();

                } catch (Exception e) {
                    LOGGER.error("Error executing request {} in group '{}': {}",
                            i + 1, group.getGroupId(), e.getMessage());
                    if (group.isCancelled()) break;
                }

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
     * Waits for sufficient token availability before processing requests.
     *
     * <p><b>Behavior:</b></p>
     * <ul>
     *   <li>Polls token availability every 5 seconds</li>
     *   <li>Returns when minTokensRequired tokens are available</li>
     *   <li>Logs status updates during wait</li>
     *   <li>Maximum wait of 60 seconds; proceeds anyway after timeout</li>
     * </ul>
     *
     * <p><b>Token Availability Estimation:</b></p>
     * Uses minimum remaining tokens across all tokens in the pool (conservative estimate).
     *
     * @param groupId GroupId for logging purposes
     * @param minTokensRequired Minimum number of tokens needed
     */
    private void waitForTokenAvailability(String groupId, int minTokensRequired) {
        long startTime = System.currentTimeMillis();
        long maxWaitMs = 60000;

        while (!shuttingDown && (System.currentTimeMillis() - startTime) < maxWaitMs) {
            int estimatedTokens = getEstimatedTokenCount();

            if (estimatedTokens >= minTokensRequired) {
                LOGGER.info("Token availability restored for group '{}' ({}>={})",
                        groupId, estimatedTokens, minTokensRequired);
                return;
            }

            LOGGER.warn("Stalling group '{}': tokens {} < minimum {}. Will retry in 5 seconds...",
                    groupId, estimatedTokens, minTokensRequired);

            try {
                Thread.sleep(5000);
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
     * Gets an estimate of current token availability.
     *
     * <p>Uses the minimum remaining requests across all tokens in the pool.
     * This is a conservative estimate - actual availability may be higher.</p>
     *
     * @return Minimum remaining tokens across the token pool (0 if no tokens)
     */
    private int getEstimatedTokenCount() {
        return this.tokenPool.getMinRemainingTokens();
    }
}