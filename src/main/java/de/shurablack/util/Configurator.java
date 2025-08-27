package de.shurablack.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;

/**
 * Singleton class responsible for managing application configuration.
 * It loads configuration from a `jima-config.properties` file if available,
 * or falls back to environment variables. If the configuration file is
 * missing, a template file is created.
 */
public class Configurator {

    // Logger instance for logging messages
    private static final Logger LOGGER = LogManager.getLogger(Configurator.class);

    // Singleton instance of the Configurator class
    private static final Configurator INSTANCE = new Configurator();

    // Properties object to store configuration key-value pairs
    private final Properties properties;

    /**
     * Retrieves the singleton instance of the Configurator class.
     *
     * @return the singleton instance of Configurator
     */
    public static Configurator getInstance() {
        return INSTANCE;
    }

    /**
     * Private constructor to initialize the Configurator.
     * Loads configuration from a file or environment variables.
     */
    private Configurator() {
        Properties props = new Properties();

        if (checkForConfigFile()) {
            LOGGER.info("Loading configuration from jima-config.properties file");
            props = loadPropertiesFromFile();
            if (props == null || !checkForPropertiesValues(props)) {
                LOGGER.error("jima-config.properties file is missing required properties");
                props = new Properties();
            }
        } else {
            LOGGER.warn("jima-config.properties file not found");
            writeTemplateConfigFile();
            LOGGER.info("Loading configuration from environment variables");
            props.setProperty("API_KEY", System.getenv("API_KEY"));
            props.setProperty("APPLICATION_NAME", System.getenv("APPLICATION_NAME"));
            props.setProperty("APPLICATION_VERSION", System.getenv("APPLICATION_VERSION"));
            props.setProperty("CONTACT_EMAIL", System.getenv("CONTACT_EMAIL"));
        }

        this.properties = props;
    }

    /**
     * Retrieves the value of a configuration property by its key.
     *
     * @param key the key of the property to retrieve
     * @param <T> the expected type of the property value
     * @return the value of the property
     * @throws IllegalArgumentException if the property is not found or is of an unexpected type
     */
    public <T> T get(String key) {
        final Object value = properties.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Property " + key + " not found");
        }
        try {
            return (T) value;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Property " + key + " is not of the expected type", e);
        }
    }

    /**
     * Checks if the `jima-config.properties` file exists in the current directory.
     *
     * @return true if the file exists and is not a directory, false otherwise
     */
    private boolean checkForConfigFile() {
        File file = new File("jima-config.properties");
        return file.exists() && !file.isDirectory();
    }

    /**
     * Creates a template `jima-config.properties` file with placeholder values.
     * Logs an error if the file creation fails.
     */
    private void writeTemplateConfigFile() {
        try {
            Properties templateProps = new Properties();
            templateProps.setProperty("API_KEY", "your_api_key_here");
            templateProps.setProperty("APPLICATION_NAME", "your_app_name_here");
            templateProps.setProperty("APPLICATION_VERSION", "your_app_version_here");
            templateProps.setProperty("CONTACT_EMAIL", "your_contact_email_here");
            templateProps.store(new FileWriter("jima-config.properties"), "Template config file");
            LOGGER.info("Template config.properties file created");
        } catch (Exception e) {
            LOGGER.error("Failed to create template config.properties file", e);
        }
    }

    /**
     * Loads properties from the `jima-config.properties` file.
     *
     * @return a Properties object containing the loaded key-value pairs,
     *         or null if an error occurs during loading
     */
    private Properties loadPropertiesFromFile() {
        try {
            Properties props = new Properties();
            props.load(new FileReader("jima-config.properties"));
            return props;
        } catch (Exception e) {
            LOGGER.error("Failed to load config.properties file", e);
            return null;
        }
    }

    /**
     * Validates that the required properties are present in the given Properties object.
     *
     * @param properties the Properties object to validate
     * @return true if all required properties are present, false otherwise
     */
    private boolean checkForPropertiesValues(Properties properties) {
        return properties.containsKey("API_KEY") &&
                properties.containsKey("APPLICATION_NAME") &&
                properties.containsKey("APPLICATION_VERSION") &&
                properties.containsKey("CONTACT_EMAIL");
    }

}