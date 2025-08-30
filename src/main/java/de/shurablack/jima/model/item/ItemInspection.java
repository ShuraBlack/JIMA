package de.shurablack.jima.model.item;

import de.shurablack.jima.model.EndpointUpdate;
import lombok.*;

/**
 * Represents an inspection of an item.
 * This class extends the EndpointUpdate class and contains details about the inspected item.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class ItemInspection extends EndpointUpdate {

    /**
     * The details of the inspected item.
     */
    private ItemDetail item;

}