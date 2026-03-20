package de.shurablack.jima.util.types;

import java.util.stream.Stream;

/**
 * Represents the categories of a museum in the system.
 * This enum defines various categories that can be used to classify museum items.
 */
public enum MuseumCategory {

    /**
     * Represents the category for skins.
     */
    SKINS,

    /**
     * Represents the category for backgrounds.
     */
    BACKGROUNDS,

    /**
     * Represents the category for guild icons.
     */
    GUILD_ICONS,

    /**
     * Represents the category for pets.
     */
    PETS,

    /**
     * Represents the category for collectibles.
     */
    COLLECTIBLES,

    /**
     * Represents the category for bestiary items.
     */
    BESTIARY;

    /**
     * Converts a string to the corresponding MuseumCategory enum value.
     * The string matching is case-insensitive.
     *
     * @param value The string representation of the museum category (case-insensitive).
     * @return The matching MuseumCategory enum value, or SKINS if no match is found.
     */
    public static MuseumCategory fromString(String value) {
        if (value == null) {
            return SKINS;
        }
        return Stream.of(values())
                .filter(category -> category.name().equalsIgnoreCase(value))
                .findFirst()
                .orElse(SKINS);
    }
}