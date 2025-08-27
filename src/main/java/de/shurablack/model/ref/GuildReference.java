package de.shurablack.model.ref;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.shurablack.util.Nullable;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class GuildReference {

    private int id;
    private String name;
    @Nullable
    private String tag;
    @Nullable
    private String iconUrl;
    @Nullable
    @JsonProperty(required = false)
    private String backgroundUrl;

}
