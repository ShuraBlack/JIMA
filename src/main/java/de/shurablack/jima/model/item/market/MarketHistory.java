package de.shurablack.jima.model.item.market;

import de.shurablack.jima.model.EndpointUpdate;
import lombok.*;

import java.util.List;

/**
 * Represents the market history for items.
 * This class extends the EndpointUpdate class and contains details about the historical data,
 * the latest sold items, and the type of market data.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class MarketHistory extends EndpointUpdate {

    /**
     * The historical data of the market, represented as a list of history entries.
     */
    private List<HistoryEntry> historyData;

    /**
     * The latest sold items in the market, represented as a list of latest sell transactions.
     */
    private List<LatestTransaction> latestSold;

    /**
     * The type of market data (e.g., listings and orders).
     */
    private String type;

}