package de.shurablack.jima.model.character;

import de.shurablack.jima.model.ref.CharacterReference;
import de.shurablack.jima.model.EndpointUpdate;
import lombok.*;

import java.util.List;

/**
 * Represents a collection of alternate characters associated with a primary character.
 * This class extends `EndpointUpdate` and contains a list of character references.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class CharacterAlts extends EndpointUpdate {

    /**
     * A list of references to alternate characters.
     */
    private List<CharacterReference> characters;

}