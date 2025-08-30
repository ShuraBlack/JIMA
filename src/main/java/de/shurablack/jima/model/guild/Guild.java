package de.shurablack.jima.model.guild;

import de.shurablack.jima.util.Nullable;
import lombok.*;

/**
 * Represents a guild in the system.
 * This class contains details about the guild, including its ID, name, tag, description, experience,
 * level, icon URL, background URL, member count, season position, and marks.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Guild {

    /**
     * The unique identifier for the guild.
     */
    private int id;

    /**
     * The name of the guild.
     */
    private String name;

    /**
     * The tag associated with the guild.
     */
    private String tag;

    /**
     * The description of the guild.
     * This field is nullable.
     */
    @Nullable
    private String description;

    /**
     * The total amount of experience gained by the guild.
     */
    private long experience;

    /**
     * The level of the guild.
     */
    private int level;

    /**
     * The URL of the guild's icon.
     * This field is nullable.
     */
    @Nullable
    private String iconUrl;

    /**
     * The URL of the guild's background image.
     * This field is nullable.
     */
    @Nullable
    private String backgroundUrl;

    /**
     * The number of members in the guild.
     */
    private int memberCount;

    /**
     * The position of the guild in the current season.
     * This field is nullable.
     */
    @Nullable
    private Integer seasonPosition;

    /**
     * The number of marks associated with the guild.
     */
    private int marks;

}