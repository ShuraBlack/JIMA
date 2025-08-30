package de.shurablack.jima.http;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents the various API endpoints used in the system.
 * Each endpoint is defined with its corresponding path and scope.
 */
@AllArgsConstructor
@Getter
public enum Endpoint {

    /**
     * The base URL for the API.
     */
    BASE_URL("https://api.idle-mmo.com/v1", null),

    /**
     * Endpoint for checking authentication status.
     */
    AUTHENTICATE(BASE_URL.path + "/auth/check", "v1.auth.check"),

    /**
     * Endpoint for retrieving the list of world bosses.
     */
    WORLD_BOSSES(BASE_URL.path + "/combat/world_bosses/list", "v1.combat.world_bosses.list"),

    /**
     * Endpoint for retrieving the list of dungeons.
     */
    DUNGEONS(BASE_URL.path + "/combat/dungeons/list", "v1.combat.dungeons.list"),

    /**
     * Endpoint for retrieving the list of enemies.
     */
    ENEMIES(BASE_URL.path + "/combat/enemies/list", "v1.combat.enemies.list"),

    /**
     * Endpoint for searching items.
     */
    ITEMS(BASE_URL.path + "/item/search", "v1.item.search"),

    /**
     * Endpoint for inspecting a specific item.
     */
    ITEM_INSPECTION(BASE_URL.path + "/item/{hashed_item_id}/inspect", "v1.item.inspect"),

    /**
     * Endpoint for retrieving the market history of a specific item.
     */
    ITEM_MARKET_HISTORY(BASE_URL.path + "/item/{hashed_item_id}/market-history", "v1.item.market_history"),

    /**
     * Endpoint for viewing character information.
     */
    CHARACTER_VIEW(BASE_URL.path + "/character/{hashed_character_id}/information", "v1.character.view"),

    /**
     * Endpoint for retrieving character metrics.
     */
    CHARACTER_METRICS(BASE_URL.path + "/character/{hashed_character_id}/metrics", "v1.character.metrics"),

    /**
     * Endpoint for retrieving character effects.
     */
    CHARACTER_EFFECTS(BASE_URL.path + "/character/{hashed_character_id}/effects", "v1.character.effects"),

    /**
     * Endpoint for retrieving alternate characters of a character.
     */
    CHARACTER_ALT_CHARACTERS(BASE_URL.path + "/character/{hashed_character_id}/characters", "v1.character.characters"),

    /**
     * Endpoint for retrieving museum information of a character.
     */
    CHARACTER_MUSEUM(BASE_URL.path + "/character/{hashed_character_id}/museum", "v1.character.museum"),

    /**
     * Endpoint for retrieving the current action of a character.
     */
    CHARACTER_CURRENT_ACTION(BASE_URL.path + "/character/{hashed_character_id}/current-action", "v1.character.current_action"),

    /**
     * Endpoint for retrieving the pets of a character.
     */
    CHARACTER_PETS(BASE_URL.path + "/character/{hashed_character_id}/pets", "v1.character.pets"),

    /**
     * Endpoint for retrieving guild information.
     */
    GUILD_INFORMATION(BASE_URL.path + "/guild/{id}/information", "v1.guild.information"),

    /**
     * Endpoint for viewing guild conquests.
     */
    GUILD_CONQUESTS(BASE_URL.path + "/guild/conquest/view", "v1.guild.conquest.view"),

    /**
     * Endpoint for inspecting a specific guild conquest zone.
     */
    GUILD_CONQUEST_ZONE_INSPECTION(BASE_URL.path + "/guild/conquest/zone/{zone_id}/inspect", "v1.guild.conquest.zone.inspect"),

    /**
     * Endpoint for retrieving shrine progress.
     */
    SHRINE_PROGRESS(BASE_URL.path + "/shrine/progress", "v1.shrine.progress");

    /**
     * The path of the endpoint.
     */
    private final String path;

    /**
     * The scope of the endpoint, defining its purpose or access level.
     */
    private final String scope;

}