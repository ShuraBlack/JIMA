package de.shurablack.model.character.pet;

import lombok.*;

/**
 * Represents the hunger level of a pet.
 * This class contains details about the current hunger, maximum hunger, and the percentage of hunger.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Hunger {

    /**
     * The current hunger level of the pet.
     */
    private long current;

    /**
     * The maximum possible hunger level of the pet.
     */
    private long maximum;

    /**
     * The percentage of the pet's hunger, calculated as (current / maximum) * 100.
     */
    private long percentage;

}