package de.shurablack.jima.model.combat.enemy;

import de.shurablack.jima.model.combat.Location;
import de.shurablack.jima.model.combat.Loot;
import de.shurablack.jima.util.Nullable;
import lombok.*;

import java.util.List;

/**
 * Represents an enemy in the system.
 * This class contains details about the enemy, such as its ID, name, image URL, level, experience, health,
 * chance of dropping loot, location, and the list of loot items.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Enemy {

    /**
     * The unique identifier of the enemy.
     */
    private int id;

    /**
     * The name of the enemy.
     */
    private String name;

    /**
     * The URL of the image representing the enemy. This field is optional.
     */
    @Nullable
    private String imageUrl;

    /**
     * The level of the enemy.
     */
    private int level;

    /**
     * The amount of experience awarded for defeating the enemy.
     */
    private int experience;

    /**
     * The health points of the enemy.
     */
    private int health;

    /**
     * The percentage chance of the enemy dropping loot upon defeat.
     */
    private int chanceOfLoot;

    /**
     * The location where the enemy is found.
     */
    private Location location;

    /**
     * The list of loot items that can be obtained from the enemy.
     */
    private List<Loot> loot;

}