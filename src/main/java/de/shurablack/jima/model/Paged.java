package de.shurablack.jima.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.shurablack.jima.util.Nullable;
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
     * The next page number (nullable).
     * If there is no next page, this value is null.
     */
    @Nullable
    @JsonProperty(required = false)
    private Integer nextPage;

    /**
     * The total number of items.
     */
    private int total;

    /**
     * Indicates whether there are more pages available after the current page.
     */
    @JsonProperty(required = false)
    private boolean hasMore;

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