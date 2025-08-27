package de.shurablack.model.shrine;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.shurablack.model.item.Effect;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a stage in the shrine system.
 * This class contains details about the shrine stage, including its ID, tier, effects, progress values,
 * percentage completion, goal achievement time, and activation statuses.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ShrineStage {

    /**
     * The unique identifier for the shrine stage.
     */
    private int id;

    /**
     * The tier associated with the shrine stage.
     */
    private Tier tier;

    /**
     * The list of effects applied to the shrine stage.
     */
    private List<Effect> effects;

    /**
     * The current value of progress in the shrine stage.
     */
    private long currentValue;

    /**
     * The target value required to complete the shrine stage.
     */
    private long targetValue;

    /**
     * The remaining value needed to reach the target.
     */
    private long targetRemaining;

    /**
     * The percentage of progress completed in the shrine stage.
     */
    private double percentage;

    /**
     * The timestamp when the goal was reached.
     */
    private LocalDateTime goalReachedAt;

    /**
     * Indicates whether the shrine stage is currently active.
     */
    @JsonProperty("is_active")
    private boolean active;

    /**
     * Indicates whether the shrine stage is in progress.
     */
    @JsonProperty("in_progress")
    private boolean progress;

    /**
     * Indicates whether the shrine stage can be activated.
     */
    @JsonProperty("can_activate")
    private boolean activatable;

}