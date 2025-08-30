package de.shurablack.jima.model.guild;

import de.shurablack.jima.model.EndpointUpdate;
import lombok.*;

/**
 * Represents a view of a guild in the system.
 * This class extends `EndpointUpdate` and contains details about a specific guild.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class GuildView extends EndpointUpdate {

    /**
     * The guild associated with this view.
     */
    private Guild guild;

}