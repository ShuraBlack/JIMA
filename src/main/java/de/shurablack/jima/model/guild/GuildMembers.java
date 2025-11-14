package de.shurablack.jima.model.guild;

import de.shurablack.jima.model.EndpointUpdate;
import de.shurablack.jima.model.ref.GuildReference;
import lombok.*;

import java.util.List;

/**
 * Represents the members of a guild, including the guild reference and a list of its members.
 * Extends {@link EndpointUpdate} to include update-related metadata.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class GuildMembers extends EndpointUpdate {

    /**
     * Reference to the guild this object belongs to.
     */
    private GuildReference guild;

    /**
     * List of members in the guild.
     */
    private List<Member> members;

}