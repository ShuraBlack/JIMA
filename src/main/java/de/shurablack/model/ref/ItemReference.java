package de.shurablack.model.ref;

import de.shurablack.util.Nullable;
import lombok.*;

/**
 * Represents a reference to an item.
 * This class contains details about the item's unique identifier, name, and image URL.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ItemReference {

    /**
     * The unique hashed identifier of the item.
     */
    private String hashedId;

    /**
     * The name of the item.
     */
    private String name;

    /**
     * The URL of the item's image (nullable).
     */
    @Nullable
    private String imageUrl;

}