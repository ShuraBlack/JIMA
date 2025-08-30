package de.shurablack.jima.model.item.market;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.shurablack.jima.model.ref.ItemReference;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents the latest sell transaction for an item in the market.
 * This class contains details about the item, its tier, quantity sold, price per item,
 * total price, and the date and time of the transaction.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class LatestTransaction {

    @JsonProperty(required = false)
    private Long id;

    /**
     * A reference to the item being sold.
     */
    private ItemReference item;

    /**
     * The tier of the item.
     */
    private int tier;

    /**
     * The quantity of the item sold in the transaction.
     */
    private int quantity;

    /**
     * The price per item in the transaction.
     */
    private int pricePerItem;

    /**
     * The total price for the transaction.
     */
    private int totalPrice;

    /**
     * The date and time when the item was sold.
     */
    private LocalDateTime soldAt;

}