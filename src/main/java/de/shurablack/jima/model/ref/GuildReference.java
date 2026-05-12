package de.shurablack.jima.model.ref;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.shurablack.jima.util.Nullable;
import lombok.*;

/**
 * Represents a reference to a guild in the system.
 * This class contains essential guild information used when referencing a guild in other entities.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class GuildReference {

    /**
     * The unique identifier of the guild.
     */
    private int id;

    /**
     * The name of the guild.
     */
    private String name;

    /**
     * The tag or abbreviation of the guild (nullable).
     * Used to represent the guild in a shortened form.
     */
    @Nullable
    @JsonProperty(required = false)
    private String tag;

    /**
     * The URL of the guild's icon image (nullable).
     */
    @Nullable
    @JsonProperty(required = false)
    private String iconUrl;

    /**
     * The URL of the guild's background image (nullable).
     */
    @Nullable
    @JsonProperty(required = false)
    private String backgroundUrl;

    /**
     * The number of members in the guild.
     */
    @Nullable
    @JsonProperty(required = false)
    private Integer memberCount;

}
