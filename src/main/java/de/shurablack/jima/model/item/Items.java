package de.shurablack.jima.model.item;

import de.shurablack.jima.model.Paged;
import lombok.*;

import java.util.List;

/**
 * Represents a paginated collection of items.
 * This class contains a list of items and pagination information.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Items{

    /**
     * The list of items in the current page.
     */
    private List<Item> items;

    /**
     * Pagination information for the items.
     */
    private Paged pagination;

}