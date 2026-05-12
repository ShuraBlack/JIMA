package de.shurablack.jima.model.character.pet;

import de.shurablack.jima.model.shared.Label;
import de.shurablack.jima.util.types.SecondaryStatType;
import lombok.*;

import java.util.List;

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
