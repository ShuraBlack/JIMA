package de.shurablack.jima.model.guild.hall.blueprint;

import de.shurablack.jima.model.guild.hall.requirement.Requirement;
import de.shurablack.jima.util.Nullable;
import de.shurablack.jima.util.types.BlueprintType;
import lombok.*;

import java.util.List;

/**
 * Guild hall upgrade or construction blueprint.
 *
 * <p><b>Replacement:</b></p>
 * If {@code isReplacement} is true, this blueprint replaces a previous version
 * identified by {@code replacesBlueprintId}.
 * </p>
 *
 * @see Requirement For material requirements
 * @see Length For construction duration
 * @see BlueprintType For blueprint classification
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Blueprint {

    /** Unique blueprint identifier */
    private int id;

    /** Machine-readable blueprint key */
    private String key;

    /** Display name of the blueprint */
    private String name;

    /** Blueprint type (upgrade or construction) */
    private BlueprintType type;

    /** Minimum guild level required to construct */
    private int levelNeeded;

    /** Whether this blueprint is currently available for construction */
    private boolean isAvailable;

    /** Optional image URL for visual representation */
    @Nullable
    private String imageUrl;

    /** Detailed description of the upgrade/construction */
    private String description;

    /** Construction cost in gold/resources */
    private int cost;

    /** Construction duration with raw and readable formats */
    private Length length;

    /** Material requirements needed to construct */
    private List<Requirement> requirements;

    /** Whether this blueprint replaces a previous version */
    private boolean isReplacement;

    /** ID of the blueprint being replaced (if applicable) */
    @Nullable
    private String replacesBlueprintId;

    /** Benefits provided by this upgrade upon completion */
    private List<String> benefits;

}
