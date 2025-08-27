package de.shurablack.http;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Generic class representing an HTTP response.
 *
 * @param <T> the type of the data contained in the response
 */
@AllArgsConstructor
@Getter
public class Response<T> {

    /**
     * The response code indicating the status of the HTTP response.
     */
    private final ResponseCode responseCode;

    /**
     * The data payload of the response, which can be of any type.
     */
    private final T data;

    /**
     * The error message associated with the response, if any.
     */
    private final String error;

    /**
     * Checks if the response indicates a successful operation.
     *
     * @return true if the response code is SUCCESS, false otherwise
     */
    public boolean isSuccess() {
        return responseCode.equals(ResponseCode.SUCCESS);
    }

}