package de.shurablack.jima.model.ref;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.shurablack.jima.util.Nullable;
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
    @JsonProperty(required = false)
    private String tag;

    @Nullable
    @JsonProperty(required = false)
    private String iconUrl;

    @Nullable
    @JsonProperty(required = false)
    private String backgroundUrl;

    @JsonProperty(required = false)
    private int memberCount;

}
