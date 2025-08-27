package de.shurablack.model.auth;

import lombok.*;

/**
 * Represents a character in the system.
 * This class contains details about the character, such as its ID, hashed ID, and name.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Character {

    /**
     * The unique identifier of the character.
     */
    private String id;

    /**
     * The hashed version of the character's ID.
     */
    private String hashedId;

    /**
     * The name of the character.
     */
    private String name;

}