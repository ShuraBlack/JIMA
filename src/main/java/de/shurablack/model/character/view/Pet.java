package de.shurablack.model.character.view;

import de.shurablack.util.Nullable;
import lombok.*;

/**
 * Represents a pet in the system.
 * This class contains details about a pet, including its ID, name, image URL, and level.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Pet {

    /**
     * The unique identifier for the pet.
     */
    private int id;

    /**
     * The name of the pet.
     */
    private String name;

    /**
     * The URL of the pet's image.
     * This field is nullable.
     */
    @Nullable
    private String imageUrl;

    /**
     * The level of the pet.
     */
    private int level;

}