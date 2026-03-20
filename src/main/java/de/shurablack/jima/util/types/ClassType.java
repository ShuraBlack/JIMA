package de.shurablack.jima.util.types;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public enum ClassType {
    WARRIOR("Warrior"),
    SHADOWBLADE("Shadowblade"),
    RANGER("Ranger"),
    MINER("Miner"),
    ANGLER("Angler"),
    CHEF("Chef"),
    LUMBERJACK("Lumberjack"),
    SMELTER("Smelter"),
    BEASTMASTER("Beastmaster"),
    BANISHED("Banished"),
    FORSAKEN("Forsaken"),
    CURSED("Cursed"),
    UNKNOWN("Unknown");

    private final String displayName;

    /**
     * Converts a string to the corresponding ClassType enum value.
     * Supports both enum names and display names (case-insensitive).
     *
     * @param value The string representation of the class type (enum name or display name, case-insensitive).
     * @return The matching ClassType enum value, or UNKNOWN if no match is found.
     */
    public static ClassType fromString(String value) {
        if (value == null) {
            return UNKNOWN;
        }

        // Try matching by enum name
        return Stream.of(values())
                .filter(type -> type.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseGet(() ->
                    // Try matching by display name
                    Stream.of(values())
                            .filter(type -> type.displayName.equalsIgnoreCase(value))
                            .findFirst()
                            .orElse(UNKNOWN)
                );
    }
}
