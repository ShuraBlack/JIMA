package de.shurablack.jima.model.character.pet;

import de.shurablack.jima.model.shared.TimeRange;
import de.shurablack.jima.util.Nullable;
import de.shurablack.jima.util.types.Quality;
import de.shurablack.jima.util.types.SecondaryStatType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;


/**
 * Comprehensive detail information for a single pet companion.
 *
 * <p><b>Overview:</b> PetDetail aggregates all information about a pet including
 * identification, attributes, stats, evolution progress, and active periods.</p>
 *
 * <p><b>Pet Identification:</b></p>
 * <ul>
 *   <li>{@code id} - Unique instance ID for this specific pet</li>
 *   <li>{@code name} - Default pet name (e.g., species name)</li>
 *   <li>{@code customName} - Player-assigned nickname (optional)</li>
 *   <li>{@code petId} - Template/species ID</li>
 *   <li>{@code imageUrl} - Pet artwork or icon URL</li>
 * </ul>
 *
 * <p><b>Pet Attributes:</b></p>
 * <ul>
 *   <li>{@code level} - Current combat level (affects stats and abilities)</li>
 *   <li>{@code experience} - Experience toward next level</li>
 *   <li>{@code totalExperience} - Cumulative experience earned</li>
 *   <li>{@code quality} - Rarity tier (Common, Rare, Epic, etc.)</li>
 *   <li>{@code stats} - Secondary stat map (Attack, Defense, Speed, etc.)</li>
 * </ul>
 *
 * <p><b>Pet Status Records:</b></p>
 * <ul>
 *   <li>{@code health} - Health points (HP) tracking</li>
 *   <li>{@code happiness} - Pet happiness level (affects performance)</li>
 *   <li>{@code hunger} - Pet hunger state (needs feeding)</li>
 * </ul>
 *
 * <p><b>Pet Progression:</b></p>
 * <ul>
 *   <li>{@code evolution} - Evolution state and available bonuses</li>
 *   <li>{@code location} - Current location in the game world</li>
 * </ul>
 *
 * <p><b>Activity Tracking:</b></p>
 * <ul>
 *   <li>{@code battle} - Active battle cooldown period</li>
 *   <li>{@code createdAt} - When the pet was acquired or created</li>
 * </ul>
 *
 * @see Record For health, happiness, and hunger detail
 * @see Evolution For evolution system details
 * @see Location For pet location information
 * @see TimeRange For battle cooldown tracking
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class PetDetail {

    private Integer id;

    private String name;

    @Nullable
    private String customName;

    private int petId;

    private String petName;

    private String imageUrl;

    private int level;

    private int experience;

    private int totalExperience;

    private Quality quality;

    private Map<SecondaryStatType, Integer> stats;

    private Record health;

    private Record happiness;

    private Record hunger;

    private boolean equipped;

    private TimeRange battle;

    private Evolution evolution;

    private Location location;

    private LocalDateTime createdAt;
}