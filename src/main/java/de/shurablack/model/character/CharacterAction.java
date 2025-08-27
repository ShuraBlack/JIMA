package de.shurablack.model.character;

import de.shurablack.util.Nullable;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents an action performed by a character in the system.
 * This class contains details about the action, including its type, associated item, image URL, title,
 * and timestamps for when the action starts and expires.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class CharacterAction {

    /**
     * The type of the action.
     * This field is nullable.
     */
    @Nullable
    private String type;

    /**
     * The item associated with the action.
     * This field is nullable.
     */
    @Nullable
    private String item;

    /**
     * The URL of the image associated with the action.
     * This field is nullable.
     */
    @Nullable
    private String imageUrl;

    /**
     * The title of the action.
     * This field is nullable.
     */
    @Nullable
    private String title;

    /**
     * The timestamp indicating when the action expires.
     * This field is nullable.
     */
    @Nullable
    private LocalDateTime expiresAt;

    /**
     * The timestamp indicating when the action starts.
     * This field is nullable.
     */
    @Nullable
    private LocalDateTime startedAt;

}