package de.shurablack.jima.model.item.market;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents an entry in the market history for an item.
 * This class contains details about the date of the entry, the total number of items sold,
 * and the average price of the item during that time.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class HistoryEntry {

    /**
     * The date and time of the market history entry.
     */
    private LocalDateTime date;

    /**
     * The total number of items sold on the specified date.
     */
    private int totalSold;

    /**
     * The average price of the item during the specified date.
     */
    private double averagePrice;

}