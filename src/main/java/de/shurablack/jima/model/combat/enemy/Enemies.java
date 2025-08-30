package de.shurablack.jima.model.combat.enemy;

import de.shurablack.jima.model.EndpointUpdate;
import lombok.*;

import java.util.List;

/**
 * Represents a collection of enemies in the system.
 * This class extends the EndpointUpdate class to include information about the enemies
 * and the time when the endpoint data was last updated.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class Enemies extends EndpointUpdate {

    /**
     * The list of enemies currently available in the system.
     */
    private List<Enemy> enemies;

}