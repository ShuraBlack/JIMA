package de.shurablack.jima.model.guild.hall;

import de.shurablack.jima.model.guild.hall.blueprint.Blueprint;
import de.shurablack.jima.model.guild.hall.upgrade.Upgrade;
import de.shurablack.jima.model.ref.LocationReference;
import de.shurablack.jima.util.Nullable;
import lombok.*;

import java.util.List;

/**
 * Guild hall main data model representing a guild's customizable headquarters.
 *
 * <p><b>Overview:</b></p>
 * A guild hall is the guild's headquarters building, containing upgrade slots and
 * customization options through blueprints. Guilds can improve their hall by
 * constructing upgrades that provide various benefits.
 *
 * @see GuildHallView For API response wrapper
 * @see Upgrade For active upgrade details
 * @see Blueprint For construction blueprints
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class GuildHall {

    /** Optional guild hall ID for identification */
    @Nullable
    private Integer id;

    /** Display name of the guild hall */
    private String name;

    /** Location reference where the hall is situated */
    private LocationReference location;

    /** Slot capacity and usage information */
    private Slots slots;

    /** Currently active or in-progress upgrades */
    private List<Upgrade> upgrades;

    /** Available blueprints for construction and improvement */
    private List<Blueprint> blueprints;
}
