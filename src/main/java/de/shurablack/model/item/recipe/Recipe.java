package de.shurablack.model.item.recipe;

import lombok.*;

import java.util.List;

/**
 * Represents a crafting recipe in the system.
 * This class contains details about the skill required, level, maximum uses, experience gained,
 * the materials needed, and the resulting item from the recipe.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Recipe {

    /**
     * The skill required to craft the recipe.
     */
    private String skill;

    /**
     * The level required to use the recipe.
     */
    private int levelRequired;

    /**
     * The maximum number of times the recipe can be used.
     */
    private int maxUses;

    /**
     * The amount of experience gained from crafting the recipe.
     */
    private int experience;

    /**
     * The list of materials required to craft the recipe.
     */
    private List<RecipeMaterial> materials;

    /**
     * The result of crafting the recipe.
     */
    private RecipeResult result;
}