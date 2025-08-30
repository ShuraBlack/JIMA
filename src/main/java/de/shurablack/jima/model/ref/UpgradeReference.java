package de.shurablack.jima.model.ref;

import lombok.*;

/**
 * Represents a reference to an upgrade.
 * This class contains details about the item's unique identifier and the quantity required for the upgrade.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class UpgradeReference {

    /**
     * The unique identifier of the item associated with the upgrade.
     */
    private int itemId;

    /**
     * The quantity required for the upgrade.
     */
    private int quantity;

}