package de.shurablack.model.item;

import lombok.*;

/**
 * Represents an effect applied to an item.
 * This class contains details about the attribute affected, the target of the effect,
 * the value of the effect, and the type of the value.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Effect {

    /**
     * The attribute affected by the effect (e.g., strength, agility).
     */
    private String attribute;

    /**
     * The target of the effect (e.g., self, enemy).
     */
    private String target;

    /**
     * The value of the effect.
     */
    private int value;

    /**
     * The type of the value (e.g., percentage, flat value).
     */
    private String valueType;

}