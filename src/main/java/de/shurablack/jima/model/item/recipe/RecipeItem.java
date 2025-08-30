package de.shurablack.jima.model.item.recipe;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * Represents an item in context of a crafting recipe.
 * This class contains details about the item's hashed ID, name, and the quantity needed.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class RecipeItem {

    /**
     * The unique hashed identifier of the item.
     */
    private String hashedItemId;

    /**
     * The name of the item.
     */
    private String itemName;

    /**
     * The quantity of the item.
     */
    @JsonProperty(defaultValue = "1")
    private int quantity = 1;

}
