package de.shurablack.model.item.recipe;

import lombok.*;

/**
 * Represents a material required for a crafting recipe.
 * This class contains details about the item's hashed ID, name, and the quantity needed.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class RecipeMaterial {

    /**
     * The unique hashed identifier of the item.
     */
    private String hashedItemId;

    /**
     * The name of the item.
     */
    private String itemName;

    /**
     * The quantity of the item required for the recipe.
     */
    private int quantity;

}