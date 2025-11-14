package de.shurablack.jima.model.pet;

import de.shurablack.jima.model.EndpointUpdate;
import de.shurablack.jima.model.Paged;
import lombok.*;

import java.util.List;

/**
 * Represents a collection of pet listings along with pagination details.
 * Extends {@link EndpointUpdate} to include metadata about the last update.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class Listings extends EndpointUpdate {

    /**
     * A list of pet listings.
     * Each listing contains details about a specific pet available for trade or sale.
     */
    private List<PetListing> listings;

    /**
     * Pagination information for the listings.
     * Includes details such as the current page, total pages, and items per page.
     */
    private Paged pagination;

}