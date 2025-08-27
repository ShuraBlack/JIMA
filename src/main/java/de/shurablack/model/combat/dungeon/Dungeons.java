package de.shurablack.model.combat.dungeon;

import de.shurablack.model.combat.EndpointUpdate;
import lombok.*;

import java.util.List;

/**
 * Represents a collection of dungeons in the system.
 * This class extends the EndpointUpdate class to include information about the dungeons
 * and the time when the endpoint data was last updated.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class Dungeons extends EndpointUpdate {

    /**
     * The list of dungeons currently available in the system.
     */
    private List<Dungeon> dungeons;

}