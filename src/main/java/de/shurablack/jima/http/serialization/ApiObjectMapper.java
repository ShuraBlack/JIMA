package de.shurablack.jima.http.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.shurablack.jima.model.auth.Authentication;
import de.shurablack.jima.model.item.recipe.RecipeExperience;
import de.shurablack.jima.util.types.SecondaryStatType;
import de.shurablack.jima.util.types.SkillType;
import de.shurablack.jima.util.types.StatType;

import java.awt.*;
import java.time.LocalDateTime;

/**
 * Custom implementation of the {@link ObjectMapper} for the API.
 * This class configures the object mapper with specific property naming strategies
 * and registers custom deserializers for various types.
 */
public class ApiObjectMapper extends ObjectMapper {

    /**
     * Constructs a new {@code ApiObjectMapper} instance.
     * Configures the property naming strategy to use snake_case and registers custom modules.
     */
    public ApiObjectMapper() {
        // Set the property naming strategy to snake_case for JSON serialization/deserialization.
        this.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        // Register the custom module containing deserializers.
        this.registerModule(getPreparedModule());
    }

    /**
     * Prepares and returns a {@link SimpleModule} with custom deserializers.
     * The module includes deserializers for basic types (e.g., {@link LocalDateTime}, {@link Color}),
     * complex types (e.g., {@link RecipeExperience}), and enums (e.g., {@link SkillType}, {@link StatType}, {@link SecondaryStatType}).
     *
     * @return A {@link SimpleModule} instance with registered deserializers.
     */
    public static SimpleModule getPreparedModule() {
        // Create a new SimpleModule instance.
        SimpleModule module = new SimpleModule();

        // Register deserializers for basic types.
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        module.addDeserializer(Color.class, new HexColorDeserializer());

        // Register deserializers for complex types.
        module.addDeserializer(RecipeExperience.class, new RecipeExperienceDeserializer());
        module.addDeserializer(Authentication.class, new AuthenticationDeserializer());

        // Register deserializers for enum types.
        module.addDeserializer(SkillType.class, new SkillTypeDeserializer());
        module.addDeserializer(StatType.class, new StatTypeDeserializer());
        module.addDeserializer(SecondaryStatType.class, new SecondaryStatTypeDeserializer());

        // Return the configured module.
        return module;
    }

}