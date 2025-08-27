package de.shurablack.model.character.view;

import lombok.*;

/**
 * Represents a stat in the system.
 * This class contains details about a stat, including its level and experience points.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Stat {

    /**
     * The level of the stat.
     */
    private int level;

    /**
     * The experience points of the stat.
     */
    private long experience;

}