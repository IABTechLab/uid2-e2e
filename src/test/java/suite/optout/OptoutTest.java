package suite.optout;

import app.component.Operator;
import app.component.Optout;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uid2.client.IdentityTokens;
import com.uid2.shared.util.Mapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.awaitility.Awaitility.with;

@SuppressWarnings("unused")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OptoutTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(OptoutTest.class);

    private static final ObjectMapper OBJECT_MAPPER = Mapper.getInstance();
    private static final int OPTOUT_DELAY_MS = 1000;
    private static final int OPTOUT_WAIT_SECONDS = 300;
    private static final int DELTA_PRODUCE_WAIT_SECONDS = 120;

    private static Set<Arguments> outputArgs;
    private static Set<Arguments> outputAdvertisingIdArgs;
    private static Optout optoutService;

    @BeforeAll
    public static void setupAll() {
        outputArgs = new HashSet<>();
        outputAdvertisingIdArgs = new HashSet<>();
        // Initialize optout service component for delta production
        optoutService = new Optout("optout", 8081, "Optout Service");
    }

    @ParameterizedTest(name = "/v2/token/logout with /v2/token/generate - {0} - {2}")
    @MethodSource({
            "suite.optout.TestData#optoutTokenEmailArgs",
            "suite.optout.TestData#optoutTokenPhoneArgs"
    })
    @Order(1)
    public void testV2LogoutWithV2TokenGenerate(String label, Operator operator, String operatorName, String type, String identity) throws Exception {
        IdentityTokens generateResponse = operator.v2TokenGenerate(type, identity).getIdentity();
        Thread.sleep(OPTOUT_DELAY_MS);
        JsonNode logoutResponse = operator.v2TokenLogout(type, identity);
        assertThat(logoutResponse).isEqualTo(OBJECT_MAPPER.readTree("{\"body\":{\"optout\":\"OK\"},\"status\":\"success\"}"));
        addToken(
                label,
                operator,
                "v2",
                "v2",
                generateResponse.getRefreshToken(),
                OBJECT_MAPPER.readTree(generateResponse.getJsonString()).at("/refresh_response_key").asText()
        );
    }

    @ParameterizedTest(name = "/v2/token/logout with /v2/identity/map - DII {0} - toOptOut {5} - {2}")
    @MethodSource({
            "suite.optout.TestData#identityMapEmailArgs",
            "suite.optout.TestData#identityMapPhoneArgs"
    })
    @Order(3)
    public void testV2LogoutWithV2IdentityMap(String label, Operator operator, String operatorName, String type, String emailOrPhone, boolean toOptOut) throws Exception {
        JsonNode identityMapResponseNode = operator.v2IdentityMap("{\""+ type + "\":[\"" + emailOrPhone + "\"]}");
        assertThat(identityMapResponseNode.at("/status").asText()).isEqualTo("success");
        String rawUID = identityMapResponseNode.get("body").get("mapped").get(0).get(TestData.ADVERTISING_ID).asText();
        long beforeOptOutTimestamp = Instant.now().toEpochMilli();
        if (toOptOut) {
            Thread.sleep(OPTOUT_DELAY_MS);
            JsonNode logoutResponse = operator.v2TokenLogout(type, emailOrPhone);
            assertThat(logoutResponse).isEqualTo(OBJECT_MAPPER.readTree("{\"body\":{\"optout\":\"OK\"},\"status\":\"success\"}"));
        }
        outputAdvertisingIdArgs.add(Arguments.of(label, operator, operatorName, rawUID, toOptOut, beforeOptOutTimestamp));
    }

    /**
     * Triggers delta production on the optout service after all logout requests.
     * This reads the opt-out requests from SQS and produces delta files that
     * the operator will sync to reflect the opt-outs.
     */
    @Test
    @Order(4)
    public void triggerDeltaProduction() throws Exception {
        LOGGER.info("Triggering delta production on optout service");
        
        // Trigger delta production and wait for completion
        // This handles 409 (job already running) gracefully
        boolean success = optoutService.triggerDeltaProduceAndWait(DELTA_PRODUCE_WAIT_SECONDS);
        
        // Get final status
        JsonNode status = optoutService.getDeltaProduceStatus();
        LOGGER.info("Delta production completed with status: {}", status);
        
        assertThat(success).as("Delta production should complete successfully").isTrue();
    }

    @Order(5)
    @ParameterizedTest(name = "/v2/token/refresh after {2} generate and {3} logout - {0} - {1}")
    @MethodSource({
            "afterOptoutTokenArgs"
    })
    public void testV2TokenRefreshAfterOptOut(String label, Operator operator, String tokenGenerateVersion, String tokenLogoutVersion, String refreshToken, String refreshResponseKey) throws Exception {
        assumeThat(tokenGenerateVersion).isEqualTo("v2");

        with().pollInterval(5, TimeUnit.SECONDS).await("Get V2 Token Response").atMost(OPTOUT_WAIT_SECONDS, TimeUnit.SECONDS).until(() -> operator.v2TokenRefresh(refreshToken, refreshResponseKey).equals(OBJECT_MAPPER.readTree("{\"status\":\"optout\"}")));
    }

    @Order(6)
    @ParameterizedTest(name = "/v2/optout/status after v2/identity/map and v2/token/logout - DII {0} - expecting {4} - {2}")
    @MethodSource({"afterOptoutAdvertisingIdArgs"})
    public void testV2OptOutStatus(String label, Operator operator, String operatorName, String rawUID,
                                   boolean isOptedOut, long optedOutTimestamp) throws Exception {
        String payload = "{\"advertising_ids\":[\"" + rawUID + "\"]}";
        with().pollInterval(5, TimeUnit.SECONDS)
                .await("Get Opt out status response")
                .atMost(OPTOUT_WAIT_SECONDS, TimeUnit.SECONDS)
                .until(() -> {
                    JsonNode response = operator.v2OptOutStatus(payload);
                    JsonNode body = response.get("body");
                    JsonNode optedOutJsonNode = body.get("opted_out");
                    if (isOptedOut) {
                        return optedOutJsonNode.size() == 1;

                    } else {
                        return optedOutJsonNode.isEmpty();
                    }
                });
        JsonNode response = operator.v2OptOutStatus(payload);
        assertThat(response.at("/status").asText()).isEqualTo("success");
        JsonNode body = response.get("body");
        assertThat(body).isNotNull();
        JsonNode optedOutJsonNode = body.get("opted_out");
        assertThat(optedOutJsonNode).isNotNull();
        if (isOptedOut) {
            assertThat(optedOutJsonNode.size()).isEqualTo(1);
            JsonNode optedOutRecord = optedOutJsonNode.get(0);
            assertThat(optedOutRecord).isNotNull();
            assertThat(optedOutRecord.has(TestData.ADVERTISING_ID)).isTrue();
            String advertisingId = optedOutRecord.get(TestData.ADVERTISING_ID).asText();
            assertThat(advertisingId).isEqualTo(rawUID);
            assertThat(optedOutRecord.has(TestData.OPTED_OUT_SINCE)).isTrue();
            long optedOutSinceMilliseconds = optedOutRecord.get(TestData.OPTED_OUT_SINCE).asLong();
            assertThat(optedOutSinceMilliseconds).isGreaterThanOrEqualTo(optedOutTimestamp);
        } else {
            assertThat(optedOutJsonNode.size()).isEqualTo(0);
        }
    }

    private static Set<Arguments> afterOptoutTokenArgs() {
        return outputArgs;
    }

    private static Set<Arguments> afterOptoutAdvertisingIdArgs() {
        return outputAdvertisingIdArgs;
    }

    private void addToken(String label, Operator operator, String tokenGenerateVersion, String tokenLogoutVersion, String refreshToken, String refreshResponseKey) {
        outputArgs.add(Arguments.of(label, operator, tokenGenerateVersion, tokenLogoutVersion, refreshToken, refreshResponseKey));
    }

    private void waitForOptOutResponse(Function<String, JsonNode> tokenRefreshFunction, String refreshToken, String expectedResponse) {
        with().pollInterval(5, TimeUnit.SECONDS).await("Get Token Response").atMost(OPTOUT_WAIT_SECONDS, TimeUnit.SECONDS).until(() -> {
            JsonNode response = tokenRefreshFunction.apply(refreshToken);
            return response.equals(OBJECT_MAPPER.readTree(expectedResponse));
        });
    }
}
