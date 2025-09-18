package de.shurablack.jima.http.serialization;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import de.shurablack.jima.model.item.GatherLocation;

import java.io.IOException;

public class GatherLocationDeserializer extends JsonDeserializer<GatherLocation> {
    @Override
    public GatherLocation deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JacksonException {
        // Check if its empty list and make it null
        if (p.getCurrentToken().isStructStart()) {
            p.skipChildren();
            return null;
        }
        return ctxt.readValue(p, GatherLocation.class);
    }
}
