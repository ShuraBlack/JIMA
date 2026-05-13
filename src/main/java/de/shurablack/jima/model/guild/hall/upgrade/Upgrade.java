package de.shurablack.jima.model.guild.hall.upgrade;

import de.shurablack.jima.model.guild.hall.Repair;
import de.shurablack.jima.model.guild.hall.blueprint.Blueprint;
import de.shurablack.jima.util.Nullable;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Active or in-progress guild hall upgrade.
 *
 * <p><b>Upgrade Lifecycle:</b></p>
 * <ul>
 *   <li><b>In Progress:</b> Currently being constructed (status and endsAt/endsIn set)</li>
 *   <li><b>Completed:</b> Finished and providing benefits to the guild</li>
 * </ul>
 *
 * <p><b>Repair and Maintenance:</b> Completed upgrades may require repair if their condition drops.
 * The {@code repair} field tracks maintenance status.</p>
 *
 * <p><b>Next Upgrade:</b> If {@code availableUpgrade} is present, the upgrade can be improved to a newer version.</p>
 *
 * @see Status For upgrade status information
 * @see Repair For repair and condition information
 * @see Blueprint For the upgrade blueprint
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Upgrade {

    /** Unique upgrade identifier */
    private int id;

    /** Current status (in_progress, completed, etc.) */
    private Status status;

    /** Repair and condition information */
    private Repair repair;

    /** Next available upgrade version if applicable */
    @Nullable
    private Upgrade availableUpgrade;

    /** Date and time when upgrade construction completes */
    @Nullable
    private LocalDateTime endsAt;

    /** Milliseconds remaining until upgrade completes */
    @Nullable
    private Long endsIn;

    /** Blueprint defining this upgrade */
    private Blueprint blueprint;


}
