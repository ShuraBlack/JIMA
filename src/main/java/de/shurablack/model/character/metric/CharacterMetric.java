package de.shurablack.model.character.metric;

import de.shurablack.model.combat.EndpointUpdate;
import lombok.*;

/**
 * Represents the metrics associated with a character.
 * This class extends the `EndpointUpdate` class and contains details about the character's metrics.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class CharacterMetric extends EndpointUpdate {

    /**
     * The metrics associated with the character.
     */
    private Metric metrics;

}