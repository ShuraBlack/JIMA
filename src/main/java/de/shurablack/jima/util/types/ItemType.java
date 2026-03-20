package de.shurablack.jima.util.types;

import java.util.stream.Stream;

/**
 * Represents the different types of items in the system.
 * This enum defines various item types that can be used to classify items.
 */
public enum ItemType {
    ALL,
    SWORD,
    DAGGER,
    BOW,
    SPECIAL,
    CHESTPLATE,
    GAUNTLETS,
    SHIELD,
    GREAVES,
    BOOTS,
    HELMET,
    LOG,
    FISH,
    CRAFTING_MATERIAL,
    CONSTRUCTION_MATERIAL,
    FOOD,
    PET_EGG,
    METAL_BAR,
    POTION,
    ESSENCE_CRYSTAL,
    ORE,
    RECIPE,
    CAMPAIGN_ITEM,
    FISHING_ROD,
    PICKAXE,
    FELLING_AXE,
    MEMBERSHIP,
    TOKENS,
    COLLECTABLE,
    UPGRADE_STONE,
    CHEST,
    VIAL,
    EMPTY_CRYSTAL,
    TOKEN,
    CAKE,
    RELIC,
    BAIT,
    METAMORPHITE,
    NAMESTONE,
    BLANK_SCROLL,
    GUIDANCE_SCROLL,
    TELEPORTATION_STONE,
    GEMSTONE;

    /**
     * Converts a string to the corresponding ItemType enum value.
     * The string matching is case-insensitive.
     *
     * @param value The string representation of the item type (case-insensitive).
     * @return The matching ItemType enum value, or ItemType.ALL if no match is found.
     */
    public static ItemType fromString(String value) {
        if (value == null) {
            return ALL;
        }
        return Stream.of(values())
                .filter(type -> type.name().equalsIgnoreCase(value))
                .findFirst()
                .orElse(ALL);
    }
}
