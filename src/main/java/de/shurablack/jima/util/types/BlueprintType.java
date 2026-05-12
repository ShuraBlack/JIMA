package de.shurablack.jima.util.types;

/**
 * Blueprint type categorizing the nature of guild hall construction/upgrade.
 *
 * @see de.shurablack.jima.model.guild.hall.blueprint.Blueprint For blueprints containing type information
 * @see de.shurablack.jima.model.guild.hall.GuildHall For guild halls containing blueprints
 */
public enum BlueprintType {

    /** Base structures to be built */
    CREATION,

    /** Component upgrades or improvements to existing structures */
    COMPONENT,

    /** Blueprints for expanding or modifying upgrade slots */
    SLOT,

    /** Fallback for unrecognized blueprint types */
    UNKNOWN

}
