package de.shurablack.model.character.museum;

import de.shurablack.model.Paged;
import lombok.*;

import java.util.List;

/**
 * Represents a character's museum.
 * This class contains a list of museum items and pagination details.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class CharacterMuseum {

    /**
     * The list of items in the character's museum.
     */
    private List<MuseumItem> items;

    /**
     * The pagination details for the museum items.
     */
    private Paged pagination;

}