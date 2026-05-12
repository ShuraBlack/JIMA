package de.shurablack.jima.http.serialization;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import de.shurablack.jima.util.types.WeatherType;

import java.io.IOException;

/**
 * Jackson JSON deserializer for converting string weather values to {@link WeatherType} enums.
 *
 * <p><b>Purpose:</b></p>
 * This deserializer enables automatic conversion of weather type strings from API responses
 * into the corresponding WeatherType enum constants. It handles flexible input formats
 * (spaces, hyphens, mixed case) through {@link WeatherType#fromString(String)}.
 *
 * <p><b>How It Works:</b></p>
 * <ol>
 *   <li>Extracts the weather string from JSON</li>
 *   <li>Delegates parsing to {@link WeatherType#fromString(String)}</li>
 *   <li>Returns appropriate enum constant (or UNKNOWN if no match)</li>
 * </ol>
 *
 * <p><b>Integration:</b></p>
 * Used automatically by Jackson when deserializing Weather objects that contain weather type fields.
 * The deserializer ensures robust parsing regardless of API response format variations.
 *
 * @see WeatherType Enum with flexible parsing logic
 * @see de.shurablack.jima.model.world.Weather Model class containing weather type fields
 * @see de.shurablack.jima.http.HttpClient For Jackson configuration
 */
public class WeatherTypeDeserializer extends JsonDeserializer<WeatherType> {

    /**
     * Deserializes a JSON string value into a WeatherType enum.
     *
     * <p><b>Parsing Process:</b></p>
     * Extracts the weather string from the JSON parser and uses {@link WeatherType#fromString(String)}
     * to perform flexible parsing. Handles multiple formats:
     * <ul>
     *   <li>Exact match: "CLEAR" → CLEAR</li>
     *   <li>Lowercase: "clear" → CLEAR</li>
     *   <li>Spaces: "magic storm" → MAGIC_STORM</li>
     *   <li>Hyphens: "magic-storm" → MAGIC_STORM</li>
     *   <li>Unknown: "invalid" → UNKNOWN</li>
     * </ul>
     *
     * @param p     The {@link JsonParser} positioned at the weather value
     * @param ctxt  The {@link DeserializationContext} for deserialization (unused)
     * @return The parsed WeatherType enum constant, or UNKNOWN if parsing fails
     * @throws IOException        If a JSON parsing error occurs
     * @throws JacksonException   If a Jackson-specific error occurs
     *
     * @see WeatherType#fromString(String) For flexible parsing logic
     */
    @Override
    public WeatherType deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JacksonException {
        String value = p.getText();

        return WeatherType.fromString(value);
    }

}
