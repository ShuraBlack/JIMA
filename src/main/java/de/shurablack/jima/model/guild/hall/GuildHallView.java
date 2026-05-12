package de.shurablack.jima.model.guild.hall;

import de.shurablack.jima.model.EndpointUpdate;
import lombok.*;

/**
 * API response wrapper for guild hall data including metadata.
 *
 * <p>Extends {@link EndpointUpdate} to include timestamp information
 * about when the guild hall data was last updated.</p>
 *
 * @see GuildHall For the guild hall data model
 * @see EndpointUpdate For inherited timestamp metadata
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class GuildHallView extends EndpointUpdate {

    /** The guild hall data */
    private GuildHall guildHall;

}
