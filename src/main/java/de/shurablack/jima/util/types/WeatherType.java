package de.shurablack.jima.util.types;

/**
 * Enumeration of all possible weather conditions in the game world.
 */
public enum WeatherType {

    /**
     * Clear skies with no weather effects.
     * No buffs or debuffs are active.
     */
    CLEAR,

    /**
     * Cloudy/overcast conditions.
     * Minor visual and potentially minimal gameplay effects.
     */
    OVERCAST,

    /**
     * Precipitation weather with rain.
     * Typically associated with wet environment buffs and reduced visibility.
     */
    RAIN,

    /**
     * Foggy conditions with reduced visibility.
     * May include disorientation or navigation-related effects.
     */
    FOG,

    /**
     * Severe storm weather with strong effects.
     * Associated with hazardous conditions and powerful weather-related buffs/debuffs.
     */
    STORM,

    /**
     * Magical storm phenomenon with arcane effects.
     * Characterized by magical damage buffs or mana-related bonuses/penalties.
     */
    MAGIC_STORM,

    /**
     * Windy conditions with strong winds.
     * Affects projectile travel, movement speed, and potentially ranged attack accuracy.
     */
    WINDY,

    /**
     * Snowfall weather condition.
     * Associated with cold damage, reduced movement, and winter-themed effects.
     */
    SNOW,

    /**
     * Extreme heat/heatwave weather.
     * Causes fire damage, exhaustion, and heat-related status effects.
     */
    HEATWAVE,

    /**
     * Unknown or unrecognized weather condition.
     * Used as fallback when weather type cannot be mapped to known values.
     */
    UNKNOWN;

    /**
     * Parses a string value into a WeatherType enum constant.
     *
     * @param value The string representation of a weather type. Can include spaces,
     *              hyphens, or mixed case (e.g., "magic storm", "Magic-Storm", "MAGIC_STORM")
     * @return The corresponding WeatherType enum constant, or {@link #UNKNOWN} if the
     *         value does not match any known weather type
     *
     * @see #values() For all available weather types
     */
    public static WeatherType fromString(String value) {
        value = value.trim().toUpperCase()
                .replace(" ", "_").replace("-", "_");
        for (WeatherType type : values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return UNKNOWN;
    }

}
