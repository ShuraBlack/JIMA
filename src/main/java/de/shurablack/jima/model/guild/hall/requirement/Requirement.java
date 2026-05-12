package de.shurablack.jima.model.guild.hall.requirement;

import de.shurablack.jima.model.ref.ItemReference;
import lombok.*;

/**
 * Material requirement for blueprint construction.
 *
 * <p>Specifies items and quantities needed to build or upgrade
 * a guild hall upgrade.
 * </p>
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Requirement {

    /** The required item */
    private ItemReference item;

    /** Required quantity vs. available quantity */
    private Quantity quantity;

}
