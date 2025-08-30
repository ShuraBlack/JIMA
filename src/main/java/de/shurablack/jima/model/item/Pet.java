package de.shurablack.jima.model.item;

import de.shurablack.jima.util.Nullable;
import lombok.*;

/**
 * Represents a pet in the system.
 * This class contains details about the pet's unique identifier, name, description, and image URL.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Pet {

    /**
     * The unique hashed identifier of the pet.
     */
    private String hashedId;

    /**
     * The name of the pet.
     */
    private String name;

    /**
     * A brief description of the pet (nullable).
     */
    @Nullable
    private String description;

    /**
     * The URL of the pet's image (nullable).
     */
    @Nullable
    private String imageUrl;

}