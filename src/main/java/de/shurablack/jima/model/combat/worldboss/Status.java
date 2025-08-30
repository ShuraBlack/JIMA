package de.shurablack.jima.model.combat.worldboss;

/**
 * Represents the current status of a world boss in the system.
 * This enum defines the various states a world boss can be in during its lifecycle.
 */
public enum Status {

    /**
     * Indicates that the world boss encounter is currently in progress.
     */
    IN_PROGRESS,

    /**
     * Indicates that the world boss is ready for players to join the lobby.
     */
    READY_FOR_LOBBY,

    /**
     * Indicates that the world boss is in the process of respawning.
     */
    RESPAWNING,
}