package de.shurablack.model.guild.conquest;

import de.shurablack.model.combat.EndpointUpdate;
import lombok.*;

import java.util.Map;

/**
 * Represents a guild conquest in the system.
 * This class extends `EndpointUpdate` and contains a map of zones associated with the conquest.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class GuildConquest extends EndpointUpdate {

    /**
     * A map of zones associated with the guild conquest.
     * The key is a string representing the zone name, and the value is a `Zone` object.
     */
    private Map<String, Zone> zones;

}