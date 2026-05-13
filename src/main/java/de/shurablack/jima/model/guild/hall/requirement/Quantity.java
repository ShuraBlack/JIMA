package de.shurablack.jima.model.guild.hall.requirement;

import de.shurablack.jima.util.Nullable;
import lombok.*;

/**
 * Resource quantity tracker for upgrade requirements.
 *
 * <p>Compares required vs. available quantities for materials needed
 * to construct or upgrade guild hall features.
 * </p>
 *
 * @see Requirement For requirements containing quantities
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Quantity {

    /** Amount of resource currently available */
    @Nullable
    private Integer current;

    /** Amount of resource needed to complete construction */
    @Nullable
    private Integer needed;
}
