package de.shurablack.jima.model.shared;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class TimeRange {

    private LocalDateTime startedAt;

    private LocalDateTime endsAt;

}
