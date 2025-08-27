package de.shurablack.model.character.metric;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;

/**
 * Represents the metrics associated with various activities and events for a character.
 * This class contains mappings of activity/event names to their corresponding metric values.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Metric {

    /**
     * Metrics related to dungeon activities.
     * The key represents the dungeon name, and the value represents the associated metric value.
     */
    private Map<String, Long> dungeon;

    /**
     * Metrics related to battle activities.
     * The key represents the battle type, and the value represents the associated metric value.
     */
    private Map<String, Long> battle;

    /**
     * Metrics related to hunting activities.
     * The key represents the hunt type, and the value represents the associated metric value.
     */
    private Map<String, Long> hunt;

    /**
     * Metrics related to market activities.
     * The key represents the market type, and the value represents the associated metric value.
     */
    private Map<String, Long> market;

    /**
     * Metrics related to direct trade activities.
     * The key represents the trade type, and the value represents the associated metric value.
     */
    private Map<String, Long> directTrade;

    /**
     * Metrics related to world boss activities.
     * The key represents the world boss name, and the value represents the associated metric value.
     */
    private Map<String, Long> worldBoss;

    /**
     * Metrics related to guild raid activities.
     * The key represents the raid type, and the value represents the associated metric value.
     */
    private Map<String, Long> guildRaid;

    /**
     * Metrics related to guild challenge activities.
     * The key represents the challenge type, and the value represents the associated metric value.
     */
    private Map<String, Long> guildChallenge;

    /**
     * Metrics related to guild stockpile activities.
     * The key represents the stockpile type, and the value represents the associated metric value.
     */
    private Map<String, Long> guildStockpile;

    /**
     * Metrics related to pet battle activities.
     * The key represents the pet battle type, and the value represents the associated metric value.
     */
    private Map<String, Long> petBattle;

    /**
     * Metrics related to shrine activities.
     * The key represents the shrine type, and the value represents the associated metric value.
     */
    private Map<String, Long> shrine;

    /**
     * Metrics related to campaign activities.
     * The key represents the campaign type, and the value represents the associated metric value.
     */
    private Map<String, Long> campaign;

    /**
     * Metrics related to travel activities.
     * The key represents the travel type, and the value represents the associated metric value.
     */
    private Map<String, Long> travel;

    /**
     * Metrics related to woodcutting activities.
     * The key represents the woodcutting type, and the value represents the associated metric value.
     */
    private Map<String, Long> woodcutting;

    /**
     * Metrics related to mining activities.
     * The key represents the mining type, and the value represents the associated metric value.
     */
    private Map<String, Long> mining;

    /**
     * Metrics related to fishing activities.
     * The key represents the fishing type, and the value represents the associated metric value.
     */
    private Map<String, Long> fishing;

    /**
     * Metrics related to smelting activities.
     * The key represents the smelting type, and the value represents the associated metric value.
     */
    private Map<String, Long> smelting;

    /**
     * Metrics related to cooking activities.
     * The key represents the cooking type, and the value represents the associated metric value.
     */
    private Map<String, Long> cooking;

    /**
     * Metrics related to forge activities.
     * The key represents the forge type, and the value represents the associated metric value.
     */
    private Map<String, Long> forge;

    /**
     * Metrics related to alchemy activities.
     * The key represents the alchemy type, and the value represents the associated metric value.
     */
    private Map<String, Long> alchemy;

    /**
     * Metrics related to shadow mastery activities.
     * The key represents the shadow mastery type, and the value represents the associated metric value.
     */
    @JsonProperty("shadow-mastery")
    private Map<String, Long> shadowMastery;

    /**
     * Metrics related to yule mastery activities.
     * The key represents the yule mastery type, and the value represents the associated metric value.
     */
    @JsonProperty("yule-mastery")
    private Map<String, Long> yuleMastery;

    /**
     * Metrics related to springtide mastery activities.
     * The key represents the springtide mastery type, and the value represents the associated metric value.
     */
    @JsonProperty("springtide-mastery")
    private Map<String, Long> springtideMastery;

    /**
     * Metrics related to lunar mastery activities.
     * The key represents the lunar mastery type, and the value represents the associated metric value.
     */
    @JsonProperty("lunar-mastery")
    private Map<String, Long> lunarMastery;

    /**
     * Metrics related to tavern activities.
     * The key represents the tavern type, and the value represents the associated metric value.
     */
    private Map<String, Long> tavern;

}