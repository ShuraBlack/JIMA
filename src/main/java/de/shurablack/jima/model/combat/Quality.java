package de.shurablack.jima.model.combat;

/**
 * Represents the quality levels of an item or entity in the system.
 * Each quality level indicates a different degree of refinement or rarity.
 */
public enum Quality {

    /**
     * Standard quality, representing the most basic level.
     */
    STANDARD,

    /**
     * Refined quality, representing a higher level of refinement than standard.
     */
    REFINED,

    /**
     * Premium quality, representing a superior level of refinement.
     */
    PREMIUM,

    /**
     * Epic quality, representing a rare and highly valued level.
     */
    EPIC,

    /**
     * Legendary quality, representing an extremely rare and prestigious level.
     */
    LEGENDARY,

    /**
     * Mythic quality, representing a mythical level of rarity and value.
     */
    MYTHIC,

    /**
     * Unique quality, representing a one-of-a-kind or unparalleled level (given by the developer).
     */
    UNIQUE
}