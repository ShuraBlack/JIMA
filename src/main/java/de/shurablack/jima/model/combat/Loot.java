package de.shurablack.jima.model.combat;

import de.shurablack.jima.util.Nullable;
import lombok.*;

/**
 * Represents loot dropped in the system.
 * This class contains details about the loot, such as its item ID, name, image URL,
 * quality, quantity, and drop chance.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Loot {

    /**
     * The hashed identifier of the loot item.
     */
    private String hashedItemId;

    /**
     * The name of the loot item.
     */
    private String name;

    /**
     * The URL of the image representing the loot item.
     * <br><br>
     * Can be null if no image is available.
     */
    @Nullable
    private String imageUrl;

    /**
     * The quality of the loot item, indicating its rarity or refinement level.
     */
    private Quality quality;

    /**
     * The quantity of the loot item dropped.
     */
    private int quantity;

    /**
     * The chance of the loot item being dropped, represented as a percentage.
     */
    private double chance;

}