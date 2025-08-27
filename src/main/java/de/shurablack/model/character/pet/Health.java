package de.shurablack.model.character.pet;

import lombok.*;

/**
 * Represents the health of a pet.
 * This class contains details about the current health, maximum health, and the percentage of health.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Health {

    /**
     * The current health level of the pet.
     */
    private long current;

    /**
     * The maximum possible health level of the pet.
     */
    private long maximum;

    /**
     * The percentage of the pet's health, calculated as (current / maximum) * 100.
     */
    private long percentage;

}