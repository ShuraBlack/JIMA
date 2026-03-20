package de.shurablack.jima.http;

import com.fasterxml.jackson.databind.DeserializationFeature;
import de.shurablack.jima.model.auth.Authentication;
import de.shurablack.jima.model.combat.worldboss.WorldBosses;
import de.shurablack.jima.util.TokenStore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequesterTest {

    @BeforeAll
    static void setup() {
        // Object mapper error on not mapped fields
        RequestManager.getInstance().getMapper().configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true
        );
    }

    @Test
    void getAuthentication() {
        Response<Authentication> response = assertDoesNotThrow(() -> Requester.getAuthentication());

        assertTrue(response.isSuccessful());
    }

    @Test
    void getAuthenticationInsert() {
        Response<Authentication> response = assertDoesNotThrow(() -> Requester.getAuthentication(TokenStore.getInstance().getToken()));

        assertTrue(response.isSuccessful());
    }

    @Test
    void getWorldBosses() {
        Response<WorldBosses> response = assertDoesNotThrow(Requester::getWorldBosses);

        assertTrue(response.isSuccessful());
    }

    @Test
    void getDungeons() {
    }

    @Test
    void getEnemies() {
    }

    @Test
    void searchItems() {
    }

    @Test
    void testSearchItems() {
    }

    @Test
    void testSearchItems1() {
    }

    @Test
    void testSearchItems2() {
    }

    @Test
    void testSearchItems3() {
    }

    @Test
    void getAllItems() {
    }

    @Test
    void advancedSearchItems() {
    }

    @Test
    void testAdvancedSearchItems() {
    }

    @Test
    void inspectItem() {
    }

    @Test
    void getMarketHistory() {
    }

    @Test
    void testGetMarketHistory() {
    }

    @Test
    void getMarketListingHistory() {
    }

    @Test
    void getMarketOrderHistory() {
    }

    @Test
    void getCharacter() {
    }

    @Test
    void getCharacterMetrics() {
    }

    @Test
    void getCharacterEffects() {
    }

    @Test
    void getCharacterAlts() {
    }

    @Test
    void getCharacterMuseum() {
    }

    @Test
    void testGetCharacterMuseum() {
    }

    @Test
    void testGetCharacterMuseum1() {
    }

    @Test
    void testGetCharacterMuseum2() {
    }

    @Test
    void getCharacterAction() {
    }

    @Test
    void getCharacterPets() {
    }

    @Test
    void getCompanionExchangeListings() {
    }

    @Test
    void testGetCompanionExchangeListings() {
    }

    @Test
    void getGuild() {
    }

    @Test
    void getGuildMembers() {
    }

    @Test
    void getCurrentGuildConquest() {
    }

    @Test
    void getGuildConquestBySeason() {
    }

    @Test
    void getGuildConquestInspection() {
    }

    @Test
    void testGetGuildConquestInspection() {
    }

    @Test
    void getShrineInfo() {
    }
}