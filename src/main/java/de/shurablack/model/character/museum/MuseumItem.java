package de.shurablack.model.character.museum;

import de.shurablack.util.Nullable;
import lombok.*;

/**
 * Represents an item in a character's museum.
 * This class contains details about the item's category, quantity, ID, name, and an optional image URL.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class MuseumItem {

    /**
     * The category of the museum item (e.g., artifact, relic, etc.).
     */
    private String category;

    /**
     * The quantity of the museum item.
     */
    private long quantity;

    /**
     * The unique identifier of the museum item.
     */
    private String id;

    /**
     * The name of the museum item.
     */
    private String name;

    /**
     * The URL of the image representing the museum item (nullable).
     */
    @Nullable
    private String imageUrl;

}