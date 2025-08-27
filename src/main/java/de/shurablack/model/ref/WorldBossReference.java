package de.shurablack.model.ref;

import lombok.*;

/**
 * Represents a reference to a world boss.
 * This class contains details about the world boss's unique identifier and name.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class WorldBossReference {

    /**
     * The unique identifier of the world boss.
     */
    private int id;

    /**
     * The name of the world boss.
     */
    private String name;

}