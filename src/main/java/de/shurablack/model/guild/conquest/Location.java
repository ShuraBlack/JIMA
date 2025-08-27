package de.shurablack.model.guild.conquest;

import de.shurablack.util.Nullable;
import lombok.*;

/**
 * Represents a location in the guild conquest system.
 * This class contains details about the location, including its ID, key, name, and an optional image URL.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Location {

    /**
     * The unique identifier for the location.
     */
    private int id;

    /**
     * The key associated with the location.
     */
    private String key;

    /**
     * The name of the location.
     */
    private String name;

    /**
     * The URL of the location's image.
     * This field is nullable.
     */
    @Nullable
    private String imageUrl;

}