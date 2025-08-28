package de.shurablack.http.serialization;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import de.shurablack.util.types.SecondaryStatType;

import java.io.IOException;

/**
 * Custom deserializer for the {@link SecondaryStatType} enum.
 * This deserializer processes JSON data to convert it into a {@link SecondaryStatType} object.
 */
public class SecondaryStatTypeDeserializer extends JsonDeserializer<SecondaryStatType> {

    /**
     * Deserializes JSON data into a {@link SecondaryStatType} object.
     * The JSON value is expected to be a string that matches one of the enum constants.
     *
     * @param p     The {@link JsonParser} used to parse the JSON content.
     * @param ctxt  The {@link DeserializationContext} for the deserialization process.
     * @return A {@link SecondaryStatType} object corresponding to the JSON value.
     * @throws IOException        If an I/O error occurs during deserialization.
     * @throws JacksonException   If a JSON parsing error occurs.
     */
    @Override
    public SecondaryStatType deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JacksonException {
        // Extract the text value from the JSON parser.
        String value = p.getText();
        // Convert the text value to a SecondaryStatType enum constant.
        return SecondaryStatType.fromString(value);
    }

}