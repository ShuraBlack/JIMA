package de.shurablack.jima.model.auth;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.shurablack.jima.http.serialization.AuthenticationDeserializer;
import de.shurablack.jima.model.ref.CharacterReference;
import de.shurablack.jima.util.Nullable;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
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
@JsonDeserialize(using = AuthenticationDeserializer.class)
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
    private CharacterReference character;

    /**
     * The name of the API key. This field is optional and can be null.
     */
    @Nullable
    private String name;

    /**
     * The rate limit associated with the API key, indicating the maximum number
     * of requests that can be made within a specific time period.
     */
    private int rateLimit;

    /**
     * The expiration time of the API key. This field is optional and can be null.
     * <br><br>
     * If null, the API key does not expire.
     */
    @Nullable
    private LocalDateTime expiresAt;

    /**
     * A list of scopes associated with the API key, defining the permissions
     * granted to the key. This field is optional and can be null.
     * <br><br>
     * If null, the API key has all permissions.
     */
    @Nullable
    private List<String> scopes;

}