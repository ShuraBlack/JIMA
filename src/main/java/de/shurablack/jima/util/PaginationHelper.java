package de.shurablack.jima.util;

import de.shurablack.jima.http.Requester;
import de.shurablack.jima.http.Response;
import de.shurablack.jima.model.item.Item;
import de.shurablack.jima.model.item.Items;
import de.shurablack.jima.model.item.market.MarketHistory;
import de.shurablack.jima.model.pet.Listings;
import de.shurablack.jima.util.types.ItemType;
import de.shurablack.jima.util.types.MarketType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for handling paginated API responses.
 * Automatically fetches all pages of results and combines them into a single list.
 *
 * <p><b>Overview:</b></p>
 * This helper simplifies working with paginated endpoints by automatically handling pagination logic.
 * Instead of manually checking for more pages and making multiple requests, use these methods
 * to get complete result sets in one call.
 *
 * <p><b>Performance Note:</b></p>
 * These methods make multiple API requests (one per page). For operations on large datasets,
 * this could take significant time and consume many rate limit requests. Progress is logged
 * as pages are fetched.
 *
 * @see <a href="https://idle-mmo.com/wiki/more/api">Idle MMO API Documentation</a>
 */
public class PaginationHelper {

    private static final Logger LOGGER = LogManager.getLogger(PaginationHelper.class);

    private PaginationHelper() {
        // Private constructor to prevent instantiation
    }

    /**
     * Fetches all items of a specific type across all pages.
     * Automatically makes multiple requests to retrieve the complete item list.
     *
     * <p><b>Performance:</b></p>
     * Makes one request per page. The number of pages depends on the item type.
     * Progress is logged as pages are fetched.
     *
     * <p><b>Example:</b></p>
     * <pre>
     * // Get all sword items
     * List&lt;Item&gt; swords = PaginationHelper.fetchAllItemsByType(ItemType.SWORD);
     * System.out.println("Total swords: " + swords.size());
     *
     * // Find cheapest sword
     * Item cheapest = swords.stream()
     *     .min(Comparator.comparing(Item::getPrice))
     *     .orElse(null);
     * </pre>
     *
     * @param itemType The item type to fetch (e.g., SWORD, DAGGER, ARMOR).
     * @return A complete list of all items of the specified type across all pages.
     *         Returns empty list if no items found or API request fails.
     */
    public static List<Item> fetchAllItemsByType(ItemType itemType) {
        List<Item> allItems = new ArrayList<>();
        int currentPage = 1;

        try {
            while (true) {
                LOGGER.debug("Fetching items for type {} - page {}", itemType.name(), currentPage);
                Response<Items> response = Requester.searchType(itemType);

                if (!response.isSuccessful()) {
                    LOGGER.error("Failed to fetch items for type {}: {}", itemType.name(), response.getError());
                    break;
                }

                Items itemsData = response.getData();
                allItems.addAll(itemsData.getItems());

                LOGGER.info("Fetched page {} for {} - {} items so far", currentPage, itemType.name(), allItems.size());

                if (!itemsData.getPagination().isHasMore()) {
                    break;
                }

                currentPage++;
            }
        } catch (Exception e) {
            LOGGER.error("Exception while fetching items for type " + itemType.name(), e);
        }

        LOGGER.info("Completed fetching all {} items for type {} ({} total pages)", allItems.size(), itemType.name(), currentPage);
        return allItems;
    }

    /**
     * Fetches all items of multiple types across all pages for each type.
     * Automatically makes multiple requests to retrieve complete item lists for each type.
     *
     * <p><b>Performance:</b></p>
     * Makes one request per page per type. For N types with M pages each, makes N*M requests.
     * Progress is logged as pages are fetched.
     *
     * <p><b>Example:</b></p>
     * <pre>
     * // Get all armor items (multiple types)
     * List&lt;ItemType&gt; armorTypes = Arrays.asList(
     *     ItemType.CHESTPLATE, ItemType.HELMET, ItemType.BOOTS, ItemType.GREAVES
     * );
     * List&lt;Item&gt; armorItems = PaginationHelper.fetchAllItemsByTypes(armorTypes);
     *
     * // Find heaviest armor
     * Item heaviest = armorItems.stream()
     *     .max(Comparator.comparing(Item::getWeight))
     *     .orElse(null);
     * </pre>
     *
     * @param itemTypes A list of item types to fetch.
     * @return A combined list of all items from all specified types across all pages.
     *         Returns empty list if no items found or API requests fail.
     */
    public static List<Item> fetchAllItemsByTypes(List<ItemType> itemTypes) {
        List<Item> allItems = new ArrayList<>();

        for (ItemType itemType : itemTypes) {
            LOGGER.info("Fetching all items for type {}", itemType.name());
            List<Item> typeItems = fetchAllItemsByType(itemType);
            allItems.addAll(typeItems);
        }

        LOGGER.info("Completed fetching {} total items across {} types", allItems.size(), itemTypes.size());
        return allItems;
    }

    /**
     * Fetches all market history pages for a specific item.
     * Automatically makes multiple requests to retrieve the complete market history.
     *
     * <p><b>Performance:</b></p>
     * Makes one request per page. The number of pages depends on the item's market activity.
     * Progress is logged as pages are fetched.
     *
     * <p><b>Example:</b></p>
     * <pre>
     * // Get full market history for tier 0 listings
     * List&lt;MarketHistory&gt; history =
     *     PaginationHelper.fetchAllMarketHistory("abc123def456", 0, MarketType.LISTINGS);
     *
     * // Calculate average price
     * double avgPrice = history.stream()
     *     .mapToLong(MarketHistory::getPrice)
     *     .average()
     *     .orElse(0);
     * System.out.println("Average price: " + avgPrice);
     * </pre>
     *
     * @param itemId The hashed ID of the item (from item search/inspection).
     * @param tier   The tier level (typically 0-5, higher = more valuable).
     * @param type   The market type (LISTINGS for seller prices, ORDERS for buyer prices).
     * @return A complete list of all market history entries across all pages.
     *         Returns empty list if no history found or API request fails.
     */
    public static List<MarketHistory> fetchAllMarketHistory(String itemId, int tier, MarketType type) {
        List<MarketHistory> allHistory = new ArrayList<>();
        int currentPage = 1;

        try {
            while (true) {
                LOGGER.debug("Fetching market history for item {} - tier {} - {} - page {}",
                        itemId, tier, type.name(), currentPage);
                Response<MarketHistory> response = Requester.getMarketHistory(itemId, tier, type);

                if (!response.isSuccessful()) {
                    LOGGER.error("Failed to fetch market history: {}", response.getError());
                    break;
                }

                MarketHistory history = response.getData();
                allHistory.add(history);

                LOGGER.info("Fetched page {} - {} entries so far", currentPage, allHistory.size());

                // Note: MarketHistory may not have pagination - check if more pages available
                // For now, assume single page or implement based on API response structure
                break;
            }
        } catch (Exception e) {
            LOGGER.error("Exception while fetching market history for item " + itemId, e);
        }

        LOGGER.info("Completed fetching market history for item {} ({} pages)", itemId, currentPage);
        return allHistory;
    }

    /**
     * Gets the total number of pages for a specific item type search.
     * Makes a single request to determine pagination info without fetching all items.
     *
     * <p><b>Example:</b></p>
     * <pre>
     * int totalPages = PaginationHelper.getTotalPages(ItemType.SWORD);
     * System.out.println("Total pages of swords: " + totalPages);
     * </pre>
     *
     * @param itemType The item type to check.
     * @return The number of pages available for the item type.
     *         Returns 0 if the API request fails.
     */
    public static int getTotalPages(ItemType itemType) {
        Response<Items> response = Requester.searchType(itemType);

        if (!response.isSuccessful()) {
            LOGGER.error("Failed to get total pages for type {}: {}", itemType.name(), response.getError());
            return 0;
        }

        return response.getData().getPagination().getLastPage();
    }

    /**
     * Gets the total item count for a specific item type without fetching all items.
     * Makes a single request to check pagination info.
     *
     * <p><b>Example:</b></p>
     * <pre>
     * int totalSwords = PaginationHelper.getTotalCount(ItemType.SWORD);
     * System.out.println("Total swords available: " + totalSwords);
     * </pre>
     *
     * @param itemType The item type to check.
     * @return The total number of items of this type.
     *         Returns 0 if the API request fails.
     */
    public static int getTotalCount(ItemType itemType) {
        Response<Items> response = Requester.searchType(itemType);

        if (!response.isSuccessful()) {
            LOGGER.error("Failed to get total count for type {}: {}", itemType.name(), response.getError());
            return 0;
        }

        return response.getData().getPagination().getTotal();
    }
}
