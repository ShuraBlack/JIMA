package de.shurablack.jima.http.serialization;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.shurablack.jima.model.auth.Authentication;
import de.shurablack.jima.model.ref.CharacterReference;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class AuthenticationDeserializer extends JsonDeserializer<Authentication> {
    @Override
    public Authentication deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JacksonException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode root = mapper.readTree(p);

        boolean authenticated = root.path("authenticated").asBoolean();

        Map<String, Integer> user = mapper.convertValue(
                root.path("user"),
                mapper.getTypeFactory().constructMapType(Map.class, String.class, Integer.class)
        );

        CharacterReference character = mapper.convertValue(
                root.path("character"),
                CharacterReference.class
        );

        JsonNode apiKey = root.path("api_key");

        String name = null;
        int rateLimit = 0;
        LocalDateTime expiresAt = null;
        List<String> scopes = null;

        if (!apiKey.isMissingNode() && !apiKey.isNull()) {
            name = apiKey.path("name").isNull() ? null : apiKey.path("name").asText();
            if (apiKey.has("rate_limit")) rateLimit = apiKey.path("rate_limit").asInt();

            if (apiKey.hasNonNull("expires_at")) {
                String ts = apiKey.get("expires_at").asText();
                expiresAt = LocalDateTime.parse(ts);
            }

            if (apiKey.hasNonNull("scopes")) {
                scopes = mapper.convertValue(
                        apiKey.get("scopes"),
                        mapper.getTypeFactory().constructCollectionType(List.class, String.class)
                );
            }
        }

        return new Authentication(authenticated, user, character, name, rateLimit, expiresAt, scopes);
    }
}
