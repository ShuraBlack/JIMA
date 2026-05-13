package de.shurablack.jima.model.overview;

import de.shurablack.jima.http.Requester;
import de.shurablack.jima.http.Response;
import de.shurablack.jima.http.ResponseCode;
import de.shurablack.jima.model.character.CharacterAction;
import de.shurablack.jima.model.character.CharacterAlts;
import de.shurablack.jima.model.character.effect.CharacterEffects;
import de.shurablack.jima.model.character.metric.CharacterMetric;
import de.shurablack.jima.model.character.museum.CharacterMuseum;
import de.shurablack.jima.model.character.pet.CharacterPets;
import de.shurablack.jima.model.character.view.CharacterView;
import de.shurablack.jima.util.Nullable;
import lombok.*;

/**
 * Comprehensive character information container with optional data components.
 *
 * <p><b>Data Components:</b></p>
 * <ul>
 *   <li><b>character:</b> Base character information (required)</li>
 *   <li><b>metric:</b> Performance and gameplay statistics (optional)</li>
 *   <li><b>effects:</b> Active buffs, debuffs, and status conditions (optional)</li>
 *   <li><b>museum:</b> Collection progress and exhibits (optional)</li>
 *   <li><b>pets:</b> Companion and pet information (optional)</li>
 *   <li><b>action:</b> Current activity and action status (optional)</li>
 *   <li><b>alts:</b> Alternate character roster information (optional)</li>
 * </ul>
 *
 * <p><b>Usage:</b></p>
 * <pre>
 * Response&lt;CharacterOverview&gt; response = CharacterOverview.Builder
 *     .of("characterHash")
 *     .withMetric()
 *     .withEffects()
 *     .withMuseum()
 *     .build();
 *
 * if (response.isSuccessful()) {
 *     CharacterOverview overview = response.getData();
 *     CharacterView character = overview.getCharacter();
 *     CharacterMetric metric = overview.getMetric();
 * }
 * </pre>
 *
 * @see CharacterView Base character data
 * @see CharacterMetric Character statistics and performance
 * @see CharacterEffects Active effects and buffs
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class CharacterOverview {

    private CharacterView character;

    @Nullable
    private CharacterMetric metric;

    @Nullable
    private CharacterEffects effects;

    @Nullable
    private CharacterMuseum museum;

    @Nullable
    private CharacterPets pets;

    @Nullable
    private CharacterAction action;

    @Nullable
    private CharacterAlts alts;

    /**
     * Builder for constructing CharacterOverview instances with selective data loading.
     *
     * <p><b>Usage Pattern:</b></p>
     * Use fluent method chaining to specify which optional data to fetch:
     * <pre>
     * CharacterOverview.Builder.of(hashedId)
     *     .withMetric()
     *     .withEffects()
     *     .build();
     * </pre>
     */
    public static class Builder extends OverviewBuilder<CharacterOverview> {

        private final String hashedId;

        private Builder(String hashedId) {
            super(new CharacterOverview());
            this.hashedId = hashedId;
        }

        /**
         * Creates a new Builder for the specified character.
         *
         * @param hashedId The hashed character ID
         * @return A new Builder instance
         */
        public static Builder of(String hashedId) {
            return new Builder(hashedId);
        }

        /**
         * Includes character performance metrics (statistics and analytics).
         *
         * @return This builder for method chaining
         */
        public Builder withMetric() {
            this.withGeneric(
                    () -> Requester.getCharacterMetrics(hashedId),
                    this.getOverview()::setMetric
            );
            return this;
        }

        /**
         * Includes active effects (buffs, debuffs, and status conditions).
         *
         * @return This builder for method chaining
         */
        public Builder withEffects() {
            this.withGeneric(
                    () -> Requester.getCharacterEffects(hashedId),
                    this.getOverview()::setEffects
            );
            return this;
        }

        /**
         * Includes collection progress and museum exhibits.
         *
         * @return This builder for method chaining
         */
        public Builder withMuseum() {
            this.withGeneric(
                    () -> Requester.getCharacterMuseum(hashedId),
                    this.getOverview()::setMuseum
            );
            return this;
        }

        /**
         * Includes companion and pet information.
         *
         * @return This builder for method chaining
         */
        public Builder withPets() {
            this.withGeneric(
                    () -> Requester.getCharacterPets(hashedId),
                    this.getOverview()::setPets
            );
            return this;
        }

        /**
         * Includes current activity and action status.
         *
         * @return This builder for method chaining
         */
        public Builder withAction() {
            this.withGeneric(
                    () -> Requester.getCharacterAction(hashedId),
                    this.getOverview()::setAction
            );
            return this;
        }

        /**
         * Includes alternate character roster information.
         *
         * @return This builder for method chaining
         */
        public Builder withAlts() {
            this.withGeneric(
                    () -> Requester.getCharacterAlts(hashedId),
                    this.getOverview()::setAlts
            );
            return this;
        }

        /**
         * Builds the CharacterOverview by fetching required and queued optional data.
         *
         * <p><b>Process:</b></p>
         * <ol>
         *   <li>Fetches base character information</li>
         *   <li>Executes all queued optional data requests in order</li>
         *   <li>Returns error immediately on first failure (no partial data)</li>
         *   <li>Returns success with complete overview</li>
         * </ol>
         *
         * @return Response containing the CharacterOverview or error details
         */
        public Response<CharacterOverview> build() {
            Response<CharacterView> characterResponse = Requester.getCharacter(hashedId);
            if (!characterResponse.isSuccessful()) {
                return new Response<>(characterResponse.getResponseCode(), null, characterResponse.getError());
            }

            this.getOverview().setCharacter(characterResponse.getData());

            Response<?> tasks = this.processQueue();
            if (!tasks.isSuccessful()) {
                return new Response<>(tasks.getResponseCode(), null, tasks.getError());
            }

            return new Response<>(ResponseCode.SUCCESS, this.getOverview(), null);
        }
    }
}
