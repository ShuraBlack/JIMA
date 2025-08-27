package de.shurablack.model.guild.conquest;

import de.shurablack.util.Nullable;
import lombok.*;

/**
 * Represents a contributor in the guild conquest system.
 * This class contains details about the contributor, including their ID, hashed ID, name, total level,
 * and optional image and background URLs.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Contributor {

    /**
     * The unique identifier for the contributor.
     */
    private int id;

    /**
     * The hashed identifier for the contributor.
     */
    private String hashedId;

    /**
     * The name of the contributor.
     */
    private String name;

    /**
     * The total level of the contributor.
     */
    private int totalLevel;

    /**
     * The URL of the contributor's image.
     * This field is nullable.
     */
    @Nullable
    private String imageUrl;

    /**
     * The URL of the contributor's background image.
     * This field is nullable.
     */
    @Nullable
    private String backgroundUrl;

}