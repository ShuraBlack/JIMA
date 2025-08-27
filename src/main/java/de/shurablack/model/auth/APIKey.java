package de.shurablack.model.auth;

import de.shurablack.util.Nullable;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents an API key used for authentication and authorization in the system.
 * This class contains details about the API key, such as its name, rate limit,
 * expiration time, and associated scopes.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class APIKey {

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