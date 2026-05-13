package de.shurablack.jima.model.character.pet;

import lombok.*;

/**
 * Represents the geographic location where a pet is situated in the game world.
 *
 * <p><b>Purpose:</b> Identifies where a pet is currently stationed or trained,
 * with lock status indicating if the location is restricted.</p>
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Location {

    private int id;

    private String name;

    private boolean locked;

}
