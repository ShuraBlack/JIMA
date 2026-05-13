package de.shurablack.jima.model.world;

import lombok.*;

import java.util.List;

/**
 * Represents a daily weather forecast for a specific location.
 *
 * @see Location For the location containing forecasts
 * @see Weather For individual weather condition details
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Forecast {

    /**
     * The name of the day this forecast represents (e.g., "Monday", "Tuesday").
     */
    private String dayName;

    /**
     * The date string for this forecast day (format depends on game API).
     */
    private String date;

    /**
     * List of weather conditions occurring on this forecast day.
     * Each weather can provide buffs and debuffs active during specific time windows.
     *
     * @see Weather
     */
    private List<Weather> weathers;

}
