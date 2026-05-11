package de.shurablack.jima.model.overview;

import de.shurablack.jima.http.Requester;
import de.shurablack.jima.http.Response;
import de.shurablack.jima.http.ResponseCode;
import de.shurablack.jima.model.guild.GuildMembers;
import de.shurablack.jima.model.guild.GuildView;
import de.shurablack.jima.model.guild.conquest.GuildConquest;
import de.shurablack.jima.model.guild.conquest.Zone;
import de.shurablack.jima.util.Nullable;
import lombok.*;

import java.util.Map;

/**
 * Comprehensive guild information container with optional conquest data.
 *
 * <p><b>Data Components:</b></p>
 * <ul>
 *   <li><b>guild:</b> Guild general information (required)</li>
 *   <li><b>members:</b> Guild member roster (required)</li>
 *   <li><b>conquest:</b> Guild territorial control data, filtered for this guild only (optional)</li>
 * </ul>
 *
 * <p><b>Conquest Data Note:</b></p>
 * When {@code withConquest()} is used, the conquest data is automatically filtered to show only
 * zones where this guild holds control, reducing the response size and improving usability.
 *
 * <p><b>Usage:</b></p>
 * <pre>
 * Response&lt;GuildOverview&gt; response = GuildOverview.Builder
 *     .of(guildId)
 *     .withConquest()
 *     .build();
 *
 * if (response.isSuccessful()) {
 *     GuildOverview overview = response.getData();
 *     GuildView guild = overview.getGuild();
 *     GuildMembers members = overview.getMembers();
 *     GuildConquest conquest = overview.getConquest();
 * }
 * </pre>
 *
 * @see GuildView Guild information
 * @see GuildMembers Guild member roster
 * @see GuildConquest Territorial control data
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class GuildOverview {

    private GuildView guild;

    private GuildMembers members;

    @Nullable
    private GuildConquest conquest;

    /**
     * Builder for constructing GuildOverview instances with selective data loading.
     *
     * <p><b>Usage Pattern:</b></p>
     * <pre>
     * GuildOverview.Builder.of(guildId)
     *     .withConquest()
     *     .build();
     * </pre>
     *
     * <p><b>Note:</b> Guild information and member roster are always fetched; only conquest
     * is optional.</p>
     */
    public static class Builder extends OverviewBuilder<GuildOverview> {

        private final int id;

        private Builder(int id) {
            super(new GuildOverview());
            this.id = id;
        }

        /**
         * Creates a new Builder for the specified guild.
         *
         * @param id The guild ID
         * @return A new Builder instance
         */
        public static Builder of(int id) {
            return new Builder(id);
        }

        /**
         * Includes guild territorial control data, automatically filtered for this guild.
         *
         * <p>The conquest data is transformed to show only zones where this guild controls territory,
         * which reduces response size and improves usability.</p>
         *
         * @return This builder for method chaining
         */
        public Builder withConquest() {
            withGeneric(
                    Requester::getCurrentGuildConquest,
                    this.getOverview()::setConquest,
                    response -> filterConquestZones(id, response)
            );
            return this;
        }

        /**
         * Filters conquest zones to include only those controlled by this guild.
         *
         * @param guildId The guild ID to filter for
         * @param guildConquest The conquest data to filter
         */
        private static void filterConquestZones(int guildId, GuildConquest guildConquest) {
            for (Map.Entry<String, Zone> entry : guildConquest.getZones().entrySet()) {
                if (entry.getValue().getGuilds().stream().noneMatch(zone -> zone.getId() == guildId)) {
                    guildConquest.getZones().remove(entry.getKey());
                }
            }
        }

        /**
         * Builds the GuildOverview by fetching required and optional data.
         *
         * <p><b>Process:</b></p>
         * <ol>
         *   <li>Fetches guild information</li>
         *   <li>Fetches guild member roster</li>
         *   <li>Executes optional conquest request if queued</li>
         *   <li>Returns error immediately on first failure (no partial data)</li>
         *   <li>Returns success with complete overview</li>
         * </ol>
         *
         * @return Response containing the GuildOverview or error details
         */
        public Response<GuildOverview> build() {
            Response<GuildView> guild = Requester.getGuild(id);
            if (!guild.isSuccessful()) {
                return new Response<>(guild.getResponseCode(), null, guild.getError());
            }
            this.getOverview().setGuild(guild.getData());

            Response<GuildMembers> members = Requester.getGuildMembers(id);
            if (!members.isSuccessful()) {
                return new Response<>(members.getResponseCode(), null, members.getError());
            }
            this.getOverview().setMembers(members.getData());

            Response<?> tasks = this.processQueue();
            if (!tasks.isSuccessful()) {
                return new Response<>(tasks.getResponseCode(), null, tasks.getError());
            }

            return new Response<>(ResponseCode.SUCCESS, this.getOverview(), null);
        }
    }
}
