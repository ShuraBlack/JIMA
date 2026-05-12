package de.shurablack.jima.model.guild.events;

import de.shurablack.jima.model.EndpointUpdate;
import de.shurablack.jima.model.ref.GuildReference;
import lombok.*;

/**
 * API response wrapper containing guild Energizing Pool information and metadata.
 *
 * @see EnergizingPool The pool data model
 * @see GuildReference Guild identification information
 * @see EndpointUpdate Parent class providing timestamp tracking
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class EnergizingPoolInfo extends EndpointUpdate {

    /**
     * Reference information about the guild that owns this pool.
     * Contains the guild ID, name, and other identifying information.
     *
     * @see GuildReference For available reference fields
     */
    private GuildReference guild;

    /**
     * The actual Energizing Pool data including status, effects, and expiration time.
     *
     * @see EnergizingPool For pool structure breakdown
     */
    private EnergizingPool energizingPool;

}
