package de.shurablack.model.character.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.shurablack.util.Nullable;
import de.shurablack.util.types.ClassType;
import de.shurablack.util.types.SkillType;
import de.shurablack.util.types.StatType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Represents a character in the system.
 * This class contains various attributes of a character, including its ID, name, class type, skills, stats, and more.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Character {

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
    private ClassType classType;

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
     * A map containing the character's skills, where the key is the skill name and the value is the skill details.
     */
    private Map<SkillType, Skill> skills;

    /**
     * A map containing the character's stats, where the key is the stat name and the value is the stat value.
     */
    private Map<StatType, Stat> stats;

    /**
     * The amount of gold the character possesses.
     */
    private long gold;

    /**
     * The amount of tokens the character possesses.
     */
    private long tokens;

    /**
     * The amount of shards the character possesses.
     */
    private long shards;

    /**
     * The total level of the character.
     */
    private int totalLevel;

    /**
     * The pet currently equipped by the character.
     * This field is nullable.
     */
    @Nullable
    private Pet equippedPet;

    /**
     * The guild the character belongs to.
     * This field is nullable.
     */
    @Nullable
    private Guild guild;

    /**
     * The timestamp of the character's last activity.
     * This field is nullable.
     */
    @Nullable
    private LocalDateTime lastActivity;

    /**
     * The timestamp when the character was created.
     */
    private LocalDateTime createdAt;

}