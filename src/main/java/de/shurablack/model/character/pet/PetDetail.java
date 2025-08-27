package de.shurablack.model.character.pet;

import de.shurablack.model.combat.Quality;
import de.shurablack.util.Nullable;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Represents detailed information about a pet.
 * This class contains various attributes of a pet, including its ID, name, stats, health, happiness, and more.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class PetDetail {

    /**
     * The unique identifier for the pet.
     */
    private int id;

    /**
     * The name of the pet.
     */
    private String name;

    /**
     * The custom name of the pet, if any.
     * This field is nullable.
     */
    @Nullable
    private String customName;

    /**
     * The identifier for the pet type.
     */
    private int petId;

    /**
     * The URL of the pet's image.
     */
    private String imageUrl;

    /**
     * The level of the pet.
     */
    private int level;

    /**
     * The experience points of the pet.
     */
    private long experience;

    /**
     * The quality of the pet.
     */
    private Quality quality;

    /**
     * A map containing the pet's stats, where the key is the stat name and the value is the stat value.
     */
    private Map<String, Integer> stats;

    /**
     * The health details of the pet.
     */
    private Health health;

    /**
     * The happiness details of the pet.
     */
    private Happiness happiness;

    /**
     * The hunger details of the pet.
     */
    private Hunger hunger;

    /**
     * Indicates whether the pet is currently equipped.
     */
    private boolean equipped;

    /**
     * The battle details of the pet, if it is engaged in a battle.
     * This field is nullable.
     */
    @Nullable
    private Battle battle;

    /**
     * The location details associated with the pet.
     */
    private Location location;

    /**
     * The timestamp when the pet was created.
     */
    private LocalDateTime createdAt;

}