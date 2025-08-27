package de.shurablack.http;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum representing various HTTP response codes and their associated messages.
 * Each response code corresponds to a specific HTTP status and provides a
 * descriptive message for better understanding of the status.
 */
@AllArgsConstructor
@Getter
public enum ResponseCode {
    /**
     * Indicates that the request was completed successfully.
     */
    SUCCESS(200, "Request completed successfully"),

    /**
     * Indicates that the request was malformed or invalid.
     * Additional details can be found in the error code.
     */
    BAD_REQUEST(400, "See error code for details"),

    /**
     * Indicates that the request is unauthorized due to an
     * invalid or missing API key.
     */
    UNAUTHORIZED(401, "Invalid or missing API key"),

    /**
     * Indicates that the request is forbidden due to insufficient
     * permissions or the account being banned.
     */
    FORBIDDEN(403, "Insufficient permissions or account banned"),

    /**
     * Indicates that the requested endpoint or entity does not exist.
     */
    NOT_FOUND(404, "Endpoint or entity does not exist"),

    /**
     * Indicates that the request could not be processed due to
     * validation errors. Additional details can be found in the
     * errors field.
     */
    UNPROCESSABLE_ENTITY(422, "Validation failed. Check the errors field for details"),

    /**
     * Indicates that the rate limit for requests has been exceeded.
     */
    TOO_MANY_REQUESTS(429, "Rate limit exceeded"),

    ;

    /**
     * The HTTP status code associated with the response.
     */
    private final int code;

    /**
     * A descriptive message providing additional information about the response.
     */
    private final String message;

    /**
     * Retrieves the ResponseCode enum constant corresponding to the given HTTP status code.
     *
     * @param code the HTTP status code
     * @return the matching ResponseCode enum constant, or null if no match is found
     */
    public static ResponseCode fromCode(int code) {
        for (ResponseCode rc : values()) {
            if (rc.code == code) {
                return rc;
            }
        }
        return null;
    }
}