package de.shurablack.util.types;

/**
 * Represents the various skill types available in the system.
 * This enum defines a set of skills that can be used for categorization or processing.
 */
public enum SkillType {

    /**
     * Represents the skill type for woodcutting.
     */
    WOODCUTTING,

    /**
     * Represents the skill type for mining.
     */
    MINING,

    /**
     * Represents the skill type for fishing.
     */
    FISHING,

    /**
     * Represents the skill type for alchemy.
     */
    ALCHEMY,

    /**
     * Represents the skill type for smelting.
     */
    SMELTING,

    /**
     * Represents the skill type for cooking.
     */
    COOKING,

    /**
     * Represents the skill type for forge.
     */
    FORGE,

    /**
     * Represents the skill type for shadow mastery.
     */
    SHADOW_MASTERY,

    /**
     * Represents the skill type for bartering.
     */
    BARTERING,

    /**
     * Represents the skill type for hunting mastery.
     */
    HUNTING_MASTERY,

    /**
     * Represents the skill type for yule mastery.
     */
    YULE_MASTERY,

    /**
     * Represents the skill type for springtide mastery.
     */
    SPRINGTIDE_MASTERY,

    /**
     * Represents the skill type for combat.
     */
    COMBAT,

    /**
     * Represents the skill type for dungeoneering.
     */
    DUNGEONEERING,

    /**
     * Represents the skill type for pet mastery.
     */
    PET_MASTERY,

    /**
     * Represents the skill type for guild mastery.
     */
    GUILD_MASTERY,

    /**
     * Represents the skill type for lunar mastery.
     */
    LUNAR_MASTERY,

    /**
     * Represents the skill type for meditation.
     */
    MEDITATION,

    /**
     * Represents an unknown skill type.
     */
    UNKNOWN;

    /**
     * Converts a string value to its corresponding SkillType.
     * If the value does not match any skill type, UNKNOWN is returned.
     *
     * @param value The string value to convert.
     * @return The corresponding SkillType, or UNKNOWN if no match is found.
     */
    public static SkillType fromString(String value) {
        value = value.trim().toUpperCase()
                .replaceAll(" ", "_").replaceAll("-", "_");
        for (SkillType type : values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return UNKNOWN;
    }
}