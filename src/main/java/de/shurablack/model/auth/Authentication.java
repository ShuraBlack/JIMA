package de.shurablack.model.auth;

import lombok.*;

import java.util.Map;

/**
 * Represents the authentication details of a user in the system.
 * This class contains information about the authentication status,
 * the associated user, character, and API key.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Authentication {

    /**
     * Indicates whether the user is authenticated.
     */
    private boolean authenticated;

    /**
     * A map containing user details, where the key is a string representing
     * the detail name and the value is an integer representing the detail value.
     */
    private Map<String, Integer> user;

    /**
     * The character associated with the authenticated user.
     */
    private Character character;

    /**
     * The API key associated with the authenticated user.
     */
    private APIKey apiKey;

}