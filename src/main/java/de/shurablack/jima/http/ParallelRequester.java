package de.shurablack.jima.http;

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
import de.shurablack.jima.model.item.ItemInspection;
import de.shurablack.jima.model.item.Items;
import de.shurablack.jima.model.item.market.MarketHistory;
import de.shurablack.jima.model.shrine.ShrineInfo;
import de.shurablack.jima.model.world.WorldLocations;
import de.shurablack.jima.util.Token;
import de.shurablack.jima.util.types.ItemType;
import de.shurablack.jima.util.types.LocationType;
import de.shurablack.jima.util.types.MarketType;
import de.shurablack.jima.util.types.MuseumCategory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * A utility class for making parallel requests to various API endpoints.
 * This class provides static methods to interact with different parts of the API,
 * allowing for concurrent data retrieval.
 */
public class ParallelRequester {

    private ParallelRequester() {
        // Private constructor to prevent instantiation
    }

    /**
     * Retrieves authentication information.
     * @return A response containing authentication details.
     */
    public static CompletableFuture<Response<Authentication>> getAuthentication() {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.AUTHENTICATE,
                null,
                null,
                Authentication.class
        );
    }

    /**
     * Retrieves authentication information using a provided token.
     * @param token The authentication token.
     * @return A response containing authentication details.
     */
    public static CompletableFuture<Response<Authentication>> getAuthentication(Token token) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.AUTHENTICATE,
                null,
                null,
                Authentication.class,
                token
        );
    }

    /**
     * Asynchronously retrieves all world locations with extended weather forecast data.
     *
     * @return A {@code CompletableFuture<Response<WorldLocations>>} that completes with all
     *         world locations and weather data, or an error response if the request fails
     */
    public static CompletableFuture<Response<WorldLocations>> getWorldLocations() {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.WORLD_LOCATIONS_LIST,
                null,
                null,
                WorldLocations.class
        );
    }

    /**
     * Retrieves information about world bosses.
     * @return A response containing world boss details.
     */
    public static CompletableFuture<Response<WorldBosses>> getWorldBosses() {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.WORLD_BOSSES,
                null,
                null,
                WorldBosses.class
        );
    }

    /**
     * Retrieves information about dungeons.
     * @return A response containing dungeon details.
     */
    public static CompletableFuture<Response<Dungeons>> getDungeons() {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.DUNGEONS,
                null,
                null,
                Dungeons.class
        );
    }

    /**
     * Retrieves information about enemies.
     * @return A response containing enemy details.
     */
    public static CompletableFuture<Response<Enemies>> getEnemies() {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.ENEMIES,
                null,
                null,
                Enemies.class
        );
    }

    /**
     * Searches for items based on various criteria.
     * @param query The search query.
     * @return A response containing item details.
     */
    public static CompletableFuture<Response<Items>> searchItems(String query) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.ITEMS,
                null,
                Map.of("query", query),
                Items.class
        );
    }

    /**
     * Searches for items of a specific type.
     * @param type The type of items to search for.
     * @return A response containing item details.
     */
    public static CompletableFuture<Response<Items>> searchItems(ItemType type) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.ITEMS,
                null,
                Map.of("type", type.name().toLowerCase()),
                Items.class
        );
    }

    /**
     * Searches for items with pagination.
     * @param page The page number to retrieve.
     * @param type The type of items to search for.
     * @return A response containing item details.
     */
    public static CompletableFuture<Response<Items>> searchItems(ItemType type, int page) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.ITEMS,
                null,
                Map.of("type", type.name().toLowerCase(), "page", String.valueOf(page)),
                Items.class
        );
    }

    /**
     * Searches for items based on a query and page number.
     * @param query The search query.
     * @param page The page number to retrieve.
     * @return A response containing item details.
     */
    public static CompletableFuture<Response<Items>> searchItems(String query, int page) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.ITEMS,
                null,
                Map.of("query", query, "page", String.valueOf(page)),
                Items.class
        );
    }

    /**
     * Searches for items based on a query and type.
     * @param query The search query.
     * @param type The type of items to search for.
     * @param page The page number to retrieve.
     * @return A response containing item details.
     */
    public static CompletableFuture<Response<Items>> searchItems(String query, ItemType type, int page) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.ITEMS,
                null,
                Map.of("query", query, "type", type.name().toLowerCase(), "page", String.valueOf(page)),
                Items.class
        );
    }

    /**
     * Inspects an item based on its hashed ID.
     * @param hashedItemId The hashed ID of the item.
     * @return A response containing item inspection details.
     */
    public static CompletableFuture<Response<ItemInspection>> inspectItem(String hashedItemId) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.ITEM_INSPECTION,
                Map.of("hashed_item_id", hashedItemId),
                null,
                ItemInspection.class
        );
    }

    /**
     * Retrieves market history for an item.
     * @param hashedItemId The hashed ID of the item.
     * @param tier The tier of the item.
     * @param type The market type (listings or orders).
     * @return A response containing market history details.
     */
    public static CompletableFuture<Response<MarketHistory>> getMarketHistory(String hashedItemId, int tier, MarketType type) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.ITEM_MARKET_HISTORY,
                Map.of("hashed_item_id", hashedItemId),
                Map.of("tier", String.valueOf(tier), "type", type.name().toLowerCase()),
                MarketHistory.class
        );
    }

    /**
     * Retrieves market history for an item with a default tier of 0.
     * @param hashedItemId The hashed ID of the item.
     * @param type The market type (listings or orders).
     * @return A response containing market history details.
     */
    public static CompletableFuture<Response<MarketHistory>> getMarketHistory(String hashedItemId, MarketType type) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.ITEM_MARKET_HISTORY,
                Map.of("hashed_item_id", hashedItemId),
                Map.of("tier", "0", "type", type.name().toLowerCase()),
                MarketHistory.class
        );
    }

    /**
     * Retrieves market listing history for an item.
     * @param hashedItemId The hashed ID of the item.
     * @return A response containing market listing history details.
     */
    public static CompletableFuture<Response<MarketHistory>> getMarketListingHistory(String hashedItemId) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.ITEM_MARKET_HISTORY,
                Map.of("hashed_item_id", hashedItemId),
                Map.of("tier", "0", "type", MarketType.LISTINGS.name().toLowerCase()),
                MarketHistory.class
        );
    }

    /**
     * Retrieves market order history for an item.
     * @param hashedItemId The hashed ID of the item.
     * @return A response containing market order history details.
     */
    public static CompletableFuture<Response<MarketHistory>> getMarketOrderHistory(String hashedItemId) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.ITEM_MARKET_HISTORY,
                Map.of("hashed_item_id", hashedItemId),
                Map.of("tier", "0", "type", MarketType.ORDERS.name().toLowerCase()),
                MarketHistory.class
        );
    }

    /**
     * Retrieves character information based on its hashed ID.
     * @param hashedCharacterId The hashed ID of the character.
     * @return A response containing character details.
     */
    public static CompletableFuture<Response<CharacterView>> getCharacter(String hashedCharacterId) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.CHARACTER_VIEW,
                Map.of("hashed_character_id", hashedCharacterId),
                null,
                CharacterView.class
        );
    }

    /**
     * Retrieves character metrics based on its hashed ID.
     * @param hashedCharacterId The hashed ID of the character.
     * @return A response containing character metrics.
     */
    public static CompletableFuture<Response<CharacterMetric>> getCharacterMetrics(String hashedCharacterId) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.CHARACTER_METRICS,
                Map.of("hashed_character_id", hashedCharacterId),
                null,
                CharacterMetric.class
        );
    }

    /**
     * Retrieves character effects based on its hashed ID.
     * @param hashedCharacterId The hashed ID of the character.
     * @return A response containing character effects.
     */
    public static CompletableFuture<Response<CharacterEffects>> getCharacterEffects(String hashedCharacterId) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.CHARACTER_EFFECTS,
                Map.of("hashed_character_id", hashedCharacterId),
                null,
                CharacterEffects.class
        );
    }

    /**
     * Retrieves alternate characters for a character based on its hashed ID.
     * @param hashedCharacterId The hashed ID of the character.
     * @return A response containing alternate character details.
     */
    public static CompletableFuture<Response<CharacterAlts>> getCharacterAlts(String hashedCharacterId) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.CHARACTER_ALT_CHARACTERS,
                Map.of("hashed_character_id", hashedCharacterId),
                null,
                CharacterAlts.class
        );
    }

    /**
     * Retrieves the museum information of a character based on its hashed ID.
     * @param hashedCharacterId The hashed ID of the character.
     * @return A response containing museum details.
     */
    public static CompletableFuture<Response<CharacterMuseum>> getCharacterMuseum(String hashedCharacterId) {
        return RequestManager.getInstance().enqueueRequest(Endpoint.CHARACTER_MUSEUM,
                Map.of("hashed_character_id", hashedCharacterId),
                null,
                CharacterMuseum.class
        );
    }

    /**
     * Retrieves the museum information of a character for a specific page.
     * @param hashedCharacterId The hashed ID of the character.
     * @param page The page number.
     * @return A response containing museum details.
     */
    public static CompletableFuture<Response<CharacterMuseum>> getCharacterMuseum(String hashedCharacterId, int page) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.CHARACTER_MUSEUM,
                Map.of("hashed_character_id", hashedCharacterId),
                Map.of("page", String.valueOf(page)),
                CharacterMuseum.class
        );
    }

    /**
     * Retrieves the museum information of a character for a specific category.
     * @param hashedCharacterId The hashed ID of the character.
     * @param category The museum category.
     * @return A response containing museum details.
     */
    public static CompletableFuture<Response<CharacterMuseum>> getCharacterMuseum(String hashedCharacterId, MuseumCategory category) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.CHARACTER_MUSEUM,
                Map.of("hashed_character_id", hashedCharacterId),
                Map.of("category", category.name()),
                CharacterMuseum.class
        );
    }

    /**
     * Retrieves the museum information of a character for a specific category and page.
     * @param hashedCharacterId The hashed ID of the character.
     * @param category The museum category.
     * @param page The page number.
     * @return A response containing museum details.
     */
    public static CompletableFuture<Response<CharacterMuseum>> getCharacterMuseum(String hashedCharacterId, MuseumCategory category, int page) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.CHARACTER_MUSEUM,
                Map.of("hashed_character_id", hashedCharacterId),
                Map.of("category", category.name(), "page", String.valueOf(page)),
                CharacterMuseum.class
        );
    }

    /**
     * Retrieves the current action of a character based on its hashed ID.
     * @param hashedCharacterId The hashed ID of the character.
     * @return A response containing the character's current action.
     */
    public static CompletableFuture<Response<CharacterAction>> getCharacterAction(String hashedCharacterId) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.CHARACTER_CURRENT_ACTION,
                Map.of("hashed_character_id", hashedCharacterId),
                null,
                CharacterAction.class
        );
    }

    /**
     * Retrieves the pets of a character based on its hashed ID.
     * @param hashedCharacterId The hashed ID of the character.
     * @return A response containing character pet details.
     */
    public static CompletableFuture<Response<CharacterPets>> getCharacterPets(String hashedCharacterId) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.CHARACTER_PETS,
                Map.of("hashed_character_id", hashedCharacterId),
                null,
                CharacterPets.class
        );
    }

    /**
     * Retrieves guild information based on its ID.
     * @param id The ID of the guild.
     * @return A response containing guild details.
     */
    public static CompletableFuture<Response<GuildView>> getGuild(int id) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.GUILD_INFORMATION,
                Map.of("id", String.valueOf(id)),
                null,
                GuildView.class
        );
    }

    /**
     * Retrieves guild members based on the guild ID.
     * @param id The ID of the guild.
     * @return A response containing guild member details.
     */
    public static CompletableFuture<Response<GuildMembers>> getGuildMembers(int id) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.GUILD_MEMBERS,
                Map.of("id", String.valueOf(id)),
                null,
                GuildMembers.class
        );
    }

    /**
     * Asynchronously fetches the current Energizing Pool status and effects for a guild.
     *
     * The API key owner must be a guild member with energizing pool view permission.
     *
     * @param id The guild ID to fetch pool information for
     * @return CompletableFuture resolving to EnergizingPoolInfo with status and effects, or error code
     *
     * @see EnergizingPoolInfo For response data structure
     * @see Requester#getEnergizingPoolInfo(int) For synchronous variant
     */
    public static CompletableFuture<Response<EnergizingPoolInfo>> getEnergizingPoolInfo(int id) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.GUILD_ENERGIZING_POOL_INFORMATION,
                Map.of("id", String.valueOf(id)),
                null,
                EnergizingPoolInfo.class
        );
    }

    /**
     * Retrieves detailed guild hall information including layout, upgrades, and blueprints (non-blocking).
     *
     * <p><b>Authorization:</b></p>
     * Authorization matches the in-game guild hall view:
     * <ul>
     *   <li><b>Own Guild Hall:</b> View allowed if your guild rank has guild hall view permission</li>
     *   <li><b>Other guild halls:</b> View allowed only if that guild has public guild hall visibility enabled</li>
     * </ul>
     *
     * <p><b>Async Behavior:</b></p>
     * Returns a CompletableFuture for non-blocking operation. Combine with other parallel
     * requests using CompletableFuture.allOf() for efficient batch operations.
     *
     * @param id The guild ID whose guild hall to fetch
     * @return A CompletableFuture that resolves to guild hall details with upgrades, blueprints, and slot information
     *
     * @see GuildHallView For the response structure
     * @see de.shurablack.jima.model.guild.hall.GuildHall For guild hall data model
     * @see Requester#getGuildHall(int) For blocking variant
     */
    public static CompletableFuture<Response<GuildHallView>> getGuildHall(int id) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.GUILD_HALL,
                Map.of("id", String.valueOf(id)),
                null,
                GuildHallView.class
        );
    }

    /**
     * Retrieves the current guild conquest information.
     * @return A response containing guild conquest details.
     */
    public static CompletableFuture<Response<GuildConquest>> getCurrentGuildConquest() {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.GUILD_CONQUESTS,
                null,
                null,
                GuildConquest.class
        );
    }

    /**
     * Retrieves guild conquest information for a specific season.
     * @param season The season number.
     * @return A response containing guild conquest details.
     */
    public static CompletableFuture<Response<GuildConquest>> getGuildConquestBySeason(int season) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.GUILD_CONQUESTS,
                null,
                Map.of("season_number", String.valueOf(season)),
                GuildConquest.class
        );
    }

    /**
     * Retrieves guild conquest inspection details for a specific zone.
     * @param zone The location type representing the zone.
     * @return A response containing guild conquest inspection details.
     */
    public static CompletableFuture<Response<GuildConquestInspection>> getGuildConquestInspection(LocationType zone) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.GUILD_CONQUEST_ZONE_INSPECTION,
                Map.of("zone_id", zone.getId()),
                null,
                GuildConquestInspection.class
        );
    }

    /**
     * Retrieves guild conquest inspection details for a specific zone and season.
     * @param zone The location type representing the zone.
     * @param season The season number.
     * @return A response containing guild conquest inspection details.
     */
    public static CompletableFuture<Response<GuildConquestInspection>> getGuildConquestInspection(LocationType zone, int season) {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.GUILD_CONQUEST_ZONE_INSPECTION,
                Map.of("zone_id", zone.getId()),
                Map.of("season_number", String.valueOf(season)),
                GuildConquestInspection.class
        );
    }

    /**
     * Retrieves shrine progress information.
     * @return A response containing shrine progress details.
     */
    public static CompletableFuture<Response<ShrineInfo>> getShrineInfo() {
        return RequestManager.getInstance().enqueueRequest(
                Endpoint.SHRINE_PROGRESS,
                null,
                null,
                ShrineInfo.class
        );
    }

}
