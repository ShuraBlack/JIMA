package de.shurablack.jima.model.guild.hall;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.shurablack.jima.model.guild.hall.blueprint.Blueprint;
import de.shurablack.jima.util.Nullable;
import lombok.*;

/**
 * Repair status for active upgrades needing maintenance.
 *
 * <p>Tracks the condition of an upgrade and whether it can be repaired.
 * The condition percentage is parsed from JSON as a percentage string
 * (e.g., "75%") and converted to an integer.</p>
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Repair {

    /** Upgrade condition as percentage (0-100 or null if not applicable) */
    @Nullable
    @JsonDeserialize(using = ConditionDeserializer.class)
    private Integer conditionPercentage;

    /** Whether this upgrade can currently be repaired */
    private boolean canRepair;

    /** The blueprint being repaired */
    private Blueprint blueprint;

    /**
     * Custom deserializer for condition percentage.
     * Removes "%" suffix from JSON percentage strings and converts to integer.
     * <p>
     * Example: "75%" → 75
     * </p>
     */
    public static class ConditionDeserializer extends JsonDeserializer<Integer> {
        @Override
        public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws java.io.IOException {
            String value = p.getText();
            return Integer.parseInt(value.replace("%", ""));
        }
    }
}
