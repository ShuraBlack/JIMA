package de.shurablack.jima.model.world;

import de.shurablack.jima.model.EndpointUpdate;
import lombok.*;

import java.util.List;

/**
 * Container for all world locations with extended weather forecast data.
 *
 * <p><b>Usage:</b></p>
 * Use to retrieve all locations and their weather information:
 * <pre>
 * Response&lt;WorldLocations&gt; response = Requester.getWorldLocations();
 * if (response.isSuccessful()) {
 *     List&lt;Location&gt; locations = response.getData().getLocations();
 *     for (Location loc : locations) {
 *         List&lt;Forecast&gt; forecasts = loc.getForecast();
 *         // Process weather data for each location
 *     }
 * }
 * </pre>
 *
 * @see Location For individual location details and weather data
 * @see Forecast For daily weather forecasts
 * @see EndpointUpdate For API data freshness tracking
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class WorldLocations extends EndpointUpdate {

    /**
     * List of all game world locations with their complete weather forecast data.
     * Each location includes coordinates, description, and multi-day weather forecasts.
     *
     * @see Location
     */
    private List<Location> locations;

}
