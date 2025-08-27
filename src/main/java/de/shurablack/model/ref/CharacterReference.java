package de.shurablack.model.ref;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.shurablack.util.Nullable;
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
    @JsonProperty("class")
    private String classType;

    /**
     * The URL of the character's image.
     * This field is nullable.
     */
    @Nullable
    private String imageUrl;

    /**
     * The URL of the character's background image.
     * This field is nullable.
     */
    @Nullable
    private String backgroundUrl;

    /**
     * The total level of the character.
     */
    private int totalLevel;

    /**
     * The timestamp indicating when the character was created.
     */
    private LocalDateTime createdAt;

}