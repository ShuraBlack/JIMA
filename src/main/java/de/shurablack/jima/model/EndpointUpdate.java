package de.shurablack.jima.model;

import lombok.*;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;

/**
 * Represents the update information for an endpoint in the system.
 *
 * <p>
 * This abstract base class encapsulates the date and time at which the data of an endpoint
 * will be updated. Concrete implementations extend this class and may add additional metadata
 * (e.g., type of update, reason, source).
 * </p>
 *
 * <h3>Key Features</h3>
 * <ul>
 *   <li>The date/time field {@link #endpointUpdatesAt} uses {@link LocalDateTime},
 *       which contains no timezone information. Comparisons must use a consistent local time
 *       (e.g., also {@code LocalDateTime.now()}).</li>
 *   <li>A {@code null} value in {@link #endpointUpdatesAt} indicates that no update time has
 *       been set or no update is currently scheduled.</li>
 * </ul>
 *
 * @since 2.0.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public abstract class EndpointUpdate {

    /**
     * Date and time at which an endpoint update will occur.
     *
     * <p>
     * This field is of type {@link LocalDateTime} and therefore contains no
     * timezone information. A {@code null} value indicates that no time has been set
     * (e.g., no scheduled update).
     * </p>
     *
     * <p>
     * Note for consumers: When comparing this value against the current time, ensure that you
     * also use a {@link LocalDateTime} value (e.g., {@code LocalDateTime.now()}) and that both
     * values share the same timezone/localization assumptions.
     * </p>
     */
    private LocalDateTime endpointUpdatesAt;

    /**
     * Checks whether the endpoint update time has already passed (expired) relative to the
     * provided reference time {@code now}.
     *
     * <p>
     * Return values:
     * <ul>
     *   <li>{@code true} - if {@link #endpointUpdatesAt} is not {@code null}
     *       and {@code endpointUpdatesAt.isBefore(now)} returns true (the time
     *       is before the reference time).</li>
     *   <li>{@code false} - if {@link #endpointUpdatesAt} is {@code null}
     *       (no time set) or {@code endpointUpdatesAt} is equal to or after
     *       {@code now} (not yet expired).</li>
     * </ul>
     * </p>
     *
     * @param now the reference time to compare against; should never be {@code null}.
     *            If {@code now} is {@code null}, a {@link NullPointerException} may be thrown
     *            because the {@code LocalDateTime#isBefore} method will be invoked.
     * @return {@code true} if an update time is set and already before {@code now};
     *         {@code false} otherwise.
     *
     * @implNote This method does not account for timezone adjustments — use {@link java.time.ZonedDateTime}
     *           or {@link java.time.OffsetDateTime} if timezone correctness is required.
     *
     * @see LocalDateTime#isBefore(ChronoLocalDateTime) (LocalDateTime)
     */
    public boolean isExpired(LocalDateTime now) {
        return endpointUpdatesAt != null && endpointUpdatesAt.isBefore(now);
    }
}