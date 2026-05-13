package de.shurablack.jima.model.ref;

import de.shurablack.jima.util.Nullable;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class LocationReference {

    @Nullable
    private Integer id;

    @Nullable
    private String name;
}
