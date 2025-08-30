package de.shurablack.jima.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Utility class for downloading and saving images from a given URL.
 */
public class ImageLoader {

    /**
     * Logger instance for logging messages and errors.
     */
    private static final Logger LOGGER = LogManager.getLogger(ImageLoader.class);

    /**
     * Downloads an image from the specified URL, modifies the URL to include the desired width and height,
     * and saves the image to the specified file path.
     *
     * @param path   The file path where the image will be saved.
     * @param url    The URL of the image to download.
     * @param width  The desired width of the image.
     * @param height The desired height of the image.
     */
    public static void downloadImage(String path, String url, int width, int height) {
        String modifiedUrl = url.replace("//uploaded", "/height=" + height + ",width=" + width + "/uploaded");
        try (InputStream in = new URL(modifiedUrl).openStream()) {
            Files.copy(in, Paths.get(path), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LOGGER.error("Failed to download image from {}: {}", modifiedUrl, e.getMessage());
        }
    }

    public static void downloadImage(String path, String url) {
        try (InputStream in = new URL(url).openStream()) {
            Files.copy(in, Paths.get(path), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LOGGER.error("Failed to download image from {}: {}", url, e.getMessage());
        }
    }
}