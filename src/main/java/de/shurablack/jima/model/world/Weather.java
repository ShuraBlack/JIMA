package de.shurablack.jima.model.world;

import de.shurablack.jima.util.types.WeatherType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a specific weather condition and its effects at a location.
 *
 * <p><b>Buffs and Effects:</b></p>
 * The {@code buffs} list contains effect identifiers that are active during this weather.
 * These buffs/debuffs affect gameplay and character attributes while the weather is active.
 * Weather typically changes based on in-game time cycles (day/night).
 *
 * <p><b>Timing:</b></p>
 * Weather has both precise timing ({@code LocalDateTime}) and human-readable time strings.
 * Use {@code startsAtTime} for display purposes and {@code startsAt}/{@code endsAt} for
 * programmatic time comparisons and scheduling.
 *
 * @see Location For locations with weather forecasts
 * @see Forecast For daily weather collections
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Weather {

    /**
     * Unique identifier for this weather type (e.g., "rain", "clear", "fog").
     */
    private WeatherType key;

    /**
     * URL or icon identifier for displaying weather condition visually.
     */
    private String icon;

    /**
     * Human-readable name of the weather condition.
     */
    private String name;

    /**
     * Time window descriptor.
     */
    private String window;

    /**
     * Precise start time of this weather condition as LocalDateTime.
     * Use for programmatic time comparisons.
     */
    private LocalDateTime startsAt;

    /**
     * Start time formatted as a string (e.g., "20:00", "08:30").
     * Use for display purposes in user interfaces.
     */
    private String startsAtTime;

    /**
     * Precise end time of this weather condition as LocalDateTime.
     * Use for programmatic time comparisons.
     */
    private LocalDateTime endsAt;

    /**
     * List of buff/debuff effect IDs active during this weather condition.
     * These effects modify character attributes and gameplay while this weather is active.
     */
    private List<String> buffs;

}
