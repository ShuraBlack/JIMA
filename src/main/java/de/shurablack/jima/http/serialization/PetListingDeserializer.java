package de.shurablack.jima.http.serialization;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import de.shurablack.jima.model.pet.PetListing;
import de.shurablack.jima.util.types.Quality;

import java.io.IOException;

/**
 * Custom deserializer for the {@link PetListing} class.
 * This class is responsible for converting JSON data into a {@link PetListing} object.
 */
public class PetListingDeserializer extends JsonDeserializer<PetListing> {

    /**
     * Deserializes JSON data into a {@link PetListing} object.
     *
     * @param p     The {@link JsonParser} used to parse the JSON content.
     * @param ctxt  The {@link DeserializationContext} that provides additional context for deserialization.
     * @return A {@link PetListing} object populated with data from the JSON.
     * @throws IOException        If an I/O error occurs during deserialization.
     * @throws JacksonException   If a parsing error occurs.
     */
    @Override
    public PetListing deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JacksonException {
        // Parse the root JSON node.
        JsonNode root = p.getCodec().readTree(p);

        // Extract the "pet" and "cost" nodes from the JSON.
        JsonNode petNode = root.get("pet");
        JsonNode costNode = root.get("cost");

        // Create a new PetListing object.
        PetListing listing = new PetListing();

        // Populate the PetListing object with data from the "pet" node.
        listing.setCharacterPetId(petNode.get("character_pet_id").asInt());
        listing.setPetId(petNode.get("pet_id").asInt());
        listing.setName(petNode.get("name").asText());
        listing.setQuality(Quality.valueOf(petNode.get("quality").asText()));
        listing.setLevel(petNode.get("level").asInt());
        listing.setImageUrl(petNode.get("image_url").asText());

        // Populate the PetListing object with data from the "cost" node.
        listing.setCurrency(costNode.get("currency").asText());
        listing.setAmount(costNode.get("amount").asInt());

        // Return the fully populated PetListing object.
        return listing;
    }
}