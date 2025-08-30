package de.shurablack.jima.model.character.effect;

import de.shurablack.jima.model.EndpointUpdate;
import lombok.*;

import java.util.List;

/**
 * Represents the effects applied to a character.
 * This class extends the `EndpointUpdate` class and contains a list of effects.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class CharacterEffects extends EndpointUpdate {

    /**
     * The list of effects applied to the character.
     */
    private List<Effect> effects;

}