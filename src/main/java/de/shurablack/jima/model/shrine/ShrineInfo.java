package de.shurablack.jima.model.shrine;

import de.shurablack.jima.model.EndpointUpdate;
import lombok.*;

import java.util.List;

/**
 * Represents information about a shrine in the system.
 * This class extends `EndpointUpdate` and contains details about the progress of shrine stages.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class ShrineInfo extends EndpointUpdate {

    /**
     * The list of shrine stages representing the progress of the shrine.
     */
    private List<ShrineStage> progress;

}