package de.shurablack.jima.model.shared;

import lombok.*;

/**
 * Generic key-value pair representing an enum/type constant with a display label.
 *
 * <p><b>Purpose:</b> Combines a typed key (often an enum) with a human-readable label for UI display.
 * Commonly used for mapping system enums to localized or formatted strings.</p>
 *
 * <p><b>Generic Type Parameter:</b></p>
 * <ul>
 *   <li>{@code T} - The type of the key, typically an enum like {@link de.shurablack.jima.util.types.SecondaryStatType}</li>
 * </ul>
 *
 * <p><b>Example:</b> A stat bonus for attack power might be represented as:</p>
 * <pre>
 * Label&lt;SecondaryStatType&gt; attackBonus = new Label&lt;&gt;(
 *     SecondaryStatType.ATTACK,
 *     "Attack Power"
 * );
 * </pre>
 *
 * @param <T> The type of the key value
 */
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
