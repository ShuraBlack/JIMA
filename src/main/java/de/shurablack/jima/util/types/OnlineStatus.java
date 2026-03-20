package de.shurablack.jima.util.types;

import java.util.stream.Stream;

/**
 * Represents the online status of a player or character.
 */
public enum OnlineStatus {
    /**
     * Represents an online status.
     */
    ONLINE,

    /**
     * Represents an idle status.
     */
    IDLING,

    /**
     * Represents an offline status.
     */
    OFFLINE;

    /**
     * Converts a string to the corresponding OnlineStatus enum value.
     * The string matching is case-insensitive.
     *
     * @param value The string representation of the online status (case-insensitive).
     * @return The matching OnlineStatus enum value, or OFFLINE if no match is found.
     */
    public static OnlineStatus fromString(String value) {
        if (value == null) {
            return OFFLINE;
        }
        return Stream.of(values())
                .filter(status -> status.name().equalsIgnoreCase(value))
                .findFirst()
                .orElse(OFFLINE);
    }
}
