package de.shurablack.http.serialization;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import de.shurablack.model.item.recipe.RecipeExperience;
import de.shurablack.util.types.SkillType;
import de.shurablack.util.types.StatType;

import java.io.IOException;

/**
 * Custom deserializer for the {@link RecipeExperience} class.
 * This deserializer processes JSON data to populate a {@link RecipeExperience} object.
 */
public class RecipeExperienceDeserializer extends JsonDeserializer<RecipeExperience> {

    /**
     * Deserializes JSON data into a {@link RecipeExperience} object.
     * The JSON structure is expected to have "stats" and "skills" objects, each containing
     * a single key-value pair representing the type and value of the stat or skill.
     *
     * @param p     The {@link JsonParser} used to parse the JSON content.
     * @param ctxt  The {@link DeserializationContext} for the deserialization process.
     * @return A {@link RecipeExperience} object populated with the deserialized data.
     * @throws IOException        If an I/O error occurs during deserialization.
     * @throws JacksonException   If a JSON parsing error occurs.
     */
    @Override
    public RecipeExperience deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JacksonException {
        // Parse the root JSON node.
        JsonNode root = p.getCodec().readTree(p);
        RecipeExperience experience = new RecipeExperience();

        // Process the "stats" node if it exists.
        JsonNode statsNode = root.get("stats");
        if (statsNode != null && statsNode.isObject()) {
            String statKey = statsNode.fieldNames().next();
            experience.setStatType(StatType.fromString(statKey));
            experience.setStatValue(statsNode.get(statKey).asInt());
        }

        // Process the "skills" node if it exists.
        JsonNode skillsNode = root.get("skills");
        if (skillsNode != null && skillsNode.isObject()) {
            String skillKey = skillsNode.fieldNames().next();
            experience.setSkillType(SkillType.fromString(skillKey));
            experience.setSkillValue(skillsNode.get(skillKey).asInt());
        }

        // Return the populated RecipeExperience object.
        return experience;
    }

}