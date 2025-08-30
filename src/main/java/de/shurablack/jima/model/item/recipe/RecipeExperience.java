package de.shurablack.jima.model.item.recipe;

import de.shurablack.jima.util.types.SkillType;
import de.shurablack.jima.util.types.StatType;
import lombok.*;

/**
 * Represents the experience gained from a recipe.
 * This class contains information about the stat type, stat value, skill type, and skill value
 * associated with a recipe's experience.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class RecipeExperience {

    /**
     * The type of stat associated with the recipe experience.
     */
    private StatType statType;

    /**
     * The value of the stat associated with the recipe experience.
     */
    private int statValue;

    /**
     * The type of skill associated with the recipe experience.
     */
    private SkillType skillType;

    /**
     * The value of the skill associated with the recipe experience.
     */
    private int skillValue;
}