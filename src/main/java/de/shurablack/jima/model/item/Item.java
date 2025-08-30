package de.shurablack.jima.model.item;

import de.shurablack.jima.model.combat.Quality;
import de.shurablack.jima.util.Nullable;
import lombok.*;

/**
 * Represents an item in the system.
 * This class contains details about the item, such as its hashed ID, name, description, image URL, type, quality,
 * and optional vendor price.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Item {

    /**
     * The unique hashed identifier of the item.
     */
    private String hashedId;

    /**
     * The name of the item.
     */
    private String name;

    /**
     * The description of the item. This field is optional.
     */
    @Nullable
    private String description;

    /**
     * The URL of the image representing the item.
     */
    private String imageUrl;

    /**
     * The type or category of the item.
     */
    private String type;

    /**
     * The quality of the item, represented by the Quality enum.
     */
    private Quality quality;

    /**
     * The vendor price of the item. This field is optional.
     */
    @Nullable
    private Integer vendorPrice;

}