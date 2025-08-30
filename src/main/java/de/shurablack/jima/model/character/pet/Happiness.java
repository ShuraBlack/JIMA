package de.shurablack.jima.model.character.pet;

import lombok.*;

/**
 * Represents the happiness level of a pet.
 * This class contains details about the current happiness, maximum happiness, and the percentage of happiness.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Happiness {

    /**
     * The current happiness level of the pet.
     */
    private long current;

    /**
     * The maximum possible happiness level of the pet.
     */
    private long maximum;

    /**
     * The percentage of the pet's happiness, calculated as (current / maximum) * 100.
     */
    private long percentage;

}