package de.shurablack.jima.http;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for processing and filtering batch responses.
 * Provides convenient methods to extract successful results, collect errors, and generate summaries.
 *
 * @param <T> the type of data contained in the responses
 */
public class ResponseList<T> {

    private final List<Response<T>> responses;

    /**
     * Constructs a ResponseList from a list of Response objects.
     *
     * @param responses The list of responses to process.
     */
    public ResponseList(List<Response<T>> responses) {
        this.responses = responses;
    }

    /**
     * Retrieves all successful response data, filtering out failed responses.
     * Useful for extracting valid results from batch operations.
     *
     * @return A list containing only the data from successful responses.
     */
    public List<T> getSuccessful() {
        return responses.stream()
                .filter(Response::isSuccessful)
                .map(Response::getData)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all error messages from failed responses, filtering out successful ones.
     * Useful for error logging and diagnostics.
     *
     * @return A list containing only the error messages from failed responses.
     */
    public List<String> getErrors() {
        return responses.stream()
                .filter(r -> !r.isSuccessful())
                .map(Response::getError)
                .collect(Collectors.toList());
    }

    /**
     * Gets the total count of responses processed.
     *
     * @return The total number of responses in this list.
     */
    public int getTotalCount() {
        return responses.size();
    }

    /**
     * Gets the count of successful responses.
     *
     * @return The number of successful responses.
     */
    public int getSuccessCount() {
        return (int) responses.stream()
                .filter(Response::isSuccessful)
                .count();
    }

    /**
     * Gets the count of failed responses.
     *
     * @return The number of failed responses.
     */
    public int getErrorCount() {
        return (int) responses.stream()
                .filter(r -> !r.isSuccessful())
                .count();
    }

    /**
     * Gets the success rate as a percentage.
     *
     * @return The percentage of successful responses (0-100).
     */
    public double getSuccessRate() {
        if (responses.isEmpty()) return 0.0;
        return (getSuccessCount() * 100.0) / getTotalCount();
    }

    /**
     * Prints a summary of response statistics to stdout.
     * Useful for debugging and monitoring batch operations.
     *
     * <p>Example output:</p>
     * <pre>
     * Response Summary: 8/10 successful (80.0%)
     * </pre>
     */
    public void printSummary() {
        System.out.println(String.format(
                "Response Summary: %d/%d successful (%.1f%%)",
                getSuccessCount(),
                getTotalCount(),
                getSuccessRate()
        ));
    }

    /**
     * Prints a detailed summary including error count and list.
     * Useful for comprehensive batch operation reporting.
     *
     * <p>Example output:</p>
     * <pre>
     * Response Summary: 8/10 successful (80.0%)
     * Errors: 2
     * - Error message 1
     * - Error message 2
     * </pre>
     */
    public void printDetailedSummary() {
        printSummary();
        List<String> errors = getErrors();
        if (!errors.isEmpty()) {
            System.out.println("Errors: " + errors.size());
            errors.forEach(error -> System.out.println("  - " + error));
        }
    }

    /**
     * Gets the underlying list of responses.
     * Useful for direct access when custom processing is needed.
     *
     * @return The list of responses.
     */
    public List<Response<T>> getResponses() {
        return responses;
    }
}
