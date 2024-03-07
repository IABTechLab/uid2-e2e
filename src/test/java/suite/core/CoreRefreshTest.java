package suite.core;

import app.component.Core;
import com.fasterxml.jackson.databind.node.ArrayNode;
import helper.JsonAssert;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CoreRefreshTest {
    @ParameterizedTest(name = "Refresh test - UrlPath: {1} - JsonPath: {2}")
    @MethodSource({"suite.core.TestData#refreshArgs"})
    public void testLocationRefresh_Public_Success(Core core, String urlPath, String jsonPath) throws Exception {
        JsonNode response = core.getWithCoreApiToken(urlPath);

        assertAll("testLocationRefresh_Public_Success has version and location",
                () -> assertNotNull(response),
                () -> assertNotEquals("", response.at("/version").asText(), "Version was empty"),
                () -> assertNotEquals("", response.at("/" + jsonPath + "/location").asText(), "/" + jsonPath + "/location was empty"));
    }

    @ParameterizedTest(name = "Refresh test - UrlPath: {1} - CollectionName: {2}")
    @MethodSource({"suite.core.TestData#collectionEndpointArgs"})
    public void testCollectionRefresh_Public_Success(Core core, String urlPath, String collectionName) throws Exception {
        JsonNode response = core.getWithCoreApiToken(urlPath);

        assertAll("testCollectionRefresh_Public_Success has version and collection",
                () -> assertNotNull(response, "Response should not be null"),
                () -> assertNotEquals("", response.at("/version").asText(), "Version was empty"),
                () -> assertNotNull(response.at("/" + collectionName), "Collection should not be null")
        );

        ArrayNode nodes = (ArrayNode) response.at("/" + collectionName);
        for (JsonNode node : nodes) {
            assertTrue(JsonAssert.hasContentInFields(
                    node, List.of("/effective", "/expires", "/location", "/size")
            ), "Collection node was missing expected content");
        }
    }

    @ParameterizedTest(name = "Refresh test - UrlPath: {1} - JsonPath: {2}")
    @MethodSource({"suite.core.TestData#optOutRefreshArgs"})
    public void testOptOut_LocationRefresh_Public_Success(Core core, String urlPath, String jsonPath) throws Exception {
        JsonNode response = core.getWithOptOutApiToken(urlPath);

        assertAll("testOptOut_LocationRefresh_Public_Success has version and location",
                () -> assertNotNull(response),
                () -> assertNotEquals("", response.at("/version").asText(), "Version was empty"),
                () -> assertNotEquals("", response.at("/" + jsonPath + "/location").asText(), "/" + jsonPath + "/location was empty"));
    }
}