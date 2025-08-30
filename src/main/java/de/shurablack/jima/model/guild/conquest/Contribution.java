package de.shurablack.jima.model.guild.conquest;

import lombok.*;

/**
 * Represents a contribution in the guild conquest system.
 * This class contains details about the contribution, including the contributor, kills, experience gained,
 * and the associated guild conquest progress.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Contribution {

    /**
     * The unique identifier for the contribution.
     */
    private int id;

    /**
     * The identifier for the associated guild conquest progress.
     */
    private int guildConquestProgressId;

    /**
     * The contributor (character) associated with the contribution.
     */
    private Contributor character;

    /**
     * The number of kills achieved in the contribution.
     */
    private long kills;

    /**
     * The amount of experience gained in the contribution.
     */
    private long experience;

}