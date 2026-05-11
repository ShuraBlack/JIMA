package de.shurablack.jima.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.shurablack.jima.http.Requester;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Singleton class responsible for managing application settings and secure token storage.
 *
 * <p><b>Overview:</b></p>
 * AppSettings provides centralized management of application configuration and encryption-based
 * token storage. It uses a two-file approach: a JSON settings file (jima-settings.json) for
 * configuration and a PKCS12 KeyStore file (tokens.store) for securely storing encrypted tokens.
 *
 * <p><b>Key Responsibilities:</b></p>
 * <ul>
 *   <li><b>Settings Management:</b> Loads and validates application configuration from jima-settings.json</li>
 *   <li><b>Token Storage:</b> Manages encrypted token storage using Java KeyStore (PKCS12 format)</li>
 *   <li><b>Password Management:</b> Handles token store password input from user</li>
 *   <li><b>Initialization:</b> Provides singleton access and lazy initialization</li>
 *   <li><b>Validation:</b> Ensures all required configuration and tokens are available</li>
 * </ul>
 *
 * <p><b>Configuration Files:</b></p>
 * <ul>
 *   <li><b>jima-settings.json:</b> JSON file containing application metadata (name, version, email, usage limit)
 *       <pre>
 *       {
 *         "application_name": "MyApp",
 *         "application_version": "1.0.0",
 *         "contact_email": "support@example.com",
 *         "usage_limit": 100
 *       }
 *       </pre>
 *   </li>
 *   <li><b>tokens.store:</b> PKCS12 encrypted KeyStore file containing API tokens
 *       <pre>
 *       - Created using TokenUtil.createStore()
 *       - Keys stored as "token_0", "token_1", etc.
 *       - Password-protected with user-provided password
 *       - Requires password input at application startup
 *       </pre>
 *   </li>
 * </ul>
 *
 * <p><b>Initialization Flow:</b></p>
 * <ol>
 *   <li>User calls main() or manually instantiates AppSettings</li>
 *   <li>requestPasswordInput() prompts user for token store password</li>
 *   <li>createInstance() creates singleton instance if not already created</li>
 *   <li>Settings and tokens are now accessible via static methods</li>
 * </ol>
 *
 * <p><b>Token Storage Architecture:</b></p>
 * Tokens are stored in a PKCS12 KeyStore with the following structure:
 * <ul>
 *   <li>Each token is stored as a Key entry</li>
 *   <li>Keys are named sequentially: token_0, token_1, token_2, etc.</li>
 *   <li>Entire KeyStore is encrypted with a password</li>
 *   <li>Password is provided by user and stored in memory during application runtime</li>
 * </ul>
 *
 * <p><b>Error Handling:</b></p>
 * <ul>
 *   <li><b>Missing Settings File:</b> Creates template, throws IllegalStateException</li>
 *   <li><b>Invalid Settings:</b> Throws IllegalStateException with field details</li>
 *   <li><b>Missing Token Store:</b> Throws IllegalStateException (must be created via TokenUtil)</li>
 *   <li><b>Wrong Password:</b> Throws exception when loading KeyStore</li>
 *   <li><b>Token Retrieval Errors:</b> Logs errors, returns partial token list</li>
 * </ul>
 *
 * <p><b>Example Usage:</b></p>
 * <pre>
 * // Typical initialization in main()
 * public static void main(String[] args) {
 *     AppSettings.requestPasswordInput();              // Prompt for password
 *     AppSettings.createInstance();                    // Load settings and tokens
 *
 *     Settings config = AppSettings.getSettings();
 *     System.out.println("App: " + config.getApplicationName());
 *     System.out.println("Version: " + config.getApplicationVersion());
 *
 *     List<String> tokens = AppSettings.getTokens();
 *     System.out.println("Loaded " + tokens.size() + " tokens");
 *
 *     // Proceed with application logic
 *     Requester.getAuthentication();
 *  <li><b>Missing Token Store:</b> Throws IllegalStateException (must be created via TokenUtil)</li>
 * </ul>
 *
 * @see AppSettings.Settings
 * @see Requester
 * @see TokenUtil
 */
public class AppSettings {

    /**
     * Logger instance for configuration-related log messages.
     * Used to log initialization, errors, and diagnostic information about settings loading.
     */
    private static final Logger LOGGER = LogManager.getLogger(AppSettings.class);

    /**
     * The name of the settings JSON file.
     * Expected to be located in the application root directory (working directory).
     * Format: JSON with required fields: application_name, application_version, contact_email, usage_limit.
     */
    private static final String FILE_SETTINGS = "jima-settings.json";

    /**
     * The name of the token store KeyStore file.
     * Expected to be located in the application root directory (working directory).
     * Format: PKCS12 encrypted KeyStore containing token entries.
     * Must be created using TokenUtil.createStore() before application startup.
     */
    private static final String FILE_STORE = "tokens.store";

    /**
     * Singleton instance of AppSettings.
     * Lazily initialized on first call to createInstance().
     * Once created, it persists for the application's lifetime.
     */
    private static AppSettings INSTANCE;

    /**
     * The loaded and validated application settings.
     * Contains configuration such as application name, version, contact email, and usage limits.
     * Loaded from jima-settings.json during initialization.
     * Never null after successful initialization.
     */
    private Settings settings;

    /**
     * The KeyStore containing encrypted tokens.
     * Format: PKCS12 (Java's native encrypted key storage format).
     * Tokens are stored as Key entries with naming convention "token_0", "token_1", etc.
     * Password-protected; requires TOKEN_STORE_PASSWORD to decrypt.
     * Loaded from tokens.store file during initialization.
     */
    private KeyStore tokenStore;

    /**
     * The password for the token store KeyStore.
     * Obtained from user input via requestPasswordInput() at application startup.
     * Stored in memory as char[] for security (easier to clear than String).
     * Passed to KeyStore.load() and KeyStore.getKey() operations.
     * Static so it persists across method calls and is accessible from all methods.
     */
    private static char[] TOKEN_STORE_PASSWORD;

    /**
     * Creates the singleton AppSettings instance if it doesn't already exist.
     *
     * <p><b>Prerequisites:</b></p>
     * Call requestPasswordInput() before this method to set TOKEN_STORE_PASSWORD.
     *
     * @throws IllegalStateException if settings or token store loading fails
     *
     * @see #requestPasswordInput()
     * @see #getSettings()
     * @see #getTokens()
     */
    public static void createInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AppSettings();
        }
    }

    /**
     * Retrieves the loaded and validated application settings.
     *
     * <p><b>Prerequisites:</b></p>
     * createInstance() must be called first to initialize the singleton.
     *
     * @return The Settings object loaded from jima-settings.json
     * @throws NullPointerException if createInstance() has not been called yet
     *
     * @see #createInstance()
     * @see Settings
     */
    public static Settings getSettings() {
        return INSTANCE.settings;
    }

    /**
     * Retrieves all tokens from the encrypted token store.
     *
     * @return List of successfully retrieved tokens from the store (may be empty)
     *
     * @see #createInstance()
     * @see #requestPasswordInput()
     */
    public static List<String> getTokens() {
        List<String> tokens = new ArrayList<>();
        try {
            int idx = 0;
            while (INSTANCE.tokenStore.containsAlias("token_" + idx)) {
                String token = new String(INSTANCE.tokenStore.getKey("token_" + idx, TOKEN_STORE_PASSWORD).getEncoded());
                if (!token.isEmpty()) {
                    tokens.add(token);
                }
                idx++;
            }
        } catch (KeyStoreException e) {
            LOGGER.error("Failed to access token store: {}", e.getMessage());
        } catch (UnrecoverableKeyException e) {
            LOGGER.error("Failed to retrieve token from store: {}", e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Algorithm for retrieving token is not available: {}", e.getMessage());
        }
        return tokens;
    }

    /**
     * Prompts the user to enter the token store password via standard input.
     *
     * <p><b>Purpose:</b></p>
     * Gets the password needed to decrypt the token store (tokens.store) file.
     * Must be called before createInstance() or any operations requiring token access.
     *
     * <p><b>Example:</b></p>
     * <pre>
     * AppSettings.requestPasswordInput();  // User enters password interactively
     * AppSettings.createInstance();         // Now can decrypt token store
     * </pre>
     *
     * @see #createInstance()
     * @see #TOKEN_STORE_PASSWORD
     */
    public static void requestPasswordInput() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("[JIMA] Enter token store Password: ");
        TOKEN_STORE_PASSWORD = scanner.next().trim().toCharArray();
    }

    /**
     * <b>Don't use this function in production</b><br>
     * This function is for convenience in testing/development.
     *
     * @param password The password which will be used to decrypt the token store.
     * @see #requestPasswordInput()
     */
    public static void requestPasswordInput(String password) {
        TOKEN_STORE_PASSWORD = password.toCharArray();
    }

    /**
     * Private constructor for singleton initialization.
     *
     * <p><b>Made Private to Enforce Singleton Pattern:</b></p>
     * Prevents external code from directly instantiating AppSettings.
     * Use createInstance() static method instead.
     *
     * @throws IllegalStateException if settings or token store initialization fails
     *
     * @see #loadSettings()
     * @see #loadTokenStore()
     * @see #createInstance()
     */
    private AppSettings() {
        loadSettings();
        loadTokenStore();
    }

    /**
     * Loads and validates application settings from jima-settings.json.
     *
     * <p><b>Error Cases:</b></p>
     * <ul>
     *   <li>File not found: Creates template, throws IllegalStateException</li>
     *   <li>Invalid JSON: Logs error, throws IllegalStateException</li>
     *   <li>Missing required fields: Throws IllegalStateException with list of missing fields</li>
     *   <li>File read errors: Logs error, throws IllegalStateException</li>
     * </ul>
     *
     * @throws IllegalStateException if settings file is missing or invalid
     *
     * @see #createTemplateSettings()
     * @see Settings#allSet()
     */
    private void loadSettings() {
        if (!new File(FILE_SETTINGS).exists()) {
            createTemplateSettings();
            throw new IllegalStateException("Settings file not found and set! Template got created...");
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            this.settings = mapper.readValue(new File(FILE_SETTINGS), Settings.class);

            if (!this.settings.allSet()) {
                throw new IllegalStateException("Settings file is missing required fields! Please fill in all required fields in " + FILE_SETTINGS);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load settings from file: {}", e.getMessage());
            throw new IllegalStateException("Failed to load settings from file: " + e.getMessage());
        }
    }

    /**
     * Loads and decrypts the token store (KeyStore) file.
     *
     * @throws IllegalStateException if tokens.store file is missing or cannot be loaded
     *
     * @see #requestPasswordInput()
     * @see #TOKEN_STORE_PASSWORD
     */
    private void loadTokenStore() {
        if (!new File(FILE_STORE).exists()) {
            throw new IllegalStateException("Token store file not found! Please us the TokenUtil.createStore() function to generated one.");
        }

        try {
            this.tokenStore = KeyStore.getInstance("PKCS12");
            this.tokenStore.load(new FileInputStream(FILE_STORE), TOKEN_STORE_PASSWORD);
        } catch (Exception e) {
            LOGGER.error("Failed to load token store: {}", e.getMessage());
            throw new IllegalStateException("Failed to load token store: " + e.getMessage());
        }
    }

    /**
     * Creates a template jima-settings.json file with placeholder values.
     *
     * @see #loadSettings()
     * @see Settings#withDefault()
     */
    private static void createTemplateSettings() {
        try {
            // Use Jackson with pretty printer for readable JSON
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_SETTINGS), Settings.withDefault());
        } catch (Exception e) {
            LOGGER.error("Failed to create template settings file: {}", e.getMessage());
        }
    }

    /**
     * Inner class representing application configuration settings.
     *
     * <p><b>Purpose:</b></p>
     * Encapsulates all application-level configuration parameters needed to run JIMA.
     * Uses Jackson annotations for JSON serialization/deserialization.
     *
     * @see #withDefault()
     * @see #allSet()
     */
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Settings {

        /**
         * The name of the application using JIMA.
         * Used for User-Agent headers and logging identification.
         * Must not be null or empty.
         * Example: "MyGuildWarsApplication"
         */
        @JsonProperty(value = "application_name", required = true)
        private String applicationName;

        /**
         * The version number of the application.
         * Used for User-Agent headers to identify which version is making requests.
         * Should follow semantic versioning (e.g., "1.0.0", "2.1.5-beta").
         * Must not be null or empty.
         * Example: "1.0.0"
         */
        @JsonProperty(value = "application_version", required = true)
        private String applicationVersion;

        /**
         * Contact email address for the application.
         * Included in User-Agent headers per API best practices.
         * Used by API maintainers to contact application developers if needed.
         * Must not be null or empty.
         * Example: "developer@example.com"
         */
        @JsonProperty(value = "contact_email", required = true)
        private String contactEmail;

        /**
         * Maximum number of requests allowed per rate limit period.
         * Set to 0 for unlimited (no rate limiting enforced by application).
         * Positive integer represents maximum concurrent requests or requests per period.
         * Example: 100 means maximum 100 requests allowed
         */
        @JsonProperty(value = "usage_limit", required = true)
        private int usageLimit;

        /**
         * Creates a Settings object with default placeholder values.
         *
         * <p><b>Purpose:</b></p>
         * Used to generate template jima-settings.json file when creating configuration
         * for the first time. Provides template that user can edit with real values.
         *
         * @return New Settings instance with placeholder values
         *
         * @see #AppSettings()
         */
        public static Settings withDefault() {
            return new Settings(
                    "your_app_name_here",
                    "your_app_version_here",
                    "your_contact_email_here",
                    0
            );
        }

        /**
         * Validates that all required settings fields are set (non-null).
         *
         * @return true if all required fields are non-null, false if any are null
         */
        public boolean allSet() {
            return applicationName != null && applicationVersion != null && contactEmail != null;
        }
    }
}