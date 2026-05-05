package de.shurablack.jima.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;

/**
 * Singleton class responsible for managing application configuration.
 *
 * <p><b>Overview:</b></p>
 * Configurator is the central point for loading and accessing application configuration.
 * It implements the singleton pattern to ensure only one configuration instance exists
 * throughout the application's lifetime. Configuration can be loaded from a properties file
 * or environment variables.
 *
 * <p><b>Configuration Sources (in order of precedence):</b></p>
 * <ol>
 *   <li>Properties file: {@code jima-config.properties} in the application root directory</li>
 * </ol>
 *
 * <p><b>Required Configuration Properties:</b></p>
 * <ul>
 *   <li>{@code APPLICATION_NAME}: Name of the application (required)</li>
 *   <li>{@code APPLICATION_VERSION}: Version of the application (required)</li>
 *   <li>{@code CONTACT_EMAIL}: Contact email for the application (required)</li>
 *   <li>{@code API_KEY}: Single API key for authentication (if USE_ROTATING_TOKENS=false)</li>
 *   <li>{@code USE_ROTATING_TOKENS}: Enable multi-token rotation (true/false, optional, default=false)</li>
 * </ul>
 *
 * <p><b>Optional Configuration Properties:</b></p>
 * <ul>
 *   <li>{@code USAGE_LIMIT}: Limit on requests per time period (optional, default=0 for unlimited)</li>
 * </ul>
 */
public class Configurator {

    /**
     * Logger instance for configuration-related log messages.
     * Used to log initialization, errors, and diagnostic information.
     */
    private static final Logger LOGGER = LogManager.getLogger(Configurator.class);

    /**
     * The name of the configuration file to load.
     * Must be located in the application root directory (working directory).
     */
    private static final String CONFIG_FILE = "jima-config.properties";

    /**
     * Singleton instance of the Configurator class.
     * Lazily initialized on first call to {@link #get()}.
     * Once created, remains in memory for the application lifetime.
     */
    private static Configurator INSTANCE;

    /**
     * Properties object storing all loaded configuration key-value pairs.
     * Populated during construction from the configuration file.
     * Immutable after initialization to prevent accidental modifications.
     */
    private final Properties properties;

    /**
     * Retrieves the singleton instance of the Configurator class.
     *
     * <p><b>Lazy Initialization:</b></p>
     * The Configurator is created only when first needed, not at application startup.
     * This allows configuration file setup before the first access.
     *
     * @return the singleton instance of Configurator (never null after first call)
     * @throws IllegalStateException if configuration file is missing or invalid
     * (thrown during first instantiation only)
     *
     * @see #Configurator()
     */
    public static Configurator get() {
        if (INSTANCE == null) {
            INSTANCE = new Configurator();
        }
        return INSTANCE;
    }

    /**
     * Private constructor to initialize the Configurator singleton.
     *
     * <p><b>Side Effects:</b></p>
     * <ul>
     *   <li>Loads jima-config.properties file if it exists</li>
     *   <li>Creates a template config file if the original is missing</li>
     *   <li>Throws IllegalStateException if configuration is invalid</li>
     * </ul>
     *
     * By making this constructor private, we enforce the singleton pattern
     * and prevent external instantiation.
     */
    private Configurator() {
        this.properties = init();
    }

    /**
     * Initializes the configuration by loading and validating the properties file.
     *
     * <p><b>Error Cases:</b></p>
     * <ul>
     *   <li>File not found: Template is created, IllegalStateException thrown</li>
     *   <li>Essential properties missing: IllegalStateException thrown</li>
     *   <li>File read errors: Logged, attempt continues (may fail validation)</li>
     * </ul>
     *
     * @return A validated Properties object containing loaded configuration
     * @throws IllegalStateException if configuration file is missing/invalid or essential properties are missing
     *
     * @see #writeTemplateConfigFile()
     * @see #loadPropertiesFromFile()
     * @see #checkForEssentialValues(Properties)
     */
    private Properties init() {
        if (!new File(CONFIG_FILE).exists()) {
            writeTemplateConfigFile();
            throw new IllegalStateException("Config file not found. A template config.properties file has been created. Please fill in the required values and restart the application.");
        }

        Properties props = loadPropertiesFromFile();

        if (!checkForEssentialValues(props)) {
            throw new IllegalStateException("Essential properties are missing in jima-config.properties file");
        }

        return props;
    }

    /**
     * Retrieves a required string configuration property.
     *
     * @param key the property key to retrieve (case-sensitive)
     * @return the string value of the property (never null)
     * @throws IllegalArgumentException if the property key is not found in configuration
     *
     * @see #has(String)
     * @see #getInt(String, int)
     * @see #getBoolean(String, boolean)
     */
    public String getString(String key) {
        final Object value = properties.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Property " + key + " not found");
        }

        return value.toString();
    }

    /**
     * Retrieves an integer configuration property with a default fallback value.
     *
     * @param key the property key to retrieve (case-sensitive)
     * @param defaultValue the value to return if property is not found
     * @return the integer value of the property, or defaultValue if not found
     * @throws IllegalArgumentException if the property value cannot be parsed as an integer
     *
     * @see #getBoolean(String, boolean)
     * @see #getString(String)
     */
    public int getInt(String key, int defaultValue) {
        final Object value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Property " + key + " is not a valid integer", e);
        }
    }

    /**
     * Retrieves a boolean configuration property with a default fallback value.
     *
     * @param key the property key to retrieve (case-sensitive)
     * @param defaultValue the value to return if property is not found
     * @return the boolean value of the property, or defaultValue if not found
     * @throws IllegalArgumentException if the property value cannot be parsed as a boolean
     *
     * @see #getInt(String, int)
     * @see #getString(String)
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        final Object value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }

        try {
            return Boolean.parseBoolean(value.toString());
        } catch (Exception e) {
            throw new IllegalArgumentException("Property " + key + " is not a valid boolean", e);
        }
    }

    /**
     * Checks if a configuration property exists.
     *
     * @param key the property key to check (case-sensitive)
     * @return true if the property exists, false otherwise
     *
     * @see #getString(String)
     * @see #getInt(String, int)
     * @see #getBoolean(String, boolean)
     */
    public boolean has(String key) {
        return properties.containsKey(key);
    }

    /**
     * Creates a template configuration file with placeholder values.
     *
     * <p><b>Purpose:</b></p>
     * When the jima-config.properties file is missing, this method creates a template
     * file with empty placeholders. This guides the user on what configuration must be set.
     *
     * <p><b>Template Contents:</b></p>
     * <pre>
     * API_KEY=your_api_key_here
     * APPLICATION_NAME=your_app_name_here
     * APPLICATION_VERSION=your_app_version_here
     * CONTACT_EMAIL=your_contact_email_here
     * USE_ROTATING_TOKENS=false
     * USAGE_LIMIT=0
     * </pre>
     *
     * <p><b>Location:</b></p>
     * Template file is created in the current working directory as jima-config.properties.
     *
     * @see #init()
     */
    private void writeTemplateConfigFile() {
        try {
            Properties templateProps = new Properties();
            templateProps.setProperty("API_KEY", "your_api_key_here");
            templateProps.setProperty("APPLICATION_NAME", "your_app_name_here");
            templateProps.setProperty("APPLICATION_VERSION", "your_app_version_here");
            templateProps.setProperty("CONTACT_EMAIL", "your_contact_email_here");
            templateProps.setProperty("USE_ROTATING_TOKENS", "false");
            templateProps.setProperty("USAGE_LIMIT", "0");

            templateProps.store(new FileWriter(CONFIG_FILE), "Template config file");
            LOGGER.info("Template jima-config.properties file created");
        } catch (Exception e) {
            LOGGER.error("Failed to create template jima-config.properties file", e);
        }
    }

    /**
     * Loads properties from the jima-config.properties file.
     *
     * @return a Properties object containing the loaded configuration or empty if error occurs
     *
     * @see #init()
     */
    private Properties loadPropertiesFromFile() {
        LOGGER.info("Loading configuration from jima-config.properties file");
        try {
            Properties props = new Properties();
            props.load(new FileReader(CONFIG_FILE));
            return props;
        } catch (Exception e) {
            LOGGER.error("Failed to load config.properties file", e);
            return new Properties();
        }
    }

    /**
     * Validates that all essential configuration properties are present.
     *
     * <p><b>Essential Properties Checked:</b></p>
     * <ul>
     *   <li>APPLICATION_NAME: Required always</li>
     *   <li>APPLICATION_VERSION: Required always</li>
     *   <li>CONTACT_EMAIL: Required always</li>
     *   <li>Either API_KEY or USE_ROTATING_TOKENS: At least one required</li>
     * </ul>
     *
     * <p><b>Return Values:</b></p>
     * <ul>
     *   <li>true: All required properties are present</li>
     *   <li>false: At least one required property is missing</li>
     * </ul>
     *
     * @param properties the Properties object to validate
     * @return true if all essential properties are present, false otherwise
     *
     * @see #init()
     */
    private boolean checkForEssentialValues(Properties properties) {
        return properties.containsKey("APPLICATION_NAME") &&
                properties.containsKey("APPLICATION_VERSION") &&
                properties.containsKey("CONTACT_EMAIL") &&
                (properties.containsKey("API_KEY") || properties.containsKey("USE_ROTATING_TOKENS"));
    }

}