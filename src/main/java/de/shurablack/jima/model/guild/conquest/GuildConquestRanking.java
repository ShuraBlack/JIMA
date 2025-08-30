package de.shurablack.jima.model.guild.conquest;

import de.shurablack.jima.model.ref.GuildReference;
import lombok.*;

import java.util.List;

/**
 * Represents the ranking of a guild in the guild conquest system.
 * This class contains details about the guild's ranking, including its position, kills, experience,
 * contributions, and a reference to the guild.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class GuildConquestRanking {

    /**
     * The unique identifier for the guild conquest ranking.
     */
    private int id;

    /**
     * The position of the guild in the ranking.
     */
    private int position;

    /**
     * The total number of kills achieved by the guild.
     */
    private long kills;

    /**
     * The total amount of experience gained by the guild.
     */
    private long experience;

    /**
     * The list of contributions made by members of the guild.
     */
    private List<Contribution> contributions;

    /**
     * The reference to the guild associated with this ranking.
     */
    private GuildReference guild;

}