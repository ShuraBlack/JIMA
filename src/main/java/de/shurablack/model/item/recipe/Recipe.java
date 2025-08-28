package de.shurablack.model.item.recipe;

import de.shurablack.util.types.SkillType;
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
    private SkillType skill;

    /**
     * The level required to use the recipe.
     */
    private int levelRequired;

    /**
     * The maximum number of times the recipe can be used.
     */
    private int maxUses;

    /**
     * The experience gained from crafting the recipe.
     */
    private RecipeExperience experience;

    /**
     * The list of materials required to craft the recipe.
     */
    private List<RecipeItem> materials;

    /**
     * The result of crafting the recipe.
     */
    private RecipeItem result;

}