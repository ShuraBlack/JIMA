package de.shurablack.jima.model.overview;

import de.shurablack.jima.http.Response;
import lombok.Getter;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Abstract base class for building composite overview objects with optional data components.
 *
 * <p><b>Purpose:</b></p>
 * Provides a reusable task queue pattern for builders that need to fetch multiple optional
 * data sources and handle errors consistently. Subclasses implement specific overview types
 * (character, guild, item) with their own data components.
 *
 * <p><b>Pattern:</b></p>
 * <ul>
 *   <li>Required data is fetched during {@code build()}</li>
 *   <li>Optional data is queued via {@code withGeneric()} calls</li>
 *   <li>All queued tasks execute sequentially with fail-fast error handling</li>
 *   <li>Errors are returned immediately; no partial data is returned</li>
 * </ul>
 *
 * You can also build your own Overview objects if you want to!
 *
 * @param <T> The overview type being built
 * @see CharacterOverview Builder for character data
 * @see GuildOverview Builder for guild data
 * @see ItemOverview Builder for item data
 */
@Getter
public abstract class OverviewBuilder<T> {

    private final T overview;
    private final Queue<Supplier<Response<?>>> queue = new LinkedList<>();

    /**
     * Protected constructor for subclasses.
     *
     * @param overview The overview instance to build
     */
    public OverviewBuilder(T overview) {
        this.overview = overview;
    }

    /**
     * Queues an API request with a setter for simple data assignment.
     *
     * <p>Use this for straightforward data fetching and assignment without transformation.</p>
     *
     * @param <E> The data type being fetched
     * @param requester Supplier that makes the API request and returns a Response
     * @param setter Consumer that assigns the data to the overview object
     *
      * <p><b>Example:</b></p>
      * <pre>{@code
      * this.withGeneric(
      *     () -> Requester.getCharacterMetrics(hashedId),
      *     this.getOverview()::setMetric
      * );
      * }
      * </pre>
     */
    public <E> void withGeneric(
            Supplier<Response<E>> requester,
            Consumer<E> setter
    ) {
        queue.add(() -> {
            Response<E> response = requester.get();
            if (response.isSuccessful() && response.getData() != null) {
                setter.accept(response.getData());
            }
            return response;
        });
    }

    /**
     * Queues an API request with a transformer and setter for data transformation.
     *
     * <p>Use this when the fetched data needs processing before assignment.
     * The transformer is called before the setter.</p>
     *
     * @param <E> The data type being fetched
     * @param requester Supplier that makes the API request and returns a Response
     * @param setter Consumer that assigns the transformed data to the overview object
     * @param transformer Consumer that processes the data before assignment
     *
      * <p><b>Example:</b></p>
      * <pre>{@code
      * this.withGeneric(
      *     Requester::getCurrentGuildConquest,
      *     this.getOverview()::setConquest,
      *     conquest -> filterConquestZones(id, conquest)
      * );
      * }
      * </pre>
     */
    public <E> void withGeneric(
            Supplier<Response<E>> requester,
            Consumer<E> setter,
            Consumer<E> transformer
    ) {
        queue.add(() -> {
            Response<E> response = requester.get();
            if (response.isSuccessful() && response.getData() != null) {
                transformer.accept(response.getData());
                setter.accept(response.getData());
            }
            return response;
        });
    }

    /**
     * Executes all queued tasks sequentially with fail-fast error handling.
     *
     * <p><b>Behavior:</b></p>
     * <ul>
     *   <li>Polls tasks from the queue in order</li>
     *   <li>Stops immediately on first failure</li>
     *   <li>Returns null on complete success, or error Response on failure</li>
     * </ul>
     *
     * @return null if all tasks succeed, or the first failed Response
     */
    public Response<?> processQueue() {
        while (!queue.isEmpty()) {
            Supplier<Response<?>> task = queue.poll();
            Response<?> response = task.get();
            if (!response.isSuccessful()) {
                return new Response<>(response.getResponseCode(), null, response.getError());
            }
        }
        return null;
    }
}
