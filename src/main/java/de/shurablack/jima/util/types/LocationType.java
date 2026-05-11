package de.shurablack.jima.util.types;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.stream.Stream;

/**
 * Represents the types of locations in the system.
 * This enum defines various location types, each associated with a unique identifier.
 */
@AllArgsConstructor
@Getter
public enum LocationType {

    /**
     * Represents the location type for Bluebell Hollow.
     */
    BLUEBELL_HOLLOW("1"),

    /**
     * Represents the location type for Whispering Woods.
     */
    WHISPERING_WOODS("2"),

    /**
     * Represents the location type for Eldoria.
     */
    ELDORIA("3"),

    /**
     * Represents the location type for Crystal Caverns.
     */
    CRYSTAL_CAVERNS("4"),

    /**
     * Represents the location type for Skyreach Peak.
     */
    SKYREACH_PEAK("5"),

    /**
     * Represents the location type for Enchanted Oasis.
     */
    ENCHANTED_OASIS("6"),

    /**
     * Represents the location type for Floating Gardens of Aetheria.
     */
    FLOATING_GARDENS_OF_AETHERIA("7"),

    /**
     * Represents the location type for Celestial Observatory.
     */
    CELESTIAL_OBSERVATORY("8"),

    /**
     * Represents the location type for Isle of Whispers.
     */
    ISLE_OF_WHISPERS("9"),

    /**
     * Represents the location type for The Citadel.
     */
    THE_CITADEL("10"),

    UNKNOWN(null);

    /**
     * The unique identifier associated with the location type.
     */
    private final String id;

    /**
     * Converts a string to the corresponding LocationType enum value.
     * Supports both enum names and location IDs (case-insensitive for names).
     *
     * @param value The string representation of the location (enum name or ID, case-insensitive for names).
     * @return The matching LocationType enum value. Returns THE_CITADEL if no match is found.
     */
    public static LocationType fromString(String value) {
        if (value == null) {
            return THE_CITADEL;
        }

        // Try matching by ID first (exact match)
        for (LocationType location : values()) {
            if (location.id.equals(value)) {
                return location;
            }
        }

        // Try matching by enum name (case-insensitive)
        return Stream.of(values())
                .filter(location -> location.name().equalsIgnoreCase(value))
                .findFirst()
                .orElse(UNKNOWN);
    }
}