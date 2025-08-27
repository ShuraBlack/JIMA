package de.shurablack.model.character.effect;

import de.shurablack.util.Nullable;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents an effect applied to a character.
 * This class contains details about the effect's source, target, attribute, value, and expiration.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Effect {

    /**
     * The unique identifier of the character to whom the effect is applied.
     */
    private int characterId;

    /**
     * The source of the effect (e.g., skill, item, or environment).
     */
    private String source;

    /**
     * The target of the effect (e.g., character or object).
     */
    private String target;

    /**
     * The attribute affected by the effect (e.g., health, strength, or defense).
     */
    private String attribute;

    /**
     * The value of the effect applied to the attribute.
     */
    private int value;

    /**
     * The type of the value (e.g., absolute or percentage).
     */
    private String valueType;

    /**
     * The location identifier where the effect is applied (nullable).
     */
    @Nullable
    private int locationId;

    /**
     * The expiration time of the effect (nullable).
     */
    @Nullable
    private LocalDateTime expireAt;

}