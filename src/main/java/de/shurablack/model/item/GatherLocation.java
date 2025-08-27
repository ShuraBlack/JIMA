package de.shurablack.model.item;

import de.shurablack.model.ref.DungeonReference;
import de.shurablack.model.ref.EnemyReference;
import de.shurablack.model.ref.WorldBossReference;
import lombok.*;

import java.util.List;

/**
 * Represents a location where items can be gathered.
 * This class contains details about the enemies, dungeons, and world bosses
 * associated with the gathering location.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class GatherLocation {

    /**
     * The list of enemies present at the gathering location.
     */
    private List<EnemyReference> enemies;

    /**
     * The list of dungeons associated with the gathering location.
     */
    private List<DungeonReference> dungeons;

    /**
     * The list of world bosses present at the gathering location.
     */
    private List<WorldBossReference> worldBosses;

}