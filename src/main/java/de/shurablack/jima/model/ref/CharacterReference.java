package de.shurablack.jima.model.ref;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.shurablack.jima.util.Nullable;
import de.shurablack.jima.util.types.ClassType;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents a reference to a character in the system.
 * This class contains details about the character, including its ID, hashed ID, name, class type,
 * image and background URLs, total level, and creation timestamp.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class CharacterReference {

    /**
     * The unique identifier for the character.
     */
    private int id;

    /**
     * The hashed identifier for the character.
     */
    private String hashedId;

    /**
     * The name of the character.
     */
    private String name;

    /**
     * The class type of the character.
     * This field is serialized as "class" in JSON.
     */
    @JsonProperty(value = "class", required = false)
    private ClassType classType;

    /**
     * The URL of the character's image.
     * This field is nullable.
     */
    @Nullable
    @JsonProperty(required = false)
    private String imageUrl;

    /**
     * The URL of the character's background image.
     * This field is nullable.
     */
    @Nullable
    @JsonProperty(required = false)
    private String backgroundUrl;

    /**
     * The total level of the character.
     */
    @JsonProperty(required = false)
    private int totalLevel;

    /**
     * The timestamp indicating when the character was created.
     */
    @JsonProperty(required = false)
    private LocalDateTime createdAt;

}