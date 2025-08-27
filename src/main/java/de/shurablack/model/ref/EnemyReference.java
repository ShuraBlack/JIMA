package de.shurablack.model.ref;

import lombok.*;

/**
 * Represents a reference to an enemy.
 * This class contains details about the enemy's unique identifier, name, and level.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class EnemyReference {

    /**
     * The unique identifier of the enemy.
     */
    private int id;

    /**
     * The name of the enemy.
     */
    private String name;

    /**
     * The level of the enemy.
     */
    private int level;

}