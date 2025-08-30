package de.shurablack.jima.model.character.pet;

import de.shurablack.jima.model.EndpointUpdate;
import lombok.*;

import java.util.List;

/**
 * Represents a collection of a character's pets.
 * This class extends the `EndpointUpdate` class and contains details about the character's pets.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class CharacterPets extends EndpointUpdate {

    /**
     * The list of pets associated with the character.
     */
    private List<PetDetail> pets;

}