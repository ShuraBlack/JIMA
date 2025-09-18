package de.shurablack.jima.model.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.shurablack.jima.model.combat.Quality;
import de.shurablack.jima.model.item.recipe.Recipe;
import de.shurablack.jima.model.ref.UpgradeReference;
import de.shurablack.jima.util.Nullable;
import de.shurablack.jima.util.types.ItemType;
import de.shurablack.jima.util.types.SecondaryStatType;
import lombok.*;

import java.util.List;
import java.util.Map;

/**
 * Represents detailed information about an item.
 * This class contains various attributes of an item, including its name, type, quality,
 * stats, effects, and where it can be found.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class ItemDetail extends Item {

    /**
     * Indicates whether the item is tradeable.
     */
    @JsonProperty("is_tradeable")
    private boolean tradeable;

    /**
     * The maximum tier of the item.
     */
    private int maxTier;

    /**
     * The requirements for using the item, represented as a map of attributes to values (nullable).
     */
    @Nullable
    private Map<String, Integer> requirements;

    /**
     * The stats of the item, represented as a map of attributes to values (nullable).
     */
    @Nullable
    private Map<SecondaryStatType, Double> stats;

    /**
     * The list of effects applied by the item (nullable).
     */
    @Nullable
    private List<Effect> effects;

    /**
     * The tier modifiers of the item, represented as a map of attributes to values (nullable).
     */
    @Nullable
    private Map<String, Double> tierModifiers;

    /**
     * The list of upgrade requirements for the item (nullable).
     */
    @Nullable
    private List<UpgradeReference> upgradeRequirements;

    /**
     * The amount of health restored by the item (nullable).
     */
    @Nullable
    private Integer healthRestore;

    /**
     * The amount of hunger restored by the item (nullable).
     */
    @Nullable
    private Integer hungerRestore;

    /**
     * The crafting recipe for the item (nullable).
     */
    @Nullable
    private Recipe recipe;

    /**
     * The list of chest drops where the item can be found (nullable).
     */
    @Nullable
    private List<ChestDrop> chestDrops;

    /**
     * The pet associated with the item (nullable).
     */
    @Nullable
    private Pet pet;

    /**
     * The location where the item can be gathered.
     */
    @Nullable
    private GatherLocation whereToFind;

}