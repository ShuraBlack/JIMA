package de.shurablack.jima.http.serialization;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import de.shurablack.jima.util.types.WeatherType;

import java.io.IOException;

/**
 * Custom deserializer for the {@link WeatherType} enum.
 * This deserializer processes JSON data to convert it into a {@link WeatherType} object.
 */
public class WeatherTypeDeserializer extends JsonDeserializer<WeatherType> {

    /**
     * Deserializes JSON data into a {@link WeatherType} object.
     * The JSON value is expected to be a string that matches one of the enum constants.
     *
     * @param p     The {@link JsonParser} used to parse the JSON content.
     * @param ctxt  The {@link DeserializationContext} for the deserialization process.
     * @return A {@link WeatherType} object corresponding to the JSON value.
     * @throws IOException        If an I/O error occurs during deserialization.
     * @throws JacksonException   If a JSON parsing error occurs.
     */
    @Override
    public WeatherType deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JacksonException {
        String value = p.getText();

        return WeatherType.fromString(value);
    }

}
