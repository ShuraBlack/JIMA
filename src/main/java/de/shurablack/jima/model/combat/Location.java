package de.shurablack.jima.model.combat;

import lombok.*;

/**
 * Represents a location in the system.
 * This class contains details about the location, such as its unique identifier and name.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Location {

    /**
     * The unique identifier of the location.
     */
    private int id;

    /**
     * The name of the location.
     */
    private String name;

}