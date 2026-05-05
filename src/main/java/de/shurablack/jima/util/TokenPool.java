package de.shurablack.jima.util;

import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.*;

/**
 * Manages a pool of API tokens with automatic rotation and rate-limiting.
 *
 * <p><b>Overview:</b></p>
 * This class is responsible for managing multiple tokens and distributing requests across them
 * in a way that maximizes throughput while respecting rate limits. It automatically selects the
 * token with the most remaining requests and queues requests when all tokens are exhausted.
 *
 * <p><b>Features:</b></p>
 * <ul>
 *   <li><b>Automatic Token Rotation:</b> Selects the token with the most remaining requests</li>
 *   <li><b>Rate Limit Awareness:</b> Waits for token reset time before retrying when all tokens are exhausted</li>
 *   <li><b>Thread-Safe Initialization:</b> Synchronized methods prevent duplicate token registration</li>
 *   <li><b>Smart Retry Logic:</b> Calculates optimal wait time based on all tokens' reset times</li>
 *   <li><b>Asynchronous Operations:</b> Uses CompletableFuture for non-blocking token acquisition</li>
 *   <li><b>Duplicate Prevention:</b> Prevents adding the same token twice</li>
 * </ul>
 *
 * <p><b>How It Works:</b></p>
 * <ol>
 *   <li>Tokens are stored in a list and managed individually via the {@link Token} class</li>
 *   <li>When a token is needed via {@link #acquire()}, the pool selects the token with the most remaining requests</li>
 *   <li>If a token is available (remaining > 0), its {@link Token#acquire()} method is called</li>
 *   <li>If all tokens are exhausted (remaining ≤ 0), the pool calculates wait time based on nearest reset</li>
 *   <li>The pool schedules a retry operation after the wait time elapses</li>
 *   <li>Waiting is done asynchronously using ScheduledExecutorService to avoid blocking</li>
 * </ol>
 *
 * <p><b>Thread Safety:</b></p>
 * This class uses synchronized methods for initialization to prevent race conditions during
 * token registration. However, token acquisition and state querying are non-blocking operations
 * that use CompletableFuture for asynchronous coordination.
 *
 * <p><b>Example Usage:</b></p>
 * <pre>
 * // Create a pool (typically created by RequestManager)
 * TokenPool pool = new TokenPool();
 *
 * // Initialize tokens (typically done automatically during bootstrap)
 * pool.initializeToken("token1_key", 20, 100, 1234567890L);
 * pool.initializeToken("token2_key", 15, 100, 1234567890L);
 *
 * // Acquire a token for making a request
 * pool.acquire().thenAccept(token -> {
 *     System.out.println("Got token: " + token.getHiddenKey());
 *     // Make API request with this token
 * }).exceptionally(ex -> {
 *     System.err.println("Failed to acquire token: " + ex.getMessage());
 *     return null;
 * });
 *
 * // Check pool status
 * if (pool.isInitialized()) {
 *     int minRemaining = pool.getMinRemainingTokens();
 *     System.out.println("Minimum remaining requests across all tokens: " + minRemaining);
 * }
 * </pre>
 *
 * @see Token
 * @see de.shurablack.jima.http.RequestManager
 * @author JIMA Contributors
 */
public class TokenPool {

    /**
     * List of all tokens managed by this pool.
     * Protected by synchronized methods during modification.
     */
    private final CopyOnWriteArrayList<Token> tokens = new CopyOnWriteArrayList<>();

    /**
     * Scheduler for delayed retry operations when all tokens are exhausted.
     * Uses a single thread to prevent resource exhaustion and maintain ordering.
     */
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    /**
     * Limits how low the remaining should go before the token is considered as unusable
     */
    private int usageLimit = 0;

    public TokenPool() {
        usageLimit = Configurator.get().getInt("USAGE_LIMIT", 0);
    }

    /**
     * Initializes a new token in the pool with the given parameters.
     *
     * <p>This method is called during startup to populate the pool with tokens.
     * It creates a new Token object with the provided parameters and adds it to the pool.</p>
     *
     * <p><b>Preconditions:</b></p>
     * <ul>
     *   <li>The token key must not already exist in the pool</li>
     *   <li>The max parameter should be positive</li>
     * </ul>
     *
     * <p><b>Thread Safety:</b></p>
     * This method is synchronized to prevent race conditions when adding tokens
     * from multiple threads during pool initialization.
     *
     * @param key The API token/key string (e.g., "sk_live_xxxxxxxxxxx")
     * @param remaining The current number of remaining requests (from API response)
     * @param max The maximum number of requests allowed per rate limit period (e.g., 100)
     * @param nextReset The Unix timestamp when the rate limit resets (seconds since epoch)
     * @throws IllegalStateException If a token with the same key already exists in the pool
     */
    public synchronized void initializeToken(String key, int remaining, int max, int nextReset) {
        Optional<Token> existing = tokens.stream()
                .filter(t -> t.getKey().equals(key))
                .findFirst();

        if (existing.isPresent()) {
            throw new IllegalStateException("Token already exists!");
        }

        Token token = new Token(key, max);
        token.updateFromResponse(remaining, nextReset);
        tokens.add(token);
    }

    /**
     * Initializes an existing Token object in the pool.
     *
     * <p>This method allows adding pre-configured Token objects to the pool,
     * useful when Token objects need to be created and configured outside of this class.</p>
     *
     * <p><b>Thread Safety:</b></p>
     * This method is synchronized to prevent race conditions when adding tokens
     * from multiple threads during pool initialization.
     *
     * @param token The pre-configured Token object to add to the pool
     * @throws IllegalStateException If a token with the same key already exists in the pool
     * @throws NullPointerException If the token parameter is null
     */
    public synchronized void initializeToken(Token token) {
        Optional<Token> existing = tokens.stream()
                .filter(t -> t.getKey().equals(token.getKey()))
                .findFirst();

        if (existing.isPresent()) {
            throw new IllegalStateException("Token already exists!");
        }

        tokens.add(token);
    }

    /**
     * Checks if the token pool has been initialized with at least one token.
     *
     * <p>This is a lightweight check that runs in O(1) time.</p>
     *
     * @return {@code true} if the pool contains at least one token, {@code false} otherwise
     */
    public boolean isInitialized() {
        return !tokens.isEmpty();
    }

    /**
     * Acquires a token from the pool for making a request.
     *
     * <p><b>Behavior:</b></p>
     * <ul>
     *   <li>Selects the token with the most remaining requests (greedy strategy)</li>
     *   <li>If a token is available (remaining > 0), returns its acquisition future immediately</li>
     *   <li>If all tokens are exhausted (remaining ≤ 0), schedules a retry after waiting</li>
     *   <li>Wait time is based on the nearest token reset time, with a minimum 5-second wait</li>
     * </ul>
     *
     * <p><b>Thread Safety:</b></p>
     * This method is thread-safe and non-blocking. It returns immediately with a CompletableFuture
     * that will eventually complete with an available token.
     *
     * <p><b>Example:</b></p>
     * <pre>
     * pool.acquire()
     *     .thenAccept(token -> makeRequest(token))
     *     .exceptionally(ex -> {
     *         System.err.println("Token acquisition failed: " + ex);
     *         return null;
     *     });
     * </pre>
     *
     * @return A CompletableFuture that completes with an available Token when one becomes available
     */
    public CompletableFuture<Token> acquire() {
        CompletableFuture<Token> future = new CompletableFuture<>();
        tryAcquire(future);
        return future;
    }

    /**
     * Internal method to attempt acquiring a token or schedule a retry.
     *
     * <p><b>Algorithm:</b></p>
     * <ol>
     *   <li>Stream all tokens and filter those with remaining > 0</li>
     *   <li>Select the one with the maximum remaining requests</li>
     *   <li>If available, delegate to that token's acquire() method</li>
     *   <li>If none available, find the minimum reset time across all tokens</li>
     *   <li>Schedule a retry after (min_reset_time + 1 second), with minimum 5 second wait</li>
     * </ol>
     *
     * <p><b>Parameters:</b></p>
     * This method uses a private recursive pattern: when all tokens are exhausted,
     * it schedules itself to run again after the wait period.
     *
     * @param future The CompletableFuture to complete when a token is available
     */
    private void tryAcquire(CompletableFuture<Token> future) {
        Token best = tokens.stream()
                .filter(s -> s.getRemaining() > usageLimit)
                .max(Comparator.comparingInt(Token::getRemaining))
                .orElse(null);

        if (best != null) {
            best.acquire(usageLimit).thenRun(() -> future.complete(best));
            return;
        }

        long wait = tokens.stream()
                .mapToLong(Token::secondsUntilReset)
                .filter(w -> w > 0)
                .min()
                .orElse(5);

        long scheduleWait = Math.max(5, wait + 1);

        scheduler.schedule(() -> tryAcquire(future), scheduleWait, TimeUnit.SECONDS);
    }

    /**
     * Gets the minimum number of remaining requests across all tokens in the pool.
     *
     * <p>This represents the most conservative estimate of available requests,
     * since it reflects the token with the fewest remaining requests.</p>
     *
     * <p><b>Performance:</b></p>
     * This method runs in O(n) time where n is the number of tokens in the pool.
     * It performs a stream operation over all tokens.
     *
     * <p><b>Return Values:</b></p>
     * <ul>
     *   <li>Returns 0 if the pool is empty (no tokens initialized)</li>
     *   <li>Returns 0 if all tokens have zero remaining requests</li>
     *   <li>Returns positive integer representing the minimum token's remaining requests</li>
     * </ul>
     *
     * @return The minimum remaining requests across all tokens in the pool
     */
    public int getMinRemainingTokens() {
        return tokens.stream()
                .mapToInt(Token::getRemaining)
                .min()
                .orElse(0);
    }

    public void shutdown() {
        this.scheduler.shutdownNow();
    }
}