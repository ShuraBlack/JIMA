package de.shurablack.model.item.recipe;

import lombok.*;

/**
 * Represents the result of crafting a recipe.
 * This class contains details about the resulting item's hashed ID and name.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class RecipeResult {

    /**
     * The unique hashed identifier of the resulting item.
     */
    private String hashedItemId;

    /**
     * The name of the resulting item.
     */
    private String itemName;

}