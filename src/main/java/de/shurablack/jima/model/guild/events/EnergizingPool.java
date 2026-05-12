package de.shurablack.jima.model.guild.events;

import de.shurablack.jima.util.Nullable;
import de.shurablack.jima.util.types.PoolStatusType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a guild Energizing Pool event that provides buffs and status effects to guild members.
 *
 * <p><b>What Is An Energizing Pool?</b></p>
 * An Energizing Pool is a guild event mechanic that applies beneficial buffs to all eligible guild members
 * for a limited time period.
 *
 * @see PoolStatusType Status enum for pool operational states
 * @see EnergizingPoolInfo Wrapper containing guild reference and pool data
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class EnergizingPool {

    /**
     * Unique identifier for this pool instance.
     * May be {@code null} in certain API responses.
     */
    @Nullable
    private Integer id;

    /**
     * Current operational status of the pool.
     *
     * <p><b>States:</b></p>
     * <ul>
     *   <li>DORMANT - Not active, no buffs provided</li>
     *   <li>ACTIVE_BUT_NOT_APPLIED - Active but buffs not yet applied</li>
     *   <li>ACTIVE_AND_APPLIED - Active and buffs are active</li>
     * </ul>
     *
     * @see PoolStatusType For available status values
     */
    private PoolStatusType status;

    /**
     * Timestamp when the pool becomes inactive or expires.
     * Used to determine remaining duration and UI countdown displays.
     */
    private LocalDateTime endsAt;

    /**
     * List of status effects and buffs provided by this pool.
     */
    private List<String> effects;
}
