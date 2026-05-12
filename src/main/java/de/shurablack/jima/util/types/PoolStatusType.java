package de.shurablack.jima.util.types;

/**
 * Enum representing the current operational status of a guild Energizing Pool.
 */
public enum PoolStatusType {

    /**
     * Pool is dormant and not providing any buffs to guild members.
     */
    DORMANT,

    /**
     * Pool is actively running but has not been applied/activated yet.
     * Timer is counting down but no buffs are in effect.
     */
    ACTIVE_BUT_NOT_APPLIED,

    /**
     * Pool is active and actively applying its buffs to guild members.
     * Both running and providing status effects.
     */
    ACTIVE_AND_APPLIED,

    /**
     * Status could not be determined from the API response.
     * Fallback value for unrecognized pool states.
     */
    UNKNOWN
}
