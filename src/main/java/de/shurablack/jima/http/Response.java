package de.shurablack.jima.http;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.function.Function;
import java.util.function.Consumer;

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
    public boolean isSuccessful() {
        return responseCode.equals(ResponseCode.SUCCESS);
    }

    /**
     * Retrieves the response data or throws an exception if the response was not successful.
     * Provides a convenient way to extract data when you expect the request to succeed.
     *
     * @return The response data if the response is successful.
     * @throws IllegalStateException if the response was not successful, with the error message as the exception message.
     */
    public T getDataOrThrow() {
        if (!isSuccessful()) {
            throw new IllegalStateException("Response failed: " + error);
        }
        return data;
    }

    /**
     * Transforms the response data using the provided mapping function.
     * If the response is successful, applies the mapper to the data and returns a new Response with the transformed data.
     * If the response failed, returns a new Response with the same error state but null data.
     *
     * @param mapper A function that transforms the response data from type T to type U.
     * @param <U>    The type of the transformed data.
     * @return A new Response with the transformed data if successful, or a Response with the same error state if not.
     */
    public <U> Response<U> map(Function<T, U> mapper) {
        if (!isSuccessful()) {
            return new Response<>(responseCode, null, error);
        }
        return new Response<>(responseCode, mapper.apply(data), null);
    }

    /**
     * Retrieves the response data or returns a default value if the response was not successful.
     * Provides a safe way to extract data with a fallback when errors occur.
     *
     * @param defaultValue The value to return if the response was not successful.
     * @return The response data if successful, or the provided default value if not.
     */
    public T orElse(T defaultValue) {
        return isSuccessful() ? data : defaultValue;
    }

    /**
     * Executes the provided action if the response was successful.
     * Useful for side-effect operations like logging, UI updates, or further processing.
     *
     * @param action A consumer that accepts the response data and executes when response is successful.
     */
    public void ifSuccessful(Consumer<T> action) {
        if (isSuccessful()) {
            action.accept(data);
        }
    }

    /**
     * Executes the provided action if the response was not successful.
     * Useful for error handling, logging failures, or user notification.
     *
     * @param action A consumer that accepts the error message and executes when response failed.
     */
    public void ifFailed(Consumer<String> action) {
        if (!isSuccessful()) {
            action.accept(error);
        }
    }

}