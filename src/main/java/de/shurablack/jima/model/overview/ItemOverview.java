package de.shurablack.jima.model.overview;

import de.shurablack.jima.http.Requester;
import de.shurablack.jima.http.Response;
import de.shurablack.jima.http.ResponseCode;
import de.shurablack.jima.model.item.ItemInspection;
import de.shurablack.jima.model.item.market.MarketHistory;
import de.shurablack.jima.util.Nullable;
import de.shurablack.jima.util.types.MarketType;
import lombok.*;

/**
 * Comprehensive item information container with optional market data.
 *
 * <p><b>Data Components:</b></p>
 * <ul>
 *   <li><b>inspection:</b> Item details and attributes (required)</li>
 *   <li><b>listings:</b> Historical market seller orders and prices (optional, tradeable items only)</li>
 *   <li><b>orders:</b> Historical market buyer orders and demand (optional, tradeable items only)</li>
 * </ul>
 *
 * <p><b>Tradeability Optimization:</b></p>
 * Market data is only fetched for tradeable items. For non-tradeable items, the builder
 * returns immediately with inspection data only, avoiding unnecessary API calls.
 *
 * <p><b>Usage:</b></p>
 * <pre>
 * Response&lt;ItemOverview&gt; response = ItemOverview.Builder
 *     .of("itemHash")
 *     .withListings()
 *     .withOrders()
 *     .build();
 *
 * if (response.isSuccessful()) {
 *     ItemOverview overview = response.getData();
 *     ItemInspection inspection = overview.getInspection();
 *     if (inspection.getItem().isTradeable()) {
 *         MarketHistory listings = overview.getListings();
 *         MarketHistory orders = overview.getOrders();
 *     }
 * }
 * </pre>
 *
 * @see ItemInspection Item inspection data
 * @see MarketHistory Market price and order history
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ItemOverview {

    private ItemInspection inspection;

    @Nullable
    private MarketHistory listings;

    @Nullable
    private MarketHistory orders;

    /**
     * Builder for constructing ItemOverview instances with selective market data loading.
     *
     * <p><b>Usage Pattern:</b></p>
     * <pre>
     * ItemOverview.Builder.of(itemHash)
     *     .withListings()
     *     .withOrders()
     *     .build();
     * </pre>
     *
     * <p><b>Smart Loading:</b> If the item is not tradeable, queued market data requests
     * are skipped automatically, improving performance.</p>
     */
    public static class Builder extends OverviewBuilder<ItemOverview> {

        private final String hashedId;

        private Builder(String hashedId) {
            super(new ItemOverview());
            this.hashedId = hashedId;
        }

        /**
         * Creates a new Builder for the specified item.
         *
         * @param hashedId The hashed item ID
         * @return A new Builder instance
         */
        public static Builder of(String hashedId) {
            return new Builder(hashedId);
        }

        /**
         * Includes market seller order history (listings).
         *
         * <p>Only fetched if the item is tradeable. For non-tradeable items, this request
         * is skipped during {@code build()}.</p>
         *
         * @return This builder for method chaining
         */
        public Builder withListings() {
            this.withGeneric(
                    () -> Requester.getMarketHistory(hashedId, MarketType.LISTINGS),
                    this.getOverview()::setListings
            );
            return this;
        }

        /**
         * Includes market buyer order history (orders).
         *
         * <p>Only fetched if the item is tradeable. For non-tradeable items, this request
         * is skipped during {@code build()}.</p>
         *
         * @return This builder for method chaining
         */
        public Builder withOrders() {
            this.withGeneric(
                    () -> Requester.getMarketHistory(hashedId, MarketType.ORDERS),
                    this.getOverview()::setOrders
            );
            return this;
        }

        /**
         * Builds the ItemOverview with intelligent market data fetching.
         *
         * <p><b>Process:</b></p>
         * <ol>
         *   <li>Fetches item inspection</li>
         *   <li>If non-tradeable, returns immediately with inspection data only</li>
         *   <li>If tradeable, executes all queued market data requests</li>
         *   <li>Returns error immediately on first failure (no partial data)</li>
         *   <li>Returns success with complete overview</li>
         * </ol>
         *
         * @return Response containing the ItemOverview or error details
         */
        public Response<ItemOverview> build() {
            Response<ItemInspection> inspection = Requester.inspectItem(hashedId);
            if (!inspection.isSuccessful()) {
                return new Response<>(inspection.getResponseCode(), null, inspection.getError());
            }

            this.getOverview().setInspection(inspection.getData());

            if (!inspection.getData().getItem().isTradeable()) {
                return new Response<>(ResponseCode.SUCCESS, this.getOverview(), null);
            }

            Response<?> tasks = this.processQueue();
            if (!tasks.isSuccessful()) {
                return new Response<>(tasks.getResponseCode(), null, tasks.getError());
            }

            return new Response<>(ResponseCode.SUCCESS, this.getOverview(), null);
        }
    }
}
