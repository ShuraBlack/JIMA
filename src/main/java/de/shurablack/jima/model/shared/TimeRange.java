package de.shurablack.jima.model.shared;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents a time period between two timestamps.
 *
 * <p><b>Purpose:</b> Generic container for start and end times, used to represent
 * durations or active periods (e.g., battle cooldowns, event windows, training sessions).</p>
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class TimeRange {

    private LocalDateTime startedAt;

    private LocalDateTime endsAt;

}
