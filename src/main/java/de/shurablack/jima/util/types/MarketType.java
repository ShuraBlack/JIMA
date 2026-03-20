package de.shurablack.jima.util.types;

import java.util.stream.Stream;

/**
 * Represents the types of market views in the system.
 * This enum defines two types of markets: LISTINGS and ORDERS.
 */
public enum MarketType {

    /**
     * Represents a market type for listings.
     */
    LISTINGS,

    /**
     * Represents a market type for orders.
     */
    ORDERS;

    /**
     * Converts a string to the corresponding MarketType enum value.
     * The string matching is case-insensitive.
     *
     * @param value The string representation of the market type (case-insensitive).
     * @return The matching MarketType enum value, or LISTINGS if no match is found.
     */
    public static MarketType fromString(String value) {
        if (value == null) {
            return LISTINGS;
        }
        return Stream.of(values())
                .filter(type -> type.name().equalsIgnoreCase(value))
                .findFirst()
                .orElse(LISTINGS);
    }
}