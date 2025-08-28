package de.shurablack.util.types;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
    BANISHED("Banished"),
    FORSAKEN("Forsaken"),
    CURSED("Cursed"),
    UNKNOWN("Unknown");

    private final String displayName;

}
