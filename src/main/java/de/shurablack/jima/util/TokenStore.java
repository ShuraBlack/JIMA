package de.shurablack.jima.util;

import de.shurablack.jima.http.Requester;
import de.shurablack.jima.http.Response;
import de.shurablack.jima.model.auth.Authentication;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Files;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

/**
 * A singleton class that manages a store of tokens.
 * Tokens are stored in a thread-safe set and can be loaded from a file.
 */
public class TokenStore {

    private static final Logger LOGGER = LogManager.getLogger(TokenStore.class);

    // Singleton instance of the TokenStore
    private static final TokenStore INSTANCE = new TokenStore();

    // Thread-safe set of tokens, sorted by the remaining count in descending order
    private final ConcurrentSkipListSet<TokenEntry> tokens;

    /**
     * Private constructor to enforce singleton pattern.
     * Initializes the token set with a custom comparator.
     */
    private TokenStore() {
        tokens = new ConcurrentSkipListSet<>(
                Comparator.comparingInt(TokenEntry::getRemaining)
                        .reversed()
                        .thenComparing(TokenEntry::getNextReset)
                        .thenComparing(TokenEntry::getToken)
        );
    }

    /**
     * Retrieves the singleton instance of the TokenStore.
     *
     * @return The singleton instance of TokenStore.
     */
    public static TokenStore getInstance() {
        return INSTANCE;
    }

    /**
     * Adds a new token to the store.
     *
     * @param token The token to be added.
     */
    public void addToken(String token) {
        tokens.add(new TokenEntry(token));
    }

    /**
     * Updates the remaining count for a specific token.
     *
     * @param token     The token to be updated.
     * @param remaining The new remaining count for the token.
     * @param nextReset The next reset time for the token in milliseconds.
     */
    public void updateToken(String token, int remaining, long nextReset) {
        long timestamp = Instant.now().getEpochSecond();
        for (TokenEntry entry : tokens) {
            if (entry.getToken().equals(token)) {
                entry.setRemaining(remaining);
                entry.setNextReset(nextReset);
            } else if (entry.getNextReset() <= timestamp) {
                entry.setRemaining(20);
                entry.setNextReset(Long.MAX_VALUE);
            }
        }
    }

    /**
     * Retrieves and returns the token with the highest remaining count.
     * The token is re-added to the set after retrieval.
     *
     * @return The token with the highest remaining count, or null if no tokens are available.
     */
    public String getToken() {
        TokenEntry entry = tokens.pollFirst();
        if (entry != null && entry.getToken() != null) {
            entry.setRemaining(Math.max(0, entry.getRemaining() - 1));
            tokens.add(entry);
        }
        return entry.getToken();
    }

    /**
     * Retrieves a list of Authentication objects for all stored tokens.
     * Only successful authentications are included in the returned list.
     *
     * @return A list of Authentication objects for valid tokens.
     */
    public List<Authentication> getTokenAuthentications() {
        List<Authentication> authentications = new ArrayList<>();

        for (TokenEntry entry : new ArrayList<>(tokens)) {
            if (entry.getToken() == null) {
                continue;
            }
            Response<Authentication> response = Requester.getAuthentication(entry.getToken());
            if (response.isSuccessful()) {
                authentications.add(response.getData());
            }
        }

        return authentications;
    }

    /**
     * Loads tokens from the `jima-tokens.txt` file.
     * Logs errors if the file is missing, empty, or cannot be read.
     */
    public void loadTokens() {
        LOGGER.info("TokenStore enabled. Loading tokens from jima-tokens.txt file");
        File file = new File("jima-tokens.txt");
        if (!file.exists() || file.isDirectory()) {
            LOGGER.error("jima-tokens.txt file not found");
            return;
        }

        try {
            List<String> tokens = Files.readString(file.toPath()).lines().collect(Collectors.toList());
            if (tokens.isEmpty()) {
                LOGGER.error("jima-tokens.txt file is empty");
                return;
            }

            List<TokenEntry> entries = tokens.stream()
                    .filter(token -> !token.isBlank())
                    .map(TokenEntry::new)
                    .collect(Collectors.toList());
            this.tokens.addAll(entries);
        } catch (Exception e) {
            LOGGER.error("Failed to load jima-tokens.txt file", e);
        }
    }

    /**
     * Validates a token by checking its authentication status with the API.
     * Useful for periodic token health checks and determining token validity.
     *
     * @param token The token to validate.
     * @return true if the token is valid and authentication succeeds, false otherwise.
     */
    public boolean validateToken(String token) {
        Response<Authentication> response = Requester.getAuthentication(token);
        return response.isSuccessful();
    }

    /**
     * Retrieves authentication statistics for a specific token.
     * Provides information about the token's rate limits and usage.
     *
     * @param token The token to check.
     * @return The Authentication object containing token information and rate limits,
     *         or null if authentication failed.
     */
    public Authentication getTokenStats(String token) {
        Response<Authentication> response = Requester.getAuthentication(token);
        return response.isSuccessful() ? response.getData() : null;
    }

    /**
     * Represents an entry in the token store.
     * Each entry contains a token and its remaining count.
     */
    @Getter
    private static class TokenEntry {

        private final String token;

        private int remaining;

        private long nextReset;

        /**
         * Constructs a new TokenEntry with the specified token.
         * Initializes the remaining count to 20.
         *
         * @param token The token string.
         */
        public TokenEntry(final String token) {
            this.token = token;
            this.remaining = 20;
            this.nextReset = Long.MAX_VALUE;
        }

        /**
         * Sets the remaining count for the token.
         *
         * @param remaining The new remaining count.
         */
        public void setRemaining(int remaining) {
            this.remaining = remaining;
        }

        /**
         * Sets the next reset time for the token.
         *
         * @param nextReset The next reset time in milliseconds.
         */
        public void setNextReset(long nextReset) {
            this.nextReset = nextReset;
        }

        /**
         * Retrieves the token string.
         *
         * @return The token string.
         */
        public String getToken() {
            return token;
        }

        /**
         * Retrieves the remaining count for the token.
         *
         * @return The remaining count.
         */
        public int getRemaining() {
            return remaining;
        }

        /**
         * Retrieves the next reset time for the token.
         *
         * @return The next reset time in milliseconds.
         */
        public long getNextReset() {
            return nextReset;
        }
    }
}