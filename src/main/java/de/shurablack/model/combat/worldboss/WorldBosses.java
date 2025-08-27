package de.shurablack.model.combat.worldboss;

import de.shurablack.model.combat.EndpointUpdate;
import lombok.*;

import java.util.List;

/**
 * Represents a collection of world bosses in the system.
 * This class extends the EndpointUpdate class to include information about the dungeons
 * and the time when the endpoint data was last updated.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class WorldBosses extends EndpointUpdate {

    /**
     * The list of world bosses currently available in the system.
     */
    private List<WorldBoss> worldBosses;

}