package de.shurablack.jima.model.pet;

import de.shurablack.jima.util.types.Quality;
import lombok.*;

/**
 * Represents a listing for a pet, including details about the pet and its associated cost.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class PetListing {

    /**
     * The unique identifier for the pet associated with the character.
     */
    private int characterPetId;

    /**
     * The unique identifier for the pet.
     */
    private int petId;

    /**
     * The name of the pet.
     */
    private String name;

    /**
     * The quality of the pet, represented as an enum.
     */
    private Quality quality;

    /**
     * The level of the pet.
     */
    private int level;

    /**
     * The URL of the pet's image.
     */
    private String imageUrl;

    /**
     * The currency type used for the pet's cost.
     */
    private String currency;

    /**
     * The amount of currency required for the pet.
     */
    private int amount;

}