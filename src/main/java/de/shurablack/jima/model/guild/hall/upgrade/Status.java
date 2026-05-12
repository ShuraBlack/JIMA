package de.shurablack.jima.model.guild.hall.upgrade;

import lombok.*;

/**
 * Upgrade status representation with both machine and human-readable formats.
 *
 * @see Upgrade For upgrade details including status
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Status {

    /** Machine-readable status key  */
    private String key;

    /** Human-readable status description for display */
    private String readable;

}
