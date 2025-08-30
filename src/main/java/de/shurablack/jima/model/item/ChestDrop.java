package de.shurablack.jima.model.item;

import lombok.*;

/**
 * Represents a chest drop in the system.
 * This class contains details about the item's hashed ID, name, quantity, and the chance of it dropping.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ChestDrop {

    /**
     * The unique hashed identifier of the item.
     */
    private String hashedItemId;

    /**
     * The name of the item.
     */
    private String itemName;

    /**
     * The quantity of the item in the chest drop.
     */
    private int quantity;

    /**
     * The chance of the item dropping, represented as a percentage.
     */
    private int chance;

}