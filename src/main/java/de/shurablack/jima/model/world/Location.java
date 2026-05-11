package de.shurablack.jima.model.world;

import de.shurablack.jima.util.Nullable;
import lombok.*;

import java.util.List;

/**
 * Represents a specific location in the game world with weather forecast data.
 *
 * <p><b>Weather Forecasts:</b></p>
 * Each location contains a forecast list with daily weather predictions. Use this to:
 * <ul>
 *   <li>Determine when specific buffs or debuffs are active at a location</li>
 *   <li>Plan activities based on weather conditions</li>
 *   <li>Check event timing for activities requiring specific weather</li>
 * </ul>
 *
 * @see Forecast For daily weather data
 * @see Weather For individual weather condition details
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Location {

    /**
     * Unique numeric identifier for this location.
     */
    private int id;

    /**
     * Display name of the location (human-readable).
     */
    private String name;

    /**
     * Unique string key for referencing this location (typically lowercase).
     */
    private String key;

    /**
     * Optional extended description or lore information about the location.
     * May be {@code null} if not provided by the API.
     */
    @Nullable
    private String description;

    /**
     * URL to the location's image, map thumbnail, or icon.
     */
    private String imageUrl;

    /**
     * X coordinate for positioning this location on a map.
     */
    private int x;

    /**
     * Y coordinate for positioning this location on a map.
     */
    private int y;

    /**
     * Weather forecast data for multiple days at this location.
     * Use to determine weather conditions, buffs, and event timing.
     *
     * @see Forecast
     */
    private List<Forecast> forecast;

}
