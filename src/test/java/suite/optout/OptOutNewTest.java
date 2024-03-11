package suite.optout;

import app.common.Mapper;
import app.component.Operator;
import com.fasterxml.jackson.databind.JsonNode;
import com.uid2.client.IdentityTokens;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

@SuppressWarnings("unused")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OptOutNewTest {
    // TODO: Test failure case

    private static final int OPTOUT_DELAY_MS = 1000;
    private static final int OPTOUT_WAIT_SECONDS = 600;

    private static Set<Arguments> outputArgs;

    @BeforeAll
    public static void setupAll() {
        outputArgs = new HashSet<>();
    }

    @ParameterizedTest(name = "/v2/token/logout with /v0/token/generate - {0} - {2}")
    @MethodSource({
            "suite.optout.TestData#tokenEmailArgs"
    })
    @Order(1)
    public void testV2LogoutWithV0TokenGenerate(String label, Operator operator, String operatorName, String type, String identity) throws Exception {
        JsonNode generateResponse = operator.v0TokenGenerate(type, identity);
        Thread.sleep(OPTOUT_DELAY_MS);
        JsonNode logoutResponse = operator.v2TokenLogout(type, identity);

        assertThat(logoutResponse).isEqualTo(Mapper.OBJECT_MAPPER.readTree("{\"body\":{\"optout\":\"OK\"},\"status\":\"success\"}"));
        addToken(
                label,
                operator,
                "v0",
                "v2",
                generateResponse.at("/refresh_token").asText(),
                ""
        );
    }

    @ParameterizedTest(name = "/v2/token/logout with /v1/token/generate - {0} - {2}")
    @MethodSource({
            "suite.optout.TestData#tokenEmailArgs",
            "suite.optout.TestData#tokenPhoneArgs"
    })
    @Order(2)
    public void testV2LogoutWithV1TokenGenerate(String label, Operator operator, String operatorName, String type, String identity) throws Exception {
        JsonNode generateResponse = operator.v1TokenGenerate(type, identity);
        Thread.sleep(OPTOUT_DELAY_MS);
        JsonNode logoutResponse = operator.v2TokenLogout(type, identity);

        assertThat(logoutResponse).isEqualTo(Mapper.OBJECT_MAPPER.readTree("{\"body\":{\"optout\":\"OK\"},\"status\":\"success\"}"));
        addToken(
                label,
                operator,
                "v1",
                "v2",
                generateResponse.at("/body/refresh_token").asText(),
                ""
        );
    }

    @ParameterizedTest(name = "/v2/token/logout with /v2/token/generate - {0} - {2}")
    @MethodSource({
            "suite.optout.TestData#optoutTokenEmailArgs",
            "suite.optout.TestData#optoutTokenPhoneArgs"
    })
    @Order(3)
    public void testV2LogoutWithV2TokenGenerate(String label, Operator operator, String operatorName, String type, String identity) throws Exception {
        IdentityTokens generateResponse = operator.v2TokenGenerate(type, identity, false).getIdentity();
        Thread.sleep(OPTOUT_DELAY_MS);
        JsonNode logoutResponse = operator.v2TokenLogout(type, identity);
        assertThat(logoutResponse).isEqualTo(Mapper.OBJECT_MAPPER.readTree("{\"body\":{\"optout\":\"OK\"},\"status\":\"success\"}"));
        addToken(
                label,
                operator,
                "v2",
                "v2",
                generateResponse.getRefreshToken(),
                Mapper.OBJECT_MAPPER.readTree(generateResponse.getJsonString()).at("/refresh_response_key").asText()
        );
    }

    @ParameterizedTest(name = "/v2/token/logout with /v2/token/generate - {0} - {2}")
    @MethodSource({
            "suite.optout.TestData#optoutTokenEmailArgs",
            "suite.optout.TestData#optoutTokenPhoneArgs"
    })
    @Order(4)
    public void testV2LogoutWithV2TokenGenerateOldParticipant(String label, Operator operator, String operatorName, String type, String identity) throws Exception {
        IdentityTokens generateResponse = operator.v2TokenGenerate(type, identity, true).getIdentity();
        Thread.sleep(OPTOUT_DELAY_MS);
        JsonNode logoutResponse = operator.v2TokenLogout(type, identity);
        assertThat(logoutResponse).isEqualTo(Mapper.OBJECT_MAPPER.readTree("{\"body\":{\"optout\":\"OK\"},\"status\":\"success\"}"));
        addToken(
                "old participant " + label,
                operator,
                "v2",
                "v2",
                generateResponse.getRefreshToken(),
                Mapper.OBJECT_MAPPER.readTree(generateResponse.getJsonString()).at("/refresh_response_key").asText()
        );
    }

    @Order(5)
    @ParameterizedTest(name = "/v0/token/refresh after {3} generate and {4} logout - {0} - {2}")
    @MethodSource({
            "newRefreshTokenArgs"
    })
    public void testV0TokenRefreshAfterOptOut(String label, Operator operator, String tokenGenerateVersion, String tokenLogoutVersion, String refreshToken, String refreshResponseKey) throws Exception {
        assumeThat(tokenGenerateVersion).isEqualTo("v0");

        JsonNode response = this.waitForOptOutResponse(operator::v0CheckedTokenRefresh, refreshToken, "{\"advertisement_token\":\"\",\"advertising_token\":\"\",\"refresh_token\":\"\"}");

        assertThat(response).isEqualTo(Mapper.OBJECT_MAPPER.readTree("{\"advertisement_token\":\"\",\"advertising_token\":\"\",\"refresh_token\":\"\"}"));
    }

    @Order(6)
    @ParameterizedTest(name = "/v1/token/refresh after {3} generate and {4} logout - {0} - {2}")
    @MethodSource({
            "newRefreshTokenArgs"
    })
    public void testV1TokenRefreshAfterOptOut(String label, Operator operator, String tokenGenerateVersion, String tokenLogoutVersion, String refreshToken, String refreshResponseKey) throws Exception {
        assumeThat(tokenGenerateVersion).isEqualTo("v1");

        JsonNode response = this.waitForOptOutResponse(operator::v1CheckedTokenRefresh, refreshToken, "{\"status\":\"optout\"}");

        assertThat(response).isEqualTo(Mapper.OBJECT_MAPPER.readTree("{\"status\":\"optout\"}"));
    }

    @Order(7)
    @ParameterizedTest(name = "/v2/token/refresh after {3} generate and {4} logout - {0} - {2}")
    @MethodSource({
            "newRefreshTokenArgs"
    })
    public void testV2TokenRefreshAfterOptOut(String label, Operator operator, String tokenGenerateVersion, String tokenLogoutVersion, String refreshToken, String refreshResponseKey) throws Exception {
        assumeThat(tokenGenerateVersion).isEqualTo("v2");

        JsonNode response = operator.v2TokenRefresh(refreshToken, refreshResponseKey);
        int breakCounter = 0;
        while (breakCounter < OPTOUT_WAIT_SECONDS && !response.equals(Mapper.OBJECT_MAPPER.readTree("{\"status\":\"optout\"}"))) {
            TimeUnit.SECONDS.sleep(5);
            response = operator.v2TokenRefresh(refreshToken, refreshResponseKey);
            breakCounter++;
        }

        assertThat(breakCounter).isLessThan(OPTOUT_WAIT_SECONDS).withFailMessage("Timed-out getting an Opt Out Response");
        assertThat(response).isEqualTo(Mapper.OBJECT_MAPPER.readTree("{\"status\":\"optout\"}"));
    }

    private static Set<Arguments> newRefreshTokenArgs() {
        return outputArgs;
    }

    private void addToken(String label, Operator operator, String tokenGenerateVersion, String tokenLogoutVersion, String refreshToken, String refreshResponseKey) {
        outputArgs.add(Arguments.of(label, operator, tokenGenerateVersion, tokenLogoutVersion, refreshToken, refreshResponseKey));
    }

    private JsonNode waitForOptOutResponse(Function<String, JsonNode> tokenRefreshFunction, String refreshToken, String expectedResponse) {
        try {
            JsonNode response = tokenRefreshFunction.apply(refreshToken);
            int breakCounter = 0;
            while (breakCounter < OPTOUT_WAIT_SECONDS && !response.equals(Mapper.OBJECT_MAPPER.readTree(expectedResponse))) {
                TimeUnit.SECONDS.sleep(5);
                response = tokenRefreshFunction.apply(refreshToken);
                breakCounter++;
            }
            return response;
        } catch (Exception e) {
            return Mapper.OBJECT_MAPPER.createObjectNode();
        }
    }
}
