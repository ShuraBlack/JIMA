package de.shurablack.util.types;

/**
 * Represents the various secondary stat types available in the system.
 * This enum defines a set of secondary stats that can be used for categorization or processing.
 */
public enum SecondaryStatType {

    /**
     * Represents the secondary stat type for critical chance.
     */
    CRITICAL_CHANCE,

    /**
     * Represents the secondary stat type for critical damage.
     */
    CRITICAL_DAMAGE,

    /**
     * Represents the secondary stat type for attack power.
     */
    ATTACK_POWER,

    /**
     * Represents the secondary stat type for protection.
     */
    PROTECTION,

    /**
     * Represents the secondary stat type for agility.
     */
    AGILITY,

    /**
     * Represents the secondary stat type for accuracy.
     */
    ACCURACY,

    /**
     * Represents the secondary stat type for movement speed.
     */
    MOVEMENT_SPEED,

    /**
     * Represents the secondary stat type for damage.
     */
    DAMAGE,

    /**
     * Represents an unknown secondary stat type.
     */
    UNKNOWN;

    /**
     * Converts a string value to its corresponding SecondaryStatType.
     * If the value does not match any secondary stat type, UNKNOWN is returned.
     *
     * @param value The string value to convert.
     * @return The corresponding SecondaryStatType, or UNKNOWN if no match is found.
     */
    public static SecondaryStatType fromString(String value) {
        value = value.trim().toUpperCase().replaceAll(" ", "_");
        for (SecondaryStatType type : SecondaryStatType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return UNKNOWN;
    }
}