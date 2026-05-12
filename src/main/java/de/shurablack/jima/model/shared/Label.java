package de.shurablack.jima.model.shared;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Label<T> {

    private T key;

    private String label;

}
