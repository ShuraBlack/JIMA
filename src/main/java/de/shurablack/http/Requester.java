package de.shurablack.http;

import de.shurablack.model.auth.Authentication;
import de.shurablack.model.character.CharacterAction;
import de.shurablack.model.character.CharacterAlts;
import de.shurablack.model.character.effect.CharacterEffects;
import de.shurablack.model.character.metric.CharacterMetric;
import de.shurablack.model.character.museum.CharacterMuseum;
import de.shurablack.model.character.pet.CharacterPets;
import de.shurablack.model.character.view.CharacterView;
import de.shurablack.model.combat.dungeon.Dungeons;
import de.shurablack.model.combat.enemy.Enemies;
import de.shurablack.model.combat.worldboss.WorldBosses;
import de.shurablack.model.guild.GuildView;
import de.shurablack.model.guild.conquest.GuildConquest;
import de.shurablack.model.guild.conquest.GuildConquestInspection;
import de.shurablack.model.item.ItemInspection;
import de.shurablack.model.item.Items;
import de.shurablack.model.item.market.MarketHistory;
import de.shurablack.model.shrine.ShrineInfo;
import de.shurablack.util.LocationType;
import de.shurablack.util.MarketType;
import de.shurablack.util.MuseumCategory;

import java.util.Map;

/**
 * Provides methods to interact with various endpoints in the system.
 * This class contains static methods to perform HTTP requests and retrieve data for authentication,
 * characters, guilds, items, markets, and other entities.
 */
public class Requester {

    private Requester() {
        // Private constructor to prevent instantiation
    }

    /**
     * Retrieves authentication information.
     * @return A response containing authentication details.
     */
    public static Response<Authentication> getAuthentication() {
        return RequestManager.getInstance().enqueueRequest(Endpoint.AUTHENTICATE, null, null, Authentication.class).join();
    }

    /**
     * Retrieves information about world bosses.
     * @return A response containing world boss details.
     */
    public static Response<WorldBosses> getWorldBosses() {
        return RequestManager.getInstance().enqueueRequest(Endpoint.WORLD_BOSSES, null, null, WorldBosses.class).join();
    }

    /**
     * Retrieves information about dungeons.
     * @return A response containing dungeon details.
     */
    public static Response<Dungeons> getDungeons() {
        return RequestManager.getInstance().enqueueRequest(Endpoint.DUNGEONS, null, null, Dungeons.class).join();
    }

    /**
     * Retrieves information about enemies.
     * @return A response containing enemy details.
     */
    public static Response<Enemies> getEnemies() {
        return RequestManager.getInstance().enqueueRequest(Endpoint.ENEMIES, null, null, Enemies.class).join();
    }

    /**
     * Searches for items based on a query.
     * @param query The search query.
     * @return A response containing the search results.
     */
    public static Response<Items> searchItem(String query) {
        return RequestManager.getInstance().enqueueRequest(Endpoint.ITEMS, null, Map.of("query", query), Items.class).join();
    }

    /**
     * Searches for items based on a query and page number.
     * @param query The search query.
     * @param page The page number.
     * @return A response containing the search results.
     */
    public static Response<Items> searchItem(String query, int page) {
        return RequestManager.getInstance().enqueueRequest(Endpoint.ITEMS, null, Map.of("query", query, "page", String.valueOf(page)), Items.class).join();
    }

    /**
     * Inspects an item based on its hashed ID.
     * @param hashedItemId The hashed ID of the item.
     * @return A response containing item inspection details.
     */
    public static Response<ItemInspection> inspectItem(String hashedItemId) {
        return RequestManager.getInstance().enqueueRequest(Endpoint.ITEM_INSPECTION, Map.of("hashed_item_id", hashedItemId), null, ItemInspection.class).join();
    }

    /**
     * Retrieves market history for an item.
     * @param hashedItemId The hashed ID of the item.
     * @param tier The tier of the item.
     * @param type The market type (listings or orders).
     * @return A response containing market history details.
     */
    public static Response<MarketHistory> getMarketHistory(String hashedItemId, int tier, MarketType type) {
        return RequestManager.getInstance().enqueueRequest(Endpoint.ITEM_MARKET_HISTORY
                , Map.of("hashed_item_id", hashedItemId)
                , Map.of("tier", String.valueOf(tier), "type", type.name().toLowerCase())
                , MarketHistory.class).join();
    }

    /**
     * Retrieves market history for an item with a default tier of 0.
     * @param hashedItemId The hashed ID of the item.
     * @param type The market type (listings or orders).
     * @return A response containing market history details.
     */
    public static Response<MarketHistory> getMarketHistory(String hashedItemId, MarketType type) {
        return RequestManager.getInstance().enqueueRequest(Endpoint.ITEM_MARKET_HISTORY
                , Map.of("hashed_item_id", hashedItemId)
                , Map.of("tier", "0", "type", type.name().toLowerCase())
                , MarketHistory.class).join();
    }

    /**
     * Retrieves market listing history for an item.
     * @param hashedItemId The hashed ID of the item.
     * @return A response containing market listing history details.
     */
    public static Response<MarketHistory> getMarketListingHistory(String hashedItemId) {
        return RequestManager.getInstance().enqueueRequest(Endpoint.ITEM_MARKET_HISTORY
                , Map.of("hashed_item_id", hashedItemId)
                , Map.of("tier", "0", "type", MarketType.LISTINGS.name().toLowerCase())
                , MarketHistory.class).join();
    }

    /**
     * Retrieves market order history for an item.
     * @param hashedItemId The hashed ID of the item.
     * @return A response containing market order history details.
     */
    public static Response<MarketHistory> getMarketOrderHistory(String hashedItemId) {
        return RequestManager.getInstance().enqueueRequest(Endpoint.ITEM_MARKET_HISTORY
                , Map.of("hashed_item_id", hashedItemId)
                , Map.of("tier", "0", "type", MarketType.ORDERS.name().toLowerCase())
                , MarketHistory.class).join();
    }

    /**
     * Retrieves character information based on its hashed ID.
     * @param hashedCharacterId The hashed ID of the character.
     * @return A response containing character details.
     */
    public static Response<CharacterView> getCharacter(String hashedCharacterId) {
        return RequestManager.getInstance().enqueueRequest(Endpoint.CHARACTER_VIEW
                , Map.of("hashed_character_id", hashedCharacterId)
                , null
                , CharacterView.class).join();
    }

    /**
     * Retrieves character metrics based on its hashed ID.
     * @param hashedCharacterId The hashed ID of the character.
     * @return A response containing character metrics.
     */
    public static Response<CharacterMetric> getCharacterMetrics(String hashedCharacterId) {
        return RequestManager.getInstance().enqueueRequest(Endpoint.CHARACTER_METRICS
                , Map.of("hashed_character_id", hashedCharacterId)
                , null
                , CharacterMetric.class).join();
    }

    /**
     * Retrieves character effects based on its hashed ID.
     * @param hashedCharacterId The hashed ID of the character.
     * @return A response containing character effects.
     */
    public static Response<CharacterEffects> getCharacterEffects(String hashedCharacterId) {
        return RequestManager.getInstance().enqueueRequest(Endpoint.CHARACTER_EFFECTS
                , Map.of("hashed_character_id", hashedCharacterId)
                , null
                , CharacterEffects.class).join();
    }

    /**
     * Retrieves alternate characters for a character based on its hashed ID.
     * @param hashedCharacterId The hashed ID of the character.
     * @return A response containing alternate character details.
     */
    public static Response<CharacterAlts> getCharacterAlts(String hashedCharacterId) {
        return RequestManager.getInstance().enqueueRequest(Endpoint.CHARACTER_ALT_CHARACTERS
                , Map.of("hashed_character_id", hashedCharacterId)
                , null
                , CharacterAlts.class).join();
    }

    /**
     * Retrieves the museum information of a character based on its hashed ID.
     * @param hashedCharacterId The hashed ID of the character.
     * @return A response containing museum details.
     */
    public static Response<CharacterMuseum> getCharacterMuseum(String hashedCharacterId) {
        return RequestManager.getInstance().enqueueRequest(Endpoint.CHARACTER_MUSEUM
                , Map.of("hashed_character_id", hashedCharacterId)
                , null
                , CharacterMuseum.class).join();
    }

    /**
     * Retrieves the museum information of a character for a specific page.
     * @param hashedCharacterId The hashed ID of the character.
     * @param page The page number.
     * @return A response containing museum details.
     */
    public static Response<CharacterMuseum> getCharacterMuseum(String hashedCharacterId, int page) {
        return RequestManager.getInstance().enqueueRequest(Endpoint.CHARACTER_MUSEUM
                , Map.of("hashed_character_id", hashedCharacterId)
                , Map.of("page", String.valueOf(page))
                , CharacterMuseum.class).join();
    }

    /**
     * Retrieves the museum information of a character for a specific category.
     * @param hashedCharacterId The hashed ID of the character.
     * @param category The museum category.
     * @return A response containing museum details.
     */
    public static Response<CharacterMuseum> getCharacterMuseum(String hashedCharacterId, MuseumCategory category) {
        return RequestManager.getInstance().enqueueRequest(Endpoint.CHARACTER_MUSEUM
                , Map.of("hashed_character_id", hashedCharacterId)
                , Map.of("category", category.name())
                , CharacterMuseum.class).join();
    }

    /**
     * Retrieves the museum information of a character for a specific category and page.
     * @param hashedCharacterId The hashed ID of the character.
     * @param category The museum category.
     * @param page The page number.
     * @return A response containing museum details.
     */
    public static Response<CharacterMuseum> getCharacterMuseum(String hashedCharacterId, MuseumCategory category, int page) {
        return RequestManager.getInstance().enqueueRequest(Endpoint.CHARACTER_MUSEUM
                , Map.of("hashed_character_id", hashedCharacterId)
                , Map.of("category", category.name(), "page", String.valueOf(page))
                , CharacterMuseum.class).join();
    }

    /**
     * Retrieves the current action of a character based on its hashed ID.
     * @param hashedCharacterId The hashed ID of the character.
     * @return A response containing the character's current action.
     */
    public static Response<CharacterAction> getCharacterAction(String hashedCharacterId) {
        return RequestManager.getInstance().enqueueRequest(Endpoint.CHARACTER_CURRENT_ACTION
                , Map.of("hashed_character_id", hashedCharacterId)
                , null
                , CharacterAction.class).join();
    }

    /**
     * Retrieves the pets of a character based on its hashed ID.
     * @param hashedCharacterId The hashed ID of the character.
     * @return A response containing character pet details.
     */
    public static Response<CharacterPets> getCharacterPets(String hashedCharacterId) {
        return RequestManager.getInstance().enqueueRequest(Endpoint.CHARACTER_PETS
                , Map.of("hashed_character_id", hashedCharacterId)
                , null
                , CharacterPets.class).join();
    }

    /**
     * Retrieves guild information based on its ID.
     * @param id The ID of the guild.
     * @return A response containing guild details.
     */
    public static Response<GuildView> getGuild(int id) {
        return RequestManager.getInstance().enqueueRequest(Endpoint.GUILD_INFORMATION
                , Map.of("id", String.valueOf(id))
                , null
                , GuildView.class).join();
    }

    /**
     * Retrieves the current guild conquest information.
     * @return A response containing guild conquest details.
     */
    public static Response<GuildConquest> getCurrentGuildConquest() {
        return RequestManager.getInstance().enqueueRequest(Endpoint.GUILD_CONQUESTS
                , null
                , null
                , GuildConquest.class).join();
    }

    /**
     * Retrieves guild conquest information for a specific season.
     * @param season The season number.
     * @return A response containing guild conquest details.
     */
    public static Response<GuildConquest> getGuildConquestBySeason(int season) {
        return RequestManager.getInstance().enqueueRequest(Endpoint.GUILD_CONQUESTS
                , null
                , Map.of("season_number", String.valueOf(season))
                , GuildConquest.class).join();
    }

    /**
     * Retrieves guild conquest inspection details for a specific zone.
     * @param zone The location type representing the zone.
     * @return A response containing guild conquest inspection details.
     */
    public static Response<GuildConquestInspection> getGuildConquestInspection(LocationType zone) {
        return RequestManager.getInstance().enqueueRequest(Endpoint.GUILD_CONQUEST_ZONE_INSPECTION
                , Map.of("zone_id", zone.getId())
                , null
                , GuildConquestInspection.class).join();
    }

    /**
     * Retrieves guild conquest inspection details for a specific zone and season.
     * @param zone The location type representing the zone.
     * @param season The season number.
     * @return A response containing guild conquest inspection details.
     */
    public static Response<GuildConquestInspection> getGuildConquestInspection(LocationType zone, int season) {
        return RequestManager.getInstance().enqueueRequest(Endpoint.GUILD_CONQUEST_ZONE_INSPECTION
                , Map.of("zone_id", zone.getId())
                , Map.of("season_number", String.valueOf(season))
                , GuildConquestInspection.class).join();
    }

    /**
     * Retrieves shrine progress information.
     * @return A response containing shrine progress details.
     */
    public static Response<ShrineInfo> getShrineInfo() {
        return RequestManager.getInstance().enqueueRequest(Endpoint.SHRINE_PROGRESS, null, null, ShrineInfo.class).join();
    }

}