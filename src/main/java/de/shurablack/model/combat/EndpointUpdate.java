package de.shurablack.model.combat;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents the update information for an endpoint in the system.
 * This class contains details about the time when the endpoint data will be updated.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class EndpointUpdate {

    /**
     * The date and time when the endpoint will update.
     */
    private LocalDateTime endpointUpdatesAt;

}