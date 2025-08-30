package de.shurablack.jima.http.serialization;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.awt.*;
import java.io.IOException;

public class HexColorDeserializer extends JsonDeserializer<Color> {

    @Override
    public Color deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JacksonException {
        String hex = p.getText();
        if (!hex.startsWith("#") && !hex.startsWith("0x")) {
            hex = "#" + hex;
        }

        return Color.decode(hex);
    }
}
