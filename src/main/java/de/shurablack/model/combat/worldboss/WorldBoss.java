package de.shurablack.model.combat.worldboss;

import de.shurablack.model.combat.Location;
import de.shurablack.model.combat.Loot;
import de.shurablack.util.Nullable;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a world boss in the system.
 * This class contains details about the world boss, such as its ID, name, image URL, level,
 * location, loot, status, and battle timings.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class WorldBoss {

    /**
     * The unique identifier of the world boss.
     */
    private int id;

    /**
     * The name of the world boss.
     */
    private String name;

    /**
     * The URL of the image representing the world boss.
     */
    private String imageUrl;

    /**
     * The level of the world boss.
     */
    private int level;

    /**
     * The location where the world boss appears.
     */
    private Location location;

    /**
     * The list of loot items dropped by the world boss.
     */
    private List<Loot> loot;

    /**
     * The current status of the world boss.
     */
    private Status status;

    /**
     * The date and time when the battle with the world boss starts.
     * <br><br>
     * Can be null if the battle has already started or ended.
     */
    @Nullable
    private LocalDateTime battleStartsAt;

    /**
     * The date and time when the battle with the world boss ends.
     * <br><br>
     * Can be null if the battle is ongoing or has not yet started.
     */
    @Nullable
    private LocalDateTime battleEndsAt;

}