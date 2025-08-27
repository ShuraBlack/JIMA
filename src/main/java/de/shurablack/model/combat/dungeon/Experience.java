package de.shurablack.model.combat.dungeon;

import lombok.*;

import java.util.Map;

/**
 * Represents information about skills and the experience gained for each skill.
 * This class contains a mapping of skill names to their respective experience values.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Experience {

    /**
     * A map where the key is the name of the skill, and the value is the experience gained for that skill.
     */
    private Map<String, Integer> skills;

}