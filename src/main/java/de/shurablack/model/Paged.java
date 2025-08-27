package de.shurablack.model;

import de.shurablack.util.Nullable;
import lombok.*;

/**
 * Represents a paginated response.
 * This class contains information about the current page, total pages, items per page,
 * total items, and optional range of items displayed on the current page.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Paged {

    /**
     * The current page number.
     */
    private int currentPage;

    /**
     * The last page number.
     */
    private int lastPage;

    /**
     * The number of items per page.
     */
    private int perPage;

    /**
     * The total number of items.
     */
    private int total;

    /**
     * The index of the first item on the current page. This field is optional.
     */
    @Nullable
    private Integer from;

    /**
     * The index of the last item on the current page. This field is optional.
     */
    @Nullable
    private Integer to;

}