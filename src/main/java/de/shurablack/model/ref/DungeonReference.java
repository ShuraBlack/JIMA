package de.shurablack.model.ref;

import lombok.*;

/**
 * Represents a reference to a dungeon.
 * This class contains details about the dungeon's unique identifier and name.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class DungeonReference {

    /**
     * The unique identifier of the dungeon.
     */
    private int id;

    /**
     * The name of the dungeon.
     */
    private String name;

}