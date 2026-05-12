package de.shurablack.jima.model.guild.hall.blueprint;

import lombok.*;

/**
 * Duration representation with both raw and human-readable formats.
 *
 * <p>Used for blueprint construction times and upgrade durations.
 * Raw value represents milliseconds, while readable provides a formatted string
 * like "5 days 3 hours" for user display.</p>
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Length {

    /** Duration in milliseconds */
    private long raw;

    /** Human-readable duration format (e.g., "5 days 3 hours") */
    private String readable;

}
