package de.shurablack.model.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.shurablack.model.combat.Quality;
import de.shurablack.model.item.recipe.Recipe;
import de.shurablack.model.ref.UpgradeReference;
import de.shurablack.util.Nullable;
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
@EqualsAndHashCode
@ToString
public class ItemDetail {

    /**
     * The unique hashed identifier of the item.
     */
    private String hashedId;

    /**
     * The name of the item.
     */
    private String name;

    /**
     * A brief description of the item (nullable).
     */
    @Nullable
    private String description;

    /**
     * The URL of the item's image.
     */
    private String imageUrl;

    /**
     * The type of the item (e.g., weapon, armor).
     */
    private String type;

    /**
     * The quality of the item.
     */
    private Quality quality;

    /**
     * The vendor price of the item (nullable).
     */
    @Nullable
    private Integer vendorPrice;

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
    private Map<String, Integer> stats;

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
    private GatherLocation whereToFind;

}