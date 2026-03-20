package de.shurablack.jima.model.pet;

import lombok.*;

/**
 * Represents the evolution state of a pet in the system.
 * This class contains information about the current evolution state,
 * the maximum evolution state, bonuses per stage, and whether the pet can evolve further.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Evolution {

    /**
     * The current evolution state of the pet.
     */
    private int state;

    /**
     * The maximum evolution state the pet can reach.
     */
    private int max;

    /**
     * The bonus value added per evolution stage.
     */
    private int bonusPerStage;

    /**
     * The bonus value for the next evolution stage.
     */
    private int nextBonus;

    /**
     * Indicates whether the pet can evolve further.
     */
    private boolean canEvolve;

}