package de.shurablack.jima.model.guild.conquest;

import de.shurablack.jima.model.ref.GuildReference;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents an assault in the guild conquest system.
 * This class contains details about the assault, including the guild involved, kills, experience gained,
 * and the start and end timestamps of the assault.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Assault {

    /**
     * The reference to the guild involved in the assault.
     */
    private GuildReference guild;

    /**
     * The number of kills achieved during the assault.
     */
    private long kills;

    /**
     * The amount of experience gained during the assault.
     */
    private long experience;

    /**
     * The timestamp indicating when the assault starts.
     */
    private LocalDateTime startsAt;

    /**
     * The timestamp indicating when the assault ends.
     */
    private LocalDateTime endsAt;

}