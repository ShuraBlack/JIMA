package de.shurablack.model.character.pet;

import lombok.*;

/**
 * Represents a location associated with a pet.
 * This class contains details about the location's ID, name, and whether it is locked.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Location {

    /**
     * The unique identifier for the location.
     */
    private int id;

    /**
     * The name of the location.
     */
    private String name;

    /**
     * Indicates whether the location is locked.
     */
    private boolean locked;

}