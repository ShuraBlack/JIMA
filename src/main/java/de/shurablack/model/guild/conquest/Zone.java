package de.shurablack.model.guild.conquest;

import lombok.*;

import java.awt.*;
import java.util.List;

/**
 * Represents a zone in the guild conquest system.
 * This class contains details about the zone, including its location, contributions, status, color,
 * kills, experience, number of guilds, active assaults, and guild rankings.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Zone {

    /**
     * The location associated with the zone.
     */
    private Location location;

    /**
     * The list of contributions made in the zone.
     */
    private List<Contribution> contributions;

    /**
     * The current status of the zone.
     */
    private String status;

    /**
     * The color representing the zone.
     */
    private Color colour;

    /**
     * The total number of kills in the zone.
     */
    private long kills;

    /**
     * The total amount of experience gained in the zone.
     */
    private long experience;

    /**
     * The number of guilds present in the zone.
     */
    private int guildsCount;

    /**
     * The list of active assaults in the zone.
     */
    private List<Assault> activeAssaults;

    /**
     * The list of guild rankings in the zone.
     */
    private List<GuildConquestRanking> guilds;

}