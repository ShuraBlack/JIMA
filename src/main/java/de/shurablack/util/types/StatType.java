package de.shurablack.util.types;

/**
 * Represents the various stat types available in the system.
 * This enum defines a set of stats that can be used for categorization or processing.
 */
public enum StatType {

    /**
     * Represents the stat type for strength.
     */
    STRENGTH,

    /**
     * Represents the stat type for defence.
     */
    DEFENCE,

    /**
     * Represents the stat type for speed.
     */
    SPEED,

    /**
     * Represents the stat type for dexterity.
     */
    DEXTERITY,

    /**
     * Represents an unknown stat type.
     */
    UNKNOWN;

    /**
     * Converts a string value to its corresponding StatType.
     * If the value does not match any stat type, UNKNOWN is returned.
     *
     * @param value The string value to convert.
     * @return The corresponding StatType, or UNKNOWN if no match is found.
     */
    public static StatType fromString(String value) {
        value = value.trim().toUpperCase();
        for (StatType type : values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return UNKNOWN;
    }
}