package de.shurablack.model.character.view;

import lombok.*;

/**
 * Represents a guild in the system.
 * This class contains details about a guild, including its ID, tag, experience, level, and position.
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
     * The tag or abbreviation representing the guild.
     */
    private String tag;

    /**
     * The total experience points accumulated by the guild.
     */
    private long experience;

    /**
     * The level of the guild.
     */
    private int level;

    /**
     * The position or rank of the guild.
     */
    private String position;

}