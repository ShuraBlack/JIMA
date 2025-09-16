package de.shurablack.jima.util;

import de.shurablack.jima.http.Requester;
import de.shurablack.jima.http.Response;
import de.shurablack.jima.model.item.Item;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class for matching item names based on similarity and loading item lists from a file.
 */
public class ItemNameMatcher {

    private static final Logger LOGGER = LogManager.getLogger(ItemNameMatcher.class);

    private static List<String> DEFAULT_CANDIDATES;

    /**
     * Retrieves the default list of candidate item names.
     * If the list has not been loaded yet, it loads the list from the `items.txt` file.
     *
     * @return A list of default candidate item names.
     */
    private static List<String> getDefaultCandidates() {
        if (DEFAULT_CANDIDATES == null) {
            DEFAULT_CANDIDATES = loadItemList();
        }
        return DEFAULT_CANDIDATES;
    }

    /**
     * Finds the best matching string from a default list of candidates based on similarity to a target string.
     *
     * @param target The target string to match against.
     * @return The string from the default list of candidates that has the highest similarity to the target,
     *         or null if the list is empty.
     */
    public static String getBestMatch(String target) {
        JaroWinklerSimilarity similarity = new JaroWinklerSimilarity();
        String best = null;
        double maxScore = -1;

        // Iterate through the list of candidates and calculate similarity scores.
        for (String candidate : getDefaultCandidates()) {
            double score = similarity.apply(target, candidate);
            if (score > maxScore) {
                maxScore = score;
                best = candidate;
            }
        }
        return best;
    }

    /**
     * Finds the best matching string from a list of candidates based on similarity to a target string.
     *
     * @param target  The target string to match against.
     * @param candidates A list of candidate strings to compare with the target.
     * @return The string from the list of candidates that has the highest similarity to the target,
     *         or null if the list is empty.
     */
    public static String getBestMatch(String target, List<String> candidates) {
        JaroWinklerSimilarity similarity = new JaroWinklerSimilarity();
        String best = null;
        double maxScore = -1;

        // Iterate through the list of candidates and calculate similarity scores.
        for (String candidate : candidates) {
            double score = similarity.apply(target, candidate);
            if (score > maxScore) {
                maxScore = score;
                best = candidate;
            }
        }
        return best;
    }

    /**
     * Loads a list of item names from the `items.txt` file.
     * Blank lines are removed from the list. Logs an error if the file cannot be read.
     *
     * @return A list of item names, or an empty list if the file cannot be read.
     */
    public static List<String> loadItemList() {
        try {
            // Read all lines from the file and remove blank lines.
            List<String> items = Files.readAllLines(Path.of("items.txt"));
            items.removeIf(String::isBlank);
            return items;
        } catch (Exception e) {
            // Log an error if the file cannot be read.
            LOGGER.error("Failed to load items.txt", e);
            return List.of();
        }
    }

    /**
     * Updates the `items.txt` file with the latest list of item names fetched from the API.
     * The items are sorted alphabetically before being written to the file.
     * Logs an error if the API request fails or if the file cannot be written.
     */
    public static void updateListFile() {
        Response<Set<Item>> response = Requester.getAllItems();
        if (!response.isSuccessful()) {
            LOGGER.error("Failed to fetch items from API: " + response.getError());
            return;
        }

        Set<Item> items = response.getData();
        List<String> itemNames = items.stream()
                .map(Item::getName)
                .sorted()
                .collect(Collectors.toList());

        try {
            Files.write(Path.of("items.txt"), itemNames);
            LOGGER.info("Updated items.txt with " + itemNames.size() + " items.");
        } catch (Exception e) {
            LOGGER.error("Failed to write to items.txt", e);
        }
    }

}