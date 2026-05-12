package de.shurablack.jima.model.guild.hall;

import de.shurablack.jima.util.Nullable;
import lombok.*;

/**
 * Guild hall slot capacity tracking.
 *
 * @see GuildHall For the guild hall containing these slots
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Slots {

    /** Maximum number of upgrade slots in the guild hall */
    @Nullable
    private Integer total;

    /** Number of currently free/available slots */
    @Nullable
    private Integer free;

    /** Number of slots occupied by active upgrades */
    @Nullable
    private Integer occupied;

    /** Alternative representation of available slots */
    @Nullable
    private Integer remaining;
}
