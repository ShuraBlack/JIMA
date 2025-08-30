package de.shurablack.jima.model.guild.conquest;

import de.shurablack.jima.model.EndpointUpdate;
import lombok.*;

/**
 * Represents an inspection of a specific guild conquest in the system.
 * This class extends `EndpointUpdate` and contains details about a specific zone being inspected.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class GuildConquestInspection extends EndpointUpdate {

    /**
     * The zone associated with the guild conquest inspection.
     */
    private Zone zone;

}