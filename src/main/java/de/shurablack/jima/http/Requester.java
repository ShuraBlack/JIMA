package de.shurablack.jima.http;

import de.shurablack.jima.model.Paged;
import de.shurablack.jima.model.auth.Authentication;
import de.shurablack.jima.model.character.CharacterAction;
import de.shurablack.jima.model.character.CharacterAlts;
import de.shurablack.jima.model.character.effect.CharacterEffects;
import de.shurablack.jima.model.character.metric.CharacterMetric;
import de.shurablack.jima.model.character.museum.CharacterMuseum;
import de.shurablack.jima.model.character.pet.CharacterPets;
import de.shurablack.jima.model.character.view.CharacterView;
import de.shurablack.jima.model.combat.dungeon.Dungeons;
import de.shurablack.jima.model.combat.enemy.Enemies;
import de.shurablack.jima.model.combat.worldboss.WorldBosses;
import de.shurablack.jima.model.guild.GuildMembers;
import de.shurablack.jima.model.guild.GuildView;
import de.shurablack.jima.model.guild.conquest.GuildConquest;
import de.shurablack.jima.model.guild.conquest.GuildConquestInspection;
import de.shurablack.jima.model.guild.events.EnergizingPoolInfo;
import de.shurablack.jima.model.guild.hall.GuildHallView;
import de.shurablack.jima.model.item.Item;
import de.shurablack.jima.model.item.ItemInspection;
import de.shurablack.jima.model.item.Items;
import de.shurablack.jima.model.item.market.MarketHistory;
import de.shurablack.jima.model.pet.Listings;
import de.shurablack.jima.model.shrine.ShrineInfo;
import de.shurablack.jima.model.world.WorldLocations;
import de.shurablack.jima.util.ItemNameMatcher;
import de.shurablack.jima.util.Token;
import de.shurablack.jima.util.types.ItemType;
import de.shurablack.jima.util.types.LocationType;
import de.shurablack.jima.util.types.MarketType;
import de.shurablack.jima.util.types.MuseumCategory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Provides methods to interact with various API endpoints for the Idle MMO game.
 *
 * <p><b>Overview:</b></p>
 * This class contains static methods to perform HTTP requests and retrieve data for authentication,
 * characters, guilds, items, markets, and other game entities. All methods follow a consistent pattern
 * of returning a {@link Response} object that encapsulates the result, error messages, and response codes.
 *
 * <p><b>Request Execution:</b></p>
 * <ul>
 *   <li>All methods are <b>blocking</b> by default (they call {@link java.util.concurrent.CompletableFuture#join()})</li>
 *   <li>Requests are managed by the singleton {@link RequestManager}, which handles HTTP communication</li>
 *   <li>Token management is handled automatically by {@link de.shurablack.jima.util.TokenPool}</li>
 * </ul>
 *
 * <p><b>Rate Limiting &amp; Retry Logic:</b></p>
 * <ul>
 *   <li>The library automatically detects 429 (Too Many Requests) responses</li>
 *   <li>Automatic retries are scheduled based on the {@code X-RateLimit-Reset} header</li>
 *   <li>Token rotation is supported for increased rate limits via {@link de.shurablack.jima.util.TokenPool}</li>
 * </ul>
 *
 * <p><b>Error Handling Pattern:</b></p>
 * <pre>
 * Response&lt;CharacterView&gt; response = Requester.getCharacter("characterId");
 * if (response.isSuccessful()) {
 *     CharacterView character = response.getData();
 *     System.out.println("Name: " + character.getCharacter().getName());
 * } else {
 *     System.err.println("Error: " + response.getError());
 *     ResponseCode code = response.getResponseCode();
 *     if (code == ResponseCode.UNAUTHORIZED) {
 *         // Handle invalid token
 *     } else if (code == ResponseCode.RATE_LIMIT_EXCEEDED) {
 *         // Handle rate limit
 *     }
 * }
 * </pre>
 *
 * @see RequestManager
 * @see de.shurablack.jima.util.TokenPool
 * @see Response
 */
public class Requester {

    private Requester() {
        // Private constructor to prevent instantiation
    }

    /**
     * Enqueues an individual request with fully customizable parameters.
     * <b>DANGER</b> Using this method wrongly will result in mapping errors.
     *
     * @param endpoint The API url of the request
     * @param query The insertable values of the route
     * @param parameter The appending parameters on the request url
     * @param responseType The expected type of the API response
     * @return A response containing response type details.
     * @param <T> The response type
     */
    public static <T> Response<T> get(
            Endpoint endpoint,
            Map<String, String> query,
            Map<String, String> parameter,
            Class<T> responseType
    ) {
        return RequestManager.getInstance().enqueueRequest(
                endpoint,
                query,
                parameter,
                responseType
        ).join();
    }

    /**
     * Retrieves authentication information.
     * @return A response containing authentication details.
     */
    public static Response<Authentication> getAuthentication() {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.AUTHENTICATE,
                null,
                null,
                Authentication.class
        ).join();
    }

    /**
     * Retrieves authentication information using a provided token.
     * @param token The authentication token.
     * @return A response containing authentication details.
     */
    public static Response<Authentication> getAuthentication(Token token) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.AUTHENTICATE,
                null,
                null,
                Authentication.class,
                token
        ).join();
    }

    /**
     * Retrieves all world locations with extended weather forecast data.
     *
     * @return A {@code Response<WorldLocations>} containing all world locations with weather data,
     *         or an error response if the request fails
     */
    public static Response<WorldLocations> getWorldLocations() {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.WORLD_LOCATIONS_LIST,
                null,
                null,
                WorldLocations.class
        ).join();
    }

    /**
     * Retrieves information about world bosses.
     * @return A response containing world boss details.
     */
    public static Response<WorldBosses> getWorldBosses() {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.WORLD_BOSSES,
                null,
                null,
                WorldBosses.class
        ).join();
    }

    /**
     * Retrieves information about dungeons.
     * @return A response containing dungeon details.
     */
    public static Response<Dungeons> getDungeons() {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.DUNGEONS,
                null,
                null,
                Dungeons.class
        ).join();
    }

    /**
     * Retrieves information about enemies.
     * @return A response containing enemy details.
     */
    public static Response<Enemies> getEnemies() {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.ENEMIES,
                null,
                null,
                Enemies.class
        ).join();
    }

    /**
     * Searches for items based on various criteria.
     * @param query The search query.
     * @return A response containing item details.
     */
    public static Response<Items> searchItems(String query) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.ITEMS,
                null,
                Map.of("query", query),
                Items.class
        ).join();
    }

    /**
     * Searches for items of a specific type.
     * @param type The type of items to search for.
     * @return A response containing item details.
     */
    public static Response<Items> searchItems(ItemType type) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.ITEMS,
                null,
                Map.of("type", type.name().toLowerCase()),
                Items.class
        ).join();
    }

    /**
     * Searches for all items of a specific type, automatically handling pagination.
     *
     * <p><b>Features:</b></p>
     * <ul>
     *   <li>Automatically retrieves all pages of results</li>
     *   <li>Combines results into a single list for convenience</li>
     *   <li>Returns empty list if the item type yields no results</li>
     * </ul>
     *
     * <p><b>Example:</b></p>
     * <pre>
     * Response&lt;Items&gt; response = Requester.searchType(ItemType.WEAPON);
     * if (response.isSuccessful()) {
     *     Items result = response.getData();
     *     List&lt;Item&gt; weapons = result.getItems();
     *     System.out.println("Found " + weapons.size() + " weapons");
     *     weapons.stream()
     *         .sorted(Comparator.comparing(Item::getName))
     *         .forEach(item -&gt; System.out.println(item.getName()));
     * }
     * </pre>
     *
     * @param type The ItemType to search for (e.g., WEAPON, ARMOR, CONSUMABLE, etc.)
     * @return A response containing all items of the specified type across all pages.
     *         The response data includes a Paged object with pagination metadata.
     */
    public static Response<Items> searchType(ItemType type) {
        List<Item> items = new ArrayList<>();

        for (int page = 1 ; ; page++) {
            Response<Items> response = Requester.searchItems(type, page);
            if (!response.isSuccessful()) {
                return new Response<>(response.getResponseCode(), null, response.getError());
            }

            items.addAll(response.getData().getItems());
            if (page == response.getData().getPagination().getLastPage()) {
                break;
            }
        }

        Paged pagination = new Paged();
        pagination.setCurrentPage(1);
        pagination.setLastPage(1);
        pagination.setTotal(1);
        pagination.setHasMore(false);
        return new Response<>(ResponseCode.SUCCESS, new Items(items, pagination), null);
    }

    /**
     * Searches for all items of multiple specific types, automatically handling pagination.
     *
     * <p><b>Features:</b></p>
     * <ul>
     *   <li>Retrieves items across multiple item types in a single call</li>
     *   <li>Automatically handles pagination for each type</li>
     *   <li>Combines all results into a single list</li>
     *   <li>More efficient than calling {@link #searchType(ItemType)} multiple times</li>
     * </ul>
     *
     * <p><b>Example:</b></p>
     * <pre>
     * // Get all weapons and armor
     * Response&lt;Items&gt; response = Requester.searchTypes(
     *     List.of(ItemType.WEAPON, ItemType.ARMOR, ItemType.ACCESSORY)
     * );
     * if (response.isSuccessful()) {
     *     List&lt;Item&gt; equipment = response.getData().getItems();
     *     long weaponCount = equipment.stream()
     *         .filter(item -&gt; item.getType().equals(ItemType.WEAPON))
     *         .count();
     *     System.out.println("Found " + equipment.size() + " items (" + weaponCount + " weapons)");
     * }
     * </pre>
     *
     * @param types The set of ItemTypes to search for. If empty or null, returns an empty response.
     * @return A response containing all items of the specified types across all pages.
     *         The response data includes a Paged object with pagination metadata.
     */
    public static Response<Items> searchTypes(Set<ItemType> types) {
        List<Item> items = new ArrayList<>();

        for (ItemType type : types) {
            for (int page = 1 ; ; page++) {
                Response<Items> response = Requester.searchItems(type, page);
                if (!response.isSuccessful()) {
                    return new Response<>(response.getResponseCode(), null, response.getError());
                }

                items.addAll(response.getData().getItems());
                if (page == response.getData().getPagination().getLastPage()) {
                    break;
                }
            }
        }

        Paged pagination = new Paged();
        pagination.setCurrentPage(1);
        pagination.setLastPage(1);
        pagination.setTotal(1);
        pagination.setHasMore(false);
        return new Response<>(ResponseCode.SUCCESS, new Items(items, pagination), null);
    }

    /**
     * Searches for items with pagination.
     * @param page The page number to retrieve.
     * @param type The type of items to search for.
     * @return A response containing item details.
     */
    public static Response<Items> searchItems(ItemType type, int page) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.ITEMS,
                null,
                Map.of("type", type.name().toLowerCase(), "page", String.valueOf(page)),
                Items.class
        ).join();
    }

    /**
     * Searches for items based on a query and page number.
     * @param query The search query.
     * @param page The page number to retrieve.
     * @return A response containing item details.
     */
    public static Response<Items> searchItems(String query, int page) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.ITEMS,
                null,
                Map.of("query", query, "page", String.valueOf(page)),
                Items.class
        ).join();
    }

    /**
     * Searches for items based on a query and type.
     * @param query The search query.
     * @param type The type of items to search for.
     * @param page The page number to retrieve.
     * @return A response containing item details.
     */
    public static Response<Items> searchItems(String query, ItemType type, int page) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.ITEMS,
                null,
                Map.of("query", query, "type", type.name().toLowerCase(), "page", String.valueOf(page)),
                Items.class
        ).join();
    }

    /**
     * Retrieves all items by iterating through all item types and pages.
     *
     * <p><b>WARNING:</b> This method is <b>blocking and resource-intensive</b>.</p>
     * <ul>
     *   <li>Can take <b>several minutes</b> to complete, depending on the number of items and API rate limits</li>
     *   <li>Makes hundreds of individual API requests</li>
     *   <li>Should not be called frequently or in time-sensitive contexts</li>
     * </ul>
     *
     * <p><b>Better Alternatives:</b></p>
     * <ul>
     *   <li>Use {@link #searchType(ItemType)} to get items of a specific type</li>
     *   <li>Use {@link #searchTypes(Set)} to get items of multiple types at once</li>
     *   <li>Use {@link #searchItems(String)} for targeted searches</li>
     * </ul>
     *
     * <p><b>Example:</b></p>
     * <pre>
     * // Get all items (not recommended unless necessary)
     * Response&lt;List&lt;Item&gt;&gt; response = Requester.getAllItems();
     * if (response.isSuccessful()) {
     *     List&lt;Item&gt; allItems = response.getData();
     *     System.out.println("Total items: " + allItems.size());
     *     allItems.forEach(item -&gt; System.out.println(item.getName()));
     * } else {
     *     System.err.println("Failed to fetch items: " + response.getError());
     * }
     *
     * // Recommended: Get items of specific types instead
     * Response&lt;Items&gt; weaponsResponse = Requester.searchType(ItemType.WEAPON);
     * </pre>
     *
     * @return A response containing a complete list of all items in the game
     */
    public static Response<List<Item>> getAllItems() {
        List<Item> items = new ArrayList<>();

        for (ItemType type : ItemType.values()) {
            for (int page = 1 ; ; page++) {
                Response<Items> response = Requester.searchItems(type, page);
                if (!response.isSuccessful()) {
                    return new Response<>(response.getResponseCode(), null, response.getError());
                }

                items.addAll(response.getData().getItems());
                if (page == response.getData().getPagination().getLastPage()) {
                    break;
                }
            }
        }

        return new Response<>(ResponseCode.SUCCESS, items, null);
    }



    /**
     * Performs an advanced search for items using fuzzy matching on item names.
     *
     * <p><b>How It Works:</b></p>
     * <ul>
     *   <li>Uses Jaro-Winkler similarity algorithm for fuzzy matching</li>
     *   <li>Tolerates typos and spelling variations in item names</li>
     *   <li>Matches against the default list of all items (cached after first load)</li>
     *   <li>Returns results for the best-matching item name</li>
     * </ul>
     *
     * <p><b>Example:</b></p>
     * <pre>
     * // User types "iron sorwd" (with typo)
     * Response&lt;Items&gt; response = Requester.advancedSearchItems("iron sorwd");
     * // Will find "Iron Sword" and return matching results
     *
     * // Handles partial matches
     * Response&lt;Items&gt; partialMatch = Requester.advancedSearchItems("gold");
     * // Might find "Golden Ring", "Goldsmith's Hammer", etc.
     * </pre>
     *
     * @param query The search query string (will be fuzzy-matched against known item names).
     *              Case-insensitive and tolerant of typos.
     * @return A response containing items matching the best-matched item name,
     *         or an error if no suitable match is found.
     *
     * @see #advancedSearchItems(String, List)
     * @see de.shurablack.jima.util.ItemNameMatcher#getBestMatch(String)
     */
    public static Response<Items> advancedSearchItems(String query) {
        query = ItemNameMatcher.getBestMatch(query);
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.ITEMS,
                null,
                Map.of("query", query),
                Items.class
        ).join();
    }

    /**
     * Performs an advanced search for items using fuzzy matching against specific candidates.
     *
     * <p><b>How It Works:</b></p>
     * <ul>
     *   <li>Uses Jaro-Winkler similarity algorithm for fuzzy matching</li>
     *   <li>Matches the query against the provided list of candidate item names</li>
     *   <li>Returns results for the best-matching candidate name</li>
     *   <li>More flexible than {@link #advancedSearchItems(String)} as you control the candidates</li>
     * </ul>
     *
     * <p><b>Use Cases:</b></p>
     * <ul>
     *   <li>Search within a filtered subset of items</li>
     *   <li>Match against custom item name lists</li>
     *   <li>Implement type-specific advanced search</li>
     * </ul>
     *
     * <p><b>Example:</b></p>
     * <pre>
     * // Get all weapons first
     * Response&lt;Items&gt; weaponResponse = Requester.searchType(ItemType.WEAPON);
     * if (weaponResponse.isSuccessful()) {
     *     List&lt;String&gt; weaponNames = weaponResponse.getData().getItems()
     *         .stream()
     *         .map(Item::getName)
     *         .collect(Collectors.toList());
     *
     *     // Now search within weapons with fuzzy matching
     *     Response&lt;Items&gt; searchResponse = Requester.advancedSearchItems("iron sorwd", weaponNames);
     *     // Will find best matching weapon
     * }
     * </pre>
     *
     * @param query The search query string (will be fuzzy-matched against candidates).
     *              Case-insensitive and tolerant of typos.
     * @param candidates A list of candidate item names to match against.
     *                   If null or empty, returns an empty response.
     * @return A response containing items matching the best-matched candidate name,
     *         or an error if no match is found in the candidates.
     *
     * @see #advancedSearchItems(String)
     * @see de.shurablack.jima.util.ItemNameMatcher#getBestMatch(String, List)
     */
    public static Response<Items> advancedSearchItems(String query, List<String> candidates) {
        query = ItemNameMatcher.getBestMatch(query, candidates);
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.ITEMS,
                null,
                Map.of("query", query),
                Items.class
        ).join();
    }

    /**
     * Inspects an item based on its hashed ID.
     * @param hashedItemId The hashed ID of the item.
     * @return A response containing item inspection details.
     */
    public static Response<ItemInspection> inspectItem(String hashedItemId) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.ITEM_INSPECTION,
                Map.of("hashed_item_id", hashedItemId),
                null,
                ItemInspection.class
        ).join();
    }

    /**
     * Inspects multiple items in parallel for improved efficiency.
     * This method executes all item inspection requests concurrently, which is significantly faster
     * than calling inspectItem() sequentially for each ID, especially with large lists.
     *
     * <p><b>Features:</b></p>
     * <ul>
     *   <li>Executes requests in parallel using CompletableFuture</li>
     *   <li>Blocks until all requests complete</li>
     *   <li>Preserves order - results correspond to input order</li>
     * </ul>
     *
     * <p><b>Example:</b></p>
     * <pre>
     * List&lt;String&gt; itemIds = Arrays.asList("id1", "id2", "id3");
     * List&lt;Response&lt;ItemInspection&gt;&gt; results = Requester.getMultipleItemInspections(itemIds);
     *
     * ResponseList&lt;ItemInspection&gt; list = new ResponseList&lt;&gt;(results);
     * List&lt;ItemInspection&gt; items = list.getSuccessful();
     * items.stream()
     *     .sorted(Comparator.comparing(ItemInspection::getPrice).reversed())
     *     .forEach(item -&gt; System.out.println(item.getName() + ": " + item.getPrice()));
     * </pre>
     *
     * @param itemIds A set of hashed item IDs to inspect.
     * @return A list of Response objects for item inspections.
     */
    public static ResponseList<ItemInspection> getMultipleItemInspections(Set<String> itemIds) {
        List<CompletableFuture<Response<ItemInspection>>> futures = itemIds.stream()
                .map(id -> RequestManager.getInstance().enqueueRequest(
                        Endpoint.ITEM_INSPECTION,
                        Map.of("hashed_item_id", id),
                        null,
                        ItemInspection.class
                ))
                .collect(Collectors.toList());

        return new ResponseList<>(futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList()));
    }

    /**
     * Inspects all items by iterating through all item types and pages.
     *
     * <p><b>WARNING:</b> This method is <b>extremely resource-intensive and blocking</b>.</p>
     * <ul>
     *   <li>Can take <b>many minutes</b> to complete (potentially 10+ minutes)</li>
     *   <li>Makes thousands of individual API requests (one per item)</li>
     *   <li>Significantly higher rate limit usage than {@link #getAllItems()}</li>
     *   <li>Should only be used if detailed information for every item is absolutely required</li>
     * </ul>
     *
     * <p><b>Better Alternatives:</b></p>
     * <ul>
     *   <li>Use {@link #inspectItem(String)} to get details for specific items</li>
     *   <li>Use targeted searches with {@link #searchType(ItemType)} first, then inspect selectively</li>
     * </ul>
     *
     * <p><b>Parameters Explained:</b></p>
     * <ul>
     *   <li><code>cancelOnFailure=true</code>: Stops immediately at first error (useful for debugging)</li>
     *   <li><code>cancelOnFailure=false</code>: Continues to attempt all items even if some fail (useful for gathering maximum data)</li>
     * </ul>
     *
     * <p><b>Example:</b></p>
     * <pre>
     * // Get all item inspections, continuing even if some requests fail
     * Response&lt;List&lt;ItemInspection&gt;&gt; response = Requester.inspectAllItems(false);
     * if (response.isSuccessful()) {
     *     List&lt;ItemInspection&gt; items = response.getData();
     *     System.out.println("Inspected " + items.size() + " items");
     *     items.stream()
     *         .sorted(Comparator.comparing(ItemInspection::getPrice).reversed())
     *         .limit(10)
     *         .forEach(item -&gt; System.out.println(
     *             item.getName() + ": " + item.getPrice() + " gold"
     *         ));
     * } else {
     *     System.err.println("Failed: " + response.getError());
     * }
     * </pre>
     *
     * @param cancelOnFailure If true, returns immediately on the first failed request.
     *                        If false, skips failed items and continues with remaining items.
     *                        Use true for debugging, false for comprehensive data gathering.
     * @return A response containing a list of item inspections. May be incomplete if
     *         {@code cancelOnFailure=false} and some requests fail.
     */
    public static Response<List<ItemInspection>> inspectAllItems(boolean cancelOnFailure) {
        List<ItemInspection> inspections = new ArrayList<>();

        for (ItemType type : ItemType.values()) {
            for (int page = 1 ; ; page++) {
                Response<Items> response = Requester.searchItems(type, page);
                if (!response.isSuccessful() && cancelOnFailure) {
                    return new Response<>(response.getResponseCode(), null, response.getError());
                }

                if (!response.isSuccessful()) {
                    break;
                }

                for (Item item : response.getData().getItems()) {
                    Response<ItemInspection> inspectionResponse = Requester.inspectItem(item.getHashedId());
                    if (!inspectionResponse.isSuccessful() && cancelOnFailure) {
                        return new Response<>(inspectionResponse.getResponseCode(), null, inspectionResponse.getError());
                    }
                    if (!inspectionResponse.isSuccessful()) {
                        continue;
                    }
                    inspections.add(inspectionResponse.getData());
                }

                if (page == response.getData().getPagination().getLastPage()) {
                    break;
                }
            }
        }

        return new Response<>(ResponseCode.SUCCESS, inspections, null);
    }

    /**
     * Retrieves market history for an item with specific tier and market type.
     *
     * <p><b>Parameters Explained:</b></p>
     * <ul>
     *   <li><code>hashedItemId</code>: The unique identifier returned by item search/inspection endpoints</li>
     *   <li><code>tier</code>: The quality tier of the item (typically 0-5, higher = more valuable)</li>
     *   <li><code>type</code>: {@link MarketType#LISTINGS} for sell orders, {@link MarketType#ORDERS} for buy orders</li>
     * </ul>
     *
     * <p><b>Example:</b></p>
     * <pre>
     * // Get sell order history for an item at tier 0
     * Response&lt;MarketHistory&gt; response = Requester.getMarketHistory(
     *     "abc123def456",           // Item ID from search/inspect
     *     0,                        // Tier 0 (basic quality)
     *     MarketType.LISTINGS       // Listing prices (what people are selling for)
     * );
     * if (response.isSuccessful()) {
     *     MarketHistory history = response.getData();
     *     System.out.println("Latest price: " + history.getLatestPrice());
     *     System.out.println("Average price: " + history.getAveragePrice());
     * }
     * </pre>
     *
     * @param hashedItemId The hashed ID of the item (obtained from search or inspect endpoints).
     *                     This is a unique identifier for the specific item.
     * @param tier The quality tier of the item. Typical range: 0-5, where 0 is basic and
     *             higher numbers represent increasingly rare/valuable tiers.
     * @param type The market type to query: {@link MarketType#LISTINGS} for seller prices,
     *             {@link MarketType#ORDERS} for buyer prices.
     * @return A response containing market history data including listing/order information,
     *         pricing trends, and historical records.
     *
     * @see #getMarketHistory(String, MarketType)
     * @see #getMarketListingHistory(String)
     * @see #getMarketOrderHistory(String)
     */
    public static Response<MarketHistory> getMarketHistory(String hashedItemId, int tier, MarketType type) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.ITEM_MARKET_HISTORY,
                Map.of("hashed_item_id", hashedItemId),
                Map.of("tier", String.valueOf(tier), "type", type.name().toLowerCase()),
                MarketHistory.class
        ).join();
    }

    /**
     * Retrieves market history for an item at tier 0 (basic quality).
     *
     * <p>This is a convenience method that automatically uses tier 0. To query other tiers,
     * use {@link #getMarketHistory(String, int, MarketType)} instead.</p>
     *
     * <p><b>Example:</b></p>
     * <pre>
     * // Get listing prices for an item (tier 0)
     * Response&lt;MarketHistory&gt; response = Requester.getMarketHistory(
     *     "abc123def456",
     *     MarketType.LISTINGS
     * );
     * if (response.isSuccessful()) {
     *     MarketHistory history = response.getData();
     *     System.out.println("Current ask: " + history.getLatestPrice());
     * }
     * </pre>
     *
     * @param hashedItemId The hashed ID of the item (obtained from search or inspect endpoints).
     * @param type The market type: {@link MarketType#LISTINGS} for seller prices,
     *             {@link MarketType#ORDERS} for buyer prices.
     * @return A response containing market history data at tier 0.
     *
     * @see #getMarketHistory(String, int, MarketType)
     */
    public static Response<MarketHistory> getMarketHistory(String hashedItemId, MarketType type) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.ITEM_MARKET_HISTORY,
                Map.of("hashed_item_id", hashedItemId),
                Map.of("tier", "0", "type", type.name().toLowerCase()),
                MarketHistory.class
        ).join();
    }

    /**
     * Retrieves market listing history for an item.
     * @param hashedItemId The hashed ID of the item.
     * @return A response containing market listing history details.
     */
    public static Response<MarketHistory> getMarketListingHistory(String hashedItemId) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.ITEM_MARKET_HISTORY,
                Map.of("hashed_item_id", hashedItemId),
                Map.of("tier", "0", "type", MarketType.LISTINGS.name().toLowerCase()),
                MarketHistory.class
        ).join();
    }

    /**
     * Retrieves market order history for an item.
     * @param hashedItemId The hashed ID of the item.
     * @return A response containing market order history details.
     */
    public static Response<MarketHistory> getMarketOrderHistory(String hashedItemId) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.ITEM_MARKET_HISTORY,
                Map.of("hashed_item_id", hashedItemId),
                Map.of("tier", "0", "type", MarketType.ORDERS.name().toLowerCase()),
                MarketHistory.class
        ).join();
    }

    /**
     * Retrieves character information based on its hashed ID.
     * @param hashedCharacterId The hashed ID of the character.
     * @return A response containing character details.
     */
    public static Response<CharacterView> getCharacter(String hashedCharacterId) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.CHARACTER_VIEW,
                Map.of("hashed_character_id", hashedCharacterId),
                null,
                CharacterView.class
        ).join();
    }

    /**
     * Retrieves character metrics based on its hashed ID.
     * @param hashedCharacterId The hashed ID of the character.
     * @return A response containing character metrics.
     */
    public static Response<CharacterMetric> getCharacterMetrics(String hashedCharacterId) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.CHARACTER_METRICS,
                Map.of("hashed_character_id", hashedCharacterId),
                null,
                CharacterMetric.class
        ).join();
    }

    /**
     * Retrieves character effects based on its hashed ID.
     * @param hashedCharacterId The hashed ID of the character.
     * @return A response containing character effects.
     */
    public static Response<CharacterEffects> getCharacterEffects(String hashedCharacterId) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.CHARACTER_EFFECTS,
                Map.of("hashed_character_id", hashedCharacterId),
                null,
                CharacterEffects.class
        ).join();
    }

    /**
     * Retrieves alternate characters for a character based on its hashed ID.
     * @param hashedCharacterId The hashed ID of the character.
     * @return A response containing alternate character details.
     */
    public static Response<CharacterAlts> getCharacterAlts(String hashedCharacterId) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.CHARACTER_ALT_CHARACTERS,
                Map.of("hashed_character_id", hashedCharacterId),
                null,
                CharacterAlts.class
        ).join();
    }

    /**
     * Retrieves the museum information of a character based on its hashed ID.
     * @param hashedCharacterId The hashed ID of the character.
     * @return A response containing museum details.
     */
    public static Response<CharacterMuseum> getCharacterMuseum(String hashedCharacterId) {
        return RequestManager.getInstance().enqueueRequest(Endpoint.CHARACTER_MUSEUM,
                Map.of("hashed_character_id", hashedCharacterId),
                null,
                CharacterMuseum.class
        ).join();
    }

    /**
     * Retrieves the museum information of a character for a specific page.
     * @param hashedCharacterId The hashed ID of the character.
     * @param page The page number.
     * @return A response containing museum details.
     */
    public static Response<CharacterMuseum> getCharacterMuseum(String hashedCharacterId, int page) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.CHARACTER_MUSEUM,
                Map.of("hashed_character_id", hashedCharacterId),
                Map.of("page", String.valueOf(page)),
                CharacterMuseum.class
        ).join();
    }

    /**
     * Retrieves the museum information of a character for a specific category.
     * @param hashedCharacterId The hashed ID of the character.
     * @param category The museum category.
     * @return A response containing museum details.
     */
    public static Response<CharacterMuseum> getCharacterMuseum(String hashedCharacterId, MuseumCategory category) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.CHARACTER_MUSEUM,
                Map.of("hashed_character_id", hashedCharacterId),
                Map.of("category", category.name()),
                CharacterMuseum.class
        ).join();
    }

    /**
     * Retrieves the museum information of a character for a specific category and page.
     * @param hashedCharacterId The hashed ID of the character.
     * @param category The museum category.
     * @param page The page number.
     * @return A response containing museum details.
     */
    public static Response<CharacterMuseum> getCharacterMuseum(String hashedCharacterId, MuseumCategory category, int page) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.CHARACTER_MUSEUM,
                Map.of("hashed_character_id", hashedCharacterId),
                Map.of("category", category.name(), "page", String.valueOf(page)),
                CharacterMuseum.class
        ).join();
    }

    /**
     * Retrieves the current action of a character based on its hashed ID.
     * @param hashedCharacterId The hashed ID of the character.
     * @return A response containing the character's current action.
     */
    public static Response<CharacterAction> getCharacterAction(String hashedCharacterId) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.CHARACTER_CURRENT_ACTION,
                Map.of("hashed_character_id", hashedCharacterId),
                null,
                CharacterAction.class
        ).join();
    }

    /**
     * Retrieves the pets of a character based on its hashed ID.
     * @param hashedCharacterId The hashed ID of the character.
     * @return A response containing character pet details.
     */
    public static Response<CharacterPets> getCharacterPets(String hashedCharacterId) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.CHARACTER_PETS,
                Map.of("hashed_character_id", hashedCharacterId),
                null,
                CharacterPets.class
        ).join();
    }

    /**
     * Retrieves companion exchange listings.
     * @return A response containing companion exchange listing details.
     */
    public static Response<Listings> getCompanionExchangeListings() {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.PET_EXCHANGE_LISTINGS,
                null,
                null,
                Listings.class
        ).join();
    }

    /**
     * Retrieves companion exchange listings for a specific page.
     * @param page The page number to retrieve.
     * @return A response containing companion exchange listing details.
     */
    public static Response<Listings> getCompanionExchangeListings(int page) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.PET_EXCHANGE_LISTINGS,
                null,
                Map.of("page", String.valueOf(page)),
                Listings.class
        ).join();
    }

    /**
     * Fetches multiple characters in parallel for improved efficiency.
     * This method executes all character requests concurrently, which is significantly faster
     * than calling getCharacter() sequentially for each ID, especially with large lists.
     *
     * <p><b>Features:</b></p>
     * <ul>
     *   <li>Executes requests in parallel using CompletableFuture</li>
     *   <li>Blocks until all requests complete or an error occurs</li>
     *   <li>Preserves order - results correspond to input order</li>
     * </ul>
     *
     * <p><b>Example:</b></p>
     * <pre>
     * List&lt;String&gt; characterIds = Arrays.asList("char1", "char2", "char3");
     * List&lt;Response&lt;CharacterView&gt;&gt; results = Requester.getMultipleCharacters(characterIds);
     *
     * results.forEach(response -&gt; {
     *     if (response.isSuccessful()) {
     *         System.out.println("Level: " + response.getData().getCharacter().getLevel());
     *     } else {
     *         System.err.println("Failed: " + response.getError());
     *     }
     * });
     * </pre>
     *
     * @param characterIds A set of character IDs to fetch.
     * @return A list of Response objects corresponding to each character ID, in the same order as input.
     */
    public static List<Response<CharacterView>> getMultipleCharacters(Set<String> characterIds) {
        List<CompletableFuture<Response<CharacterView>>> futures = characterIds.stream()
                .map(id -> RequestManager.getInstance().enqueueRequest(
                        Endpoint.CHARACTER_VIEW,
                        Map.of("hashed_character_id", id),
                        null,
                        CharacterView.class
                ))
                .collect(Collectors.toList());

        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves guild information based on its ID.
     * @param id The ID of the guild.
     * @return A response containing guild details.
     */
    public static Response<GuildView> getGuild(int id) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.GUILD_INFORMATION,
                Map.of("id", String.valueOf(id)),
                null,
                GuildView.class
        ).join();
    }

    /**
     * Fetches multiple guilds in parallel for improved efficiency.
     * This method executes all guild requests concurrently, which is significantly faster
     * than calling getGuild() sequentially for each ID, especially with large lists.
     *
     * <p><b>Features:</b></p>
     * <ul>
     *   <li>Executes requests in parallel using CompletableFuture</li>
     *   <li>Blocks until all requests complete</li>
     *   <li>Preserves order - results correspond to input order</li>
     * </ul>
     *
     * <p><b>Example:</b></p>
     * <pre>
     * List&lt;Integer&gt; guildIds = Arrays.asList(1, 2, 3);
     * List&lt;Response&lt;GuildView&gt;&gt; results = Requester.getMultipleGuilds(guildIds);
     *
     * ResponseList&lt;GuildView&gt; list = new ResponseList&lt;&gt;(results);
     * List&lt;GuildView&gt; successful = list.getSuccessful();
     * System.out.println("Loaded " + successful.size() + " guilds");
     * </pre>
     *
     * @param guildIds A set of guild IDs to fetch.
     * @return A list of Response objects corresponding to each guild ID, in the same order as input.
     */
    public static List<Response<GuildView>> getMultipleGuilds(Set<Integer> guildIds) {
        List<CompletableFuture<Response<GuildView>>> futures = guildIds.stream()
                .map(id -> RequestManager.getInstance().enqueueRequest(
                        Endpoint.GUILD_INFORMATION,
                        Map.of("id", String.valueOf(id)),
                        null,
                        GuildView.class
                ))
                .collect(Collectors.toList());

        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves guild members based on the guild ID.
     * @param id The ID of the guild.
     * @return A response containing guild member details.
     */
    public static Response<GuildMembers> getGuildMembers(int id) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.GUILD_MEMBERS,
                Map.of("id", String.valueOf(id)),
                null,
                GuildMembers.class
        ).join();
    }

    /**
     * Fetches guild members for multiple guilds in parallel for improved efficiency.
     * This method executes all guild member requests concurrently, which is significantly faster
     * than calling getGuildMembers() sequentially for each guild.
     *
     * <p><b>Features:</b></p>
     * <ul>
     *   <li>Executes requests in parallel using CompletableFuture</li>
     *   <li>Blocks until all requests complete</li>
     *   <li>Preserves order - results correspond to input order</li>
     * </ul>
     *
     * <p><b>Example:</b></p>
     * <pre>
     * List&lt;Integer&gt; guildIds = Arrays.asList(1, 2, 3);
     * List&lt;Response&lt;GuildMembers&gt;&gt; results = Requester.getMultipleGuildMembers(guildIds);
     *
     * ResponseList&lt;GuildMembers&gt; list = new ResponseList&lt;&gt;(results);
     * list.printSummary();  // Shows success rate
     * List&lt;GuildMembers&gt; successful = list.getSuccessful();
     * </pre>
     *
     * @param guildIds A set of guild IDs to fetch members for.
     * @return A list of Response objects for guild members, in the same order as input.
     */
    public static List<Response<GuildMembers>> getMultipleGuildMembers(Set<Integer> guildIds) {
        List<CompletableFuture<Response<GuildMembers>>> futures = guildIds.stream()
                .map(id -> RequestManager.getInstance().enqueueRequest(
                        Endpoint.GUILD_MEMBERS,
                        Map.of("id", String.valueOf(id)),
                        null,
                        GuildMembers.class
                ))
                .collect(Collectors.toList());

        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    /**
     * Fetches the current Energizing Pool status and effects for a guild.
     *
     * The API key owner must be a guild member with energizing pool view permission.
     *
     * @param id The guild ID to fetch pool information for
     * @return Response containing EnergizingPoolInfo with status and effects, or error code
     *
     * @see EnergizingPoolInfo For response data structure
     * @see ParallelRequester#getEnergizingPoolInfo(int) For async variant
     */
    public static Response<EnergizingPoolInfo> getEnergizingPoolInfo(int id) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.GUILD_ENERGIZING_POOL_INFORMATION,
                Map.of("id", String.valueOf(id)),
                null,
                EnergizingPoolInfo.class
        ).join();
    }

    /**
     * Retrieves detailed guild hall information including layout, upgrades, and blueprints.
     *
     * <p><b>Authorization:</b></p>
     * Authorization matches the in-game guild hall view:
     * <ul>
     *   <li><b>Own Guild Hall:</b> View allowed if your guild rank has guild hall view permission</li>
     *   <li><b>Other guild halls:</b> View allowed only if that guild has public guild hall visibility enabled</li>
     * </ul>
     *
     * @param id The guild ID whose guild hall to fetch
     * @return A response containing guild hall details with upgrades, blueprints, and slot information
     *
     * @see GuildHallView For the response structure
     * @see de.shurablack.jima.model.guild.hall.GuildHall For guild hall data model
     * @see ParallelRequester#getGuildHall(int) For non-blocking variant
     */
    public static Response<GuildHallView> getGuildHall(int id) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.GUILD_HALL,
                Map.of("id", String.valueOf(id)),
                null,
                GuildHallView.class
        ).join();
    }

    /**
     * Retrieves the current guild conquest information.
     * @return A response containing guild conquest details.
     */
    public static Response<GuildConquest> getCurrentGuildConquest() {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.GUILD_CONQUESTS,
                null,
                null,
                GuildConquest.class
        ).join();
    }

    /**
     * Retrieves guild conquest information for a specific season.
     * @param season The season number.
     * @return A response containing guild conquest details.
     */
    public static Response<GuildConquest> getGuildConquestBySeason(int season) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.GUILD_CONQUESTS,
                null,
                Map.of("season_number", String.valueOf(season)),
                GuildConquest.class
        ).join();
    }

    /**
     * Retrieves guild conquest inspection details for a specific zone.
     * @param zone The location type representing the zone.
     * @return A response containing guild conquest inspection details.
     */
    public static Response<GuildConquestInspection> getGuildConquestInspection(LocationType zone) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.GUILD_CONQUEST_ZONE_INSPECTION,
                Map.of("zone_id", zone.getId()),
                null,
                GuildConquestInspection.class
        ).join();
    }

    /**
     * Retrieves guild conquest inspection details for a specific zone and season.
     * @param zone The location type representing the zone.
     * @param season The season number.
     * @return A response containing guild conquest inspection details.
     */
    public static Response<GuildConquestInspection> getGuildConquestInspection(LocationType zone, int season) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.GUILD_CONQUEST_ZONE_INSPECTION,
                Map.of("zone_id", zone.getId()),
                Map.of("season_number", String.valueOf(season)),
                GuildConquestInspection.class
        ).join();
    }

    /**
     * Retrieves shrine progress information.
     * @return A response containing shrine progress details.
     */
    public static Response<ShrineInfo> getShrineInfo() {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.SHRINE_PROGRESS,
                null,
                null,
                ShrineInfo.class
        ).join();
    }

    /**
     * Executes a group of requests with controlled delays and token management.
     * Requests are processed sequentially or in batches with automatic stalling when tokens drop below the minimum.
     *
     * <p><b>Features:</b></p>
     * <ul>
     *   <li>Sequential execution with configurable delays between requests</li>
     *   <li>Batch-based execution - execute X requests then wait</li>
     *   <li>Automatic stalling when token count drops below minimum threshold</li>
     *   <li>Transparent resumption when tokens are restored</li>
     *   <li>Non-blocking - returns futures immediately while background processing continues</li>
     * </ul>
     *
      * <p><b>Example 1 - Sequential with delays using Response requests:</b></p>
      * <pre>{@code
      * RequestGroup group = new RequestGroup()
      *     .withDelay(1000)                      // 1-second delay between requests
      *     .withMinTokensAllowed(15)             // Keep minimum 15 tokens
      *     .addResponseRequest(() -> Requester.inspectItem("id1"))
      *     .addResponseRequest(() -> Requester.inspectItem("id2"))
      *     .addResponseRequest(() -> Requester.inspectItem("id3"));
      *
      * List<CompletableFuture<?>> futures = Requester.executeRequestGroup(group);
      * group.awaitCompletion(5, TimeUnit.MINUTES);
      * }
      * </pre>
     *
      * <p><b>Example 2 - Batch execution with Response requests:</b></p>
      * <pre>{@code
      * RequestGroup group = new RequestGroup()
      *     .withBatchSize(10)                     // Execute 10 requests at a time
      *     .withWaitMsBetweenBatches(5000)        // Wait 5 seconds between batches
      *     .withMinTokensAllowed(5)               // Ensure 5 tokens available before each request
      *     .withDelay(500);                       // 500ms delay within batch
      *
      * for (int i = 0; i < 100; i++) {
      *     group.addResponseRequest(() -> Requester.inspectItem("id" + i));
      * }
      * // Execution flow:
      * // - Execute requests 1-10 (with 500ms delays between)
      * // - Wait 5 seconds
      * // - Execute requests 11-20 (with 500ms delays between)
      * // - Wait 5 seconds
      * // - ... and so on
      *
      * List<CompletableFuture<?>> futures = Requester.executeRequestGroup(group);
      * group.awaitCompletion(10, TimeUnit.MINUTES);
      * }
      * </pre>
     *
      * <p><b>Example 3 - Batch execution with token recovery stalling:</b></p>
      * <pre>{@code
      * RequestGroup group = new RequestGroup()
      *     .withBatchSize(15)                     // Execute 15 requests in each batch
      *     .withWaitMsBetweenBatches(0)           // No explicit wait (relies on token stalling)
      *     .withMinTokensAllowed(10);             // Stall next request if tokens < 10
      *
      * for (int i = 0; i < 100; i++) {
      *     group.addResponseRequest(() -> Requester.getCharacter("charId" + i));
      * }
      * // Execution flow:
      * // - Execute batch 1 (requests 1-15)
      * // - If tokens drop below 10 before batch 2, stall until tokens recover
      * // - Resume with batch 2, and so on
      * }
      * </pre>
     *
     * @param group The RequestGroup containing requests to execute
     * @return A list of CompletableFutures for each request in the group.
     *         Each future represents the asynchronous completion of a request.
     *
     * @see RequestGroup
     * @see RequestGroup#withDelay(long)
     * @see RequestGroup#withMinTokensAllowed(int)
     * @see RequestGroup#withBatchSize(int)
     * @see RequestGroup#withWaitMsBetweenBatches(long)
     * @see RequestGroup#addResponseRequest(Supplier) 
     * @see RequestGroup#addResponseRequests(Collection)
     */
    public static List<CompletableFuture<?>> executeRequestGroup(RequestGroup group) {
        return RequestManager.getInstance().enqueueRequestGroup(group);
    }

    /**
     * Executes a group of requests and blocks until all requests complete.
     * This is a convenience method that combines executeRequestGroup() and awaitCompletion().
     *
     * <p><b>Behavior:</b></p>
     * <ul>
     *   <li>Submits the request group for processing</li>
     *   <li>Blocks the calling thread until all requests complete</li>
     *   <li>Default timeout: 10 minutes</li>
     *   <li>Returns false if timeout is exceeded (rather than throwing)</li>
     * </ul>
     *
      * <p><b>Example - Simple blocking execution:</b></p>
      * <pre>{@code
      * RequestGroup group = new RequestGroup()
      *     .withBatchSize(10)
      *     .withWaitMsBetweenBatches(5000)
      *     .withMinTokensAllowed(10);
      *
      * for (int i = 0; i < 100; i++) {
      *     group.addResponseRequest(() -> Requester.inspectItem("id" + i));
      * }
      *
      * // Blocks until all 100 requests complete (or 10 minutes pass)
      * boolean success = Requester.executeRequestGroupBlocking(group);
      * if (success) {
      *     System.out.println("All requests completed!");
      * } else {
      *     System.err.println("Timeout: requests did not complete within 10 minutes");
      * }
      * }
      * </pre>
     *
     * @param group The RequestGroup containing requests to execute
     * @return true if all requests completed within 10 minutes,
     *         false if timeout was exceeded
     *
     * @see #executeRequestGroup(RequestGroup)
     * @see #executeRequestGroupBlocking(RequestGroup, long, java.util.concurrent.TimeUnit)
     */
    public static boolean executeRequestGroupBlocking(RequestGroup group) {
        try {
            return executeRequestGroupBlocking(group, 10, java.util.concurrent.TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * Executes a group of requests and blocks until all requests complete or timeout is reached.
     * This is a convenience method that combines executeRequestGroup() and awaitCompletion().
     *
     * <p><b>Behavior:</b></p>
     * <ul>
     *   <li>Submits the request group for processing</li>
     *   <li>Blocks the calling thread until all requests complete or timeout</li>
     *   <li>Returns immediately if all requests finish before timeout</li>
     *   <li>Throws InterruptedException if the waiting thread is interrupted</li>
     * </ul>
     *
      * <p><b>Example:</b></p>
      * <pre>
      * RequestGroup group = new RequestGroup()
      *     .withBatchSize(20)
      *     .withWaitMsBetweenBatches(2000)
      *     .addResponseRequest(() -&gt; Requester.getWorldBosses())
      *     .addResponseRequest(() -&gt; Requester.getDungeons());
     *
     * try {
     *     // Wait up to 5 minutes for completion
     *     boolean success = Requester.executeRequestGroupBlocking(group, 5, TimeUnit.MINUTES);
     *     if (success) {
     *         System.out.println("Request group completed successfully");
     *     } else {
     *         System.out.println("Request group timed out after 5 minutes");
     *     }
     * } catch (InterruptedException e) {
     *     System.err.println("Execution was interrupted: " + e.getMessage());
     * }
     * </pre>
     *
     * @param group The RequestGroup containing requests to execute
     * @param timeout The maximum time to wait for completion
     * @param unit The time unit of the timeout parameter
     * @return true if all requests completed within the timeout,
     *         false if timeout was exceeded
     * @throws InterruptedException if the waiting thread is interrupted
     *
     * @see #executeRequestGroup(RequestGroup)
     * @see #executeRequestGroupBlocking(RequestGroup)
     */
    public static boolean executeRequestGroupBlocking(RequestGroup group, long timeout, TimeUnit unit) throws InterruptedException {
        executeRequestGroup(group);
        return group.awaitCompletion(timeout, unit);
    }

}


