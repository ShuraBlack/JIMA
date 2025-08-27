package de.shurablack.http.serialization;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.shurablack.model.item.ItemDetail;

import java.io.IOException;

public class ItemDetailDeserializer extends JsonDeserializer<ItemDetail> {
    @Override
    public ItemDetail deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JacksonException {
        JsonNode root = p.getCodec().readTree(p);

        JsonNode itemNode = root.get("item");
        if (itemNode == null) {
            return null;
        }

        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        return mapper.treeToValue(itemNode, ItemDetail.class);
    }
}
