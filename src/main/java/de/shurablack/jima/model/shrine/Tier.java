package de.shurablack.jima.model.shrine;

import lombok.*;

/**
 * Represents a tier in the shrine system.
 * This class contains details about the tier, including its key and name.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Tier {

    /**
     * The unique key associated with the tier.
     */
    private String key;

    /**
     * The name of the tier.
     */
    private String name;

}