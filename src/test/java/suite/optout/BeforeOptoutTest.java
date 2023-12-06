package suite.optout;

import app.common.Mapper;
import app.component.Operator;
import com.fasterxml.jackson.databind.JsonNode;
import com.uid2.client.IdentityTokens;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("unused")
public class BeforeOptoutTest {
    // TODO: Test failure case

    private static final int OPTOUT_DELAY_MS = 1000;
    private static List<String> outputs;

    @BeforeAll
    public static void setupAll() {
        outputs = new ArrayList<>();
    }

    @AfterAll
    public static void teardownAll() {
        System.out.println("OUTPUTS - Copy and paste this into AfterLogoutTest");
        System.out.println("==================================================");
        outputs.forEach(System.out::print);
    }

    @ParameterizedTest(name = "/v2/token/logout with /v0/token/generate - {0} - {2}")
    @MethodSource({
            "suite.optout.TestData#tokenEmailArgs"
    })
    public void testV2LogoutWithV0TokenGenerate(String label, Operator operator, String operatorName, String type, String identity) throws Exception {
        JsonNode generateResponse = operator.v0TokenGenerate(type, identity);
        Thread.sleep(OPTOUT_DELAY_MS);
        JsonNode logoutResponse = operator.v2TokenLogout(type, identity);

        assertThat(logoutResponse).isEqualTo(Mapper.OBJECT_MAPPER.readTree("{\"body\":{\"optout\":\"OK\"},\"status\":\"success\"}"));
        addToken(
                label,
                operatorName,
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
    public void testV2LogoutWithV1TokenGenerate(String label, Operator operator, String operatorName, String type, String identity) throws Exception {
        JsonNode generateResponse = operator.v1TokenGenerate(type, identity);
        Thread.sleep(OPTOUT_DELAY_MS);
        JsonNode logoutResponse = operator.v2TokenLogout(type, identity);

        assertThat(logoutResponse).isEqualTo(Mapper.OBJECT_MAPPER.readTree("{\"body\":{\"optout\":\"OK\"},\"status\":\"success\"}"));
        addToken(
                label,
                operatorName,
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
    public void testV2LogoutWithV2TokenGenerate(String label, Operator operator, String operatorName, String type, String identity) throws Exception {
        IdentityTokens generateResponse = operator.v2TokenGenerate(type, identity, false).getIdentity();
        Thread.sleep(OPTOUT_DELAY_MS);
        JsonNode logoutResponse = operator.v2TokenLogout(type, identity);
        assertThat(logoutResponse).isEqualTo(Mapper.OBJECT_MAPPER.readTree("{\"body\":{\"optout\":\"OK\"},\"status\":\"success\"}"));
        addToken(
                label,
                operatorName,
                "v2",
                "v2",
                generateResponse.getRefreshToken(),
                Mapper.OBJECT_MAPPER.readTree(generateResponse.getJsonString()).at("/refresh_response_key").asText()
        );
    }

    @ParameterizedTest(name = "/v2/token/logout with /v2/token/generate - {0} - {2}")
    @MethodSource({
            "suite.optout.TestData#optoutTokenEmailArgsOldParticipant",
            "suite.optout.TestData#optoutTokenPhoneArgsOldParticipant"
    })
    public void testV2LogoutWithV2TokenGenerateOldParticipant(String label, Operator operator, String operatorName, String type, String identity) throws Exception {
        IdentityTokens generateResponse = operator.v2TokenGenerate(type, identity, true).getIdentity();
        Thread.sleep(OPTOUT_DELAY_MS);
        JsonNode logoutResponse = operator.v2TokenLogout(type, identity);
        assertThat(logoutResponse).isEqualTo(Mapper.OBJECT_MAPPER.readTree("{\"body\":{\"optout\":\"OK\"},\"status\":\"success\"}"));
        addToken(
                "old participant " + label,
                operatorName,
                "v2",
                "v2",
                generateResponse.getRefreshToken(),
                Mapper.OBJECT_MAPPER.readTree(generateResponse.getJsonString()).at("/refresh_response_key").asText()
        );
    }

    private void addToken(String label, String operatorName, String tokenGenerateVersion, String tokenLogoutVersion, String refreshToken, String refreshResponseKey) {
        outputs.add("List.of(\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\"),%n".formatted(
                        label, operatorName, tokenGenerateVersion, tokenLogoutVersion, refreshToken, refreshResponseKey
                )
        );
    }
}
