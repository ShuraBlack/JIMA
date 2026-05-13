package de.shurablack.jima.model.character.pet;

import lombok.*;

/**
 * Tracks a numeric record with current value, maximum capacity, and percentage.
 *
 * <p><b>Purpose:</b> Generic container for bounded metrics used across pet data.
 * Commonly represents health, happiness, hunger, or similar attributes.</p>
 *
 * <p><b>Fields:</b></p>
 * <ul>
 *   <li><b>current:</b> The current value of the metric (0 to maximum)</li>
 *   <li><b>maximum:</b> The upper limit/capacity of the metric</li>
 *   <li><b>percentage:</b> The current value as a percentage (0-100)</li>
 * </ul>
 *
 * <p><b>Example:</b> A pet with 85 health out of 100 maximum would have:</p>
 * <pre>
 * current = 85
 * maximum = 100
 * percentage = 85
 * </pre>
 *
 * @see PetDetail For the context where Record is used
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Record {

    private int current;

    private int maximum;

    private int percentage;

}
