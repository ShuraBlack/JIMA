package de.shurablack.model.character.view;

import de.shurablack.model.combat.EndpointUpdate;
import lombok.*;

/**
 * Represents a view of a character in the system.
 * This class extends `EndpointUpdate` and contains details about a character.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class CharacterView extends EndpointUpdate {

    /**
     * The character associated with this view.
     */
    private Character character;

}