package de.shurablack.jima.model.character.view;

import lombok.*;

/**
 * Represents a skill in the system.
 * This class contains details about a skill, including its level and experience points.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Skill {

    /**
     * The level of the skill.
     */
    private int level;

    /**
     * The experience points of the skill.
     */
    private long experience;

}