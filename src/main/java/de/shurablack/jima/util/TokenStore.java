package de.shurablack.jima.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Files;
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
     */
    public void updateToken(String token, int remaining) {
        for (TokenEntry entry : tokens) {
            if (entry.getToken().equals(token)) {
                entry.setRemaining(remaining);
                break;
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
            tokens.add(entry);
        }
        return entry.getToken();
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
     * Represents an entry in the token store.
     * Each entry contains a token and its remaining count.
     */
    private static class TokenEntry {

        private final String token;

        private int remaining;

        /**
         * Constructs a new TokenEntry with the specified token.
         * Initializes the remaining count to 20.
         *
         * @param token The token string.
         */
        public TokenEntry(final String token) {
            this.token = token;
            this.remaining = 20;
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
    }
}