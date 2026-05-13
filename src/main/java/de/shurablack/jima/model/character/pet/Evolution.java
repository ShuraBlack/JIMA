package de.shurablack.jima.model.character.pet;

import de.shurablack.jima.model.shared.Label;
import de.shurablack.jima.util.types.SecondaryStatType;
import lombok.*;

import java.util.List;

/**
 * Represents a pet's evolution state, progress, and bonuses toward next evolution.
 *
 * <p><b>Evolution System:</b> Pets progress through multiple evolution states (0 to max).
 * Each stage provides stat bonuses to specific secondary stats. Evolution is triggered
 * when the pet meets certain requirements and get manually evolved.</p>
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Evolution {

    private int state;

    private int max;

    private int bonusPerStage;

    private int currentBonus;

    private int nextBonus;

    private boolean canEvolve;

    private List<Label<SecondaryStatType>> targets;

}
