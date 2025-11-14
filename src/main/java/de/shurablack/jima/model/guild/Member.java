package de.shurablack.jima.model.guild;

import de.shurablack.jima.util.Nullable;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Member {

    /**
     * The name of the guild member.
     */
    private String name;

    /**
     * The position or role of the member within the guild.
     */
    private String position;

    /**
     * The URL of the member's avatar image. Can be null.
     */
    @Nullable
    private String avatarUrl;

    /**
     * The URL of the member's background image. Can be null.
     */
    @Nullable
    private String backgroundUrl;

    /**
     * The total level of the member, representing their overall progress or rank.
     */
    private int totalLevel;

}
