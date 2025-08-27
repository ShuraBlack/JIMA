package de.shurablack.model.character.pet;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents a pet battle.
 * This class contains details about the start and end times of the battle.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Battle {

    /**
     * The timestamp when the battle started.
     */
    private LocalDateTime startedAt;

    /**
     * The timestamp when the battle ends.
     */
    private LocalDateTime endsAt;

}