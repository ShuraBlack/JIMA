package de.shurablack.http.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

/**
 * Custom deserializer for converting JSON strings into {@link LocalDateTime} objects.
 * This deserializer parses an ISO-8601 formatted date-time string with an offset
 * and converts it to a {@link LocalDateTime} in the system's default time zone.
 */
public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    /**
     * Deserializes a JSON string into a {@link LocalDateTime} object.
     *
     * @param p     the {@link JsonParser} used to read the JSON content
     * @param ctxt  the {@link DeserializationContext} that can be used to access
     *              contextual information about the deserialization process
     * @return a {@link LocalDateTime} object representing the parsed date-time
     * @throws IOException if an error occurs while reading or parsing the JSON content
     */
    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        // Extract the text value from the JSON parser
        String value = p.getText();
        // Parse the text as an OffsetDateTime
        OffsetDateTime odt = OffsetDateTime.parse(value);
        // Convert the OffsetDateTime to a LocalDateTime in the system's default time zone
        return odt.atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    }

}