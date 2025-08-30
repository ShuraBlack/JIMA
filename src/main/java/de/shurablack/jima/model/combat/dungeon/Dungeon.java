package de.shurablack.jima.model.combat.dungeon;

import de.shurablack.jima.model.combat.Location;
import de.shurablack.jima.model.combat.Loot;
import de.shurablack.jima.util.Nullable;
import lombok.*;

import java.util.List;

/**
 * Represents a dungeon in the system.
 * This class contains details about the dungeon, such as its ID, name, description, image URL,
 * level requirement, difficulty, length, cost, shards, completion requirements, location, loot, and experience gained.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Dungeon {

    /**
     * The unique identifier of the dungeon.
     */
    private int id;

    /**
     * The name of the dungeon.
     */
    private String name;

    /**
     * A brief description of the dungeon. This field is optional.
     */
    @Nullable
    private String description;

    /**
     * The URL of the image representing the dungeon.
     */
    private String imageUrl;

    /**
     * The minimum level required to enter the dungeon.
     */
    private int levelRequired;

    /**
     * The difficulty level of the dungeon.
     */
    private int difficulty;

    /**
     * The length of the dungeon, typically measured in some unit (e.g., number of rooms or time).
     */
    private int length;

    /**
     * The cost required to enter the dungeon.
     */
    private int cost;

    /**
     * The number of shards rewarded upon completing the dungeon.
     */
    private int shards;

    /**
     * The requirements that must be met to complete the dungeon.
     */
    private int completionRequirement;

    /**
     * The location where the dungeon is situated.
     */
    private Location location;

    /**
     * The list of loot items that can be obtained from the dungeon.
     */
    private List<Loot> loot;

    /**
     * The experience gained for completing the dungeon, mapped to specific skills.
     */
    private Experience experience;

}