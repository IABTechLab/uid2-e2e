package suite.operator;

import common.HttpClient;
import common.Mapper;
import app.component.Operator;
import com.fasterxml.jackson.databind.JsonNode;
import com.uid2.client.DecryptionResponse;
import com.uid2.client.IdentityTokens;
import com.uid2.client.TokenRefreshResponse;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unused")
public class V2ApiOperatorPublicOnlyTest {
    private static final String EMAIL_OPTOUT_ID = "optout@unifiedid.com";
    private static final String PHONE_OPTOUT_ID = "+00000000001";

    @ParameterizedTest(name = "/v2/token/generate - OPTOUT BAD POLICY - {0} - {2}")
    @MethodSource({
            "suite.operator.TestData#tokenGenerateEmailArgsBadPolicy",
            "suite.operator.TestData#tokenGeneratePhoneArgsBadPolicy"
    }) // TODO: Can be removed after optout policy phase 3
    public void testV2TokenGenerateBadPolicy(String label, Operator operator, String operatorName, String payload) {
        if (isPrivateOperator(operator)) {
            return;
        }

        HttpClient.HttpException e = assertThrows(HttpClient.HttpException.class, () -> {
            JsonNode response = operator.v2TokenGenerateUsingPayload(payload, false);
        });

        assertAll(
                "/v2/token/generate - BAD OPTOUT POLICY - %s - %s".formatted(label, operatorName),
                () -> assertThat(e.getCode()).isEqualTo(400),
                () -> assertThat(e.getResponseJson().get("status").asText()).isEqualTo("client_error"),
                () -> assertThat(e.getResponseJson().get("message").asText()).isEqualTo("Required opt-out policy argument for token/generate is missing or not set to 1")
        );
    }

    @ParameterizedTest(name = "/v2/token/generate - OPTOUT BAD POLICY, OLD PARTICIPANT - {0} - {2}")
    @MethodSource({
            "suite.operator.TestData#tokenGenerateEmailArgsBadPolicy",
            "suite.operator.TestData#tokenGeneratePhoneArgsBadPolicy"
    }) // TODO: Can be removed after optout policy phase 3
    public void testV2TokenGenerateBadPolicyOldParticipant(String label, Operator operator, String operatorName, String payload) throws Exception {
        if (isPrivateOperator(operator)) {
            return;
        }

        JsonNode response = operator.v2TokenGenerateUsingPayload(payload, true);

        assertEquals("success", response.get("status").asText());
    }

    @ParameterizedTest(name = "/v2/token/generate - OPTOUT EMAIL, OLD PARTICIPANT - {0} - {2}")
    @MethodSource({
            "suite.operator.TestData#tokenEmailArgsSpecialOptout",
            "suite.operator.TestData#tokenPhoneArgsSpecialOptout"
    }) // TODO: Can be removed after optout policy phase 3
    public void testV2TokenGenerateSpecialOptoutOldParticipant(String label, Operator operator, String operatorName, String type, String identity, boolean asOldParticipant) throws Exception {
        if (isPrivateOperator(operator)) {
            return;
        }

        String payload = "{\"" + type + "\": \"" + identity + "\"}";
        JsonNode response = operator.v2TokenGenerateUsingPayload(payload, true);
        DecryptionResponse decrypted = operator.v2TokenDecrypt(response.get("body").get("advertising_token").asText());

        String identityMapPayload = "email".equals(type)
                ? "{\"email\": [\"" + EMAIL_OPTOUT_ID + "\"]}"
                : "{\"phone\": [\"" + PHONE_OPTOUT_ID + "\"]}";
        JsonNode identityMapResponse = operator.v2IdentityMap(identityMapPayload, true);
        String expectedUid = identityMapResponse.get("body").get("mapped").get(0).get("advertising_id").asText();

        String optoutCheckPayload = "{\"" + type + "\": \"" + identity + "\", \"optout_check\":1}";
        JsonNode optoutCheckResponse = operator.v2TokenGenerateUsingPayload(optoutCheckPayload, true);

        assertAll(
                "/v2/token/generate - OPTOUT EMAIL, OLD PARTICIPANT",
                () -> assertEquals("success", response.get("status").asText()),
                () -> assertEquals(expectedUid, decrypted.getUid()),
                () -> assertEquals("optout", optoutCheckResponse.get("status").asText())
        );
    }

    @ParameterizedTest(name = "/v2/token/refresh - OPTOUT EMAIL, OLD PARTICIPANT - {0} - {2}")
    @MethodSource({
            "suite.operator.TestData#tokenEmailArgsSpecialRefreshOptout",
            "suite.operator.TestData#tokenPhoneArgsSpecialRefreshOptout"
    })
    public void testV2SpecialRefreshOptoutOldParticipant(String label, Operator operator, String operatorName, String type, String identity) {
        if (isPrivateOperator(operator)) {
            return;
        }

        IdentityTokens currentIdentity = operator.v2TokenGenerate(type, identity, true).getIdentity();
        TokenRefreshResponse refreshed = operator.v2TokenRefresh(currentIdentity);

        assertTrue(refreshed.isOptout());
    }

    @ParameterizedTest(name = "/v2/token/validate - OLD PARTICIPANT - {0} - {2}")
    @MethodSource({
            "suite.operator.TestData#tokenValidateEmailArgs",
            "suite.operator.TestData#tokenValidatePhoneArgs"
    }) // TODO: Can be removed after optout policy phase 3
    public void testV2TokenValidateOldParticipant(String label, Operator operator, String operatorName, String type, String identity) throws Exception {
        if (isPrivateOperator(operator)) {
            return;
        }

        IdentityTokens currentIdentity = operator.v2TokenGenerate(type, identity, true).getIdentity();
        String advertisingToken = currentIdentity.getAdvertisingToken();
        JsonNode response = operator.v2TokenValidate(type, identity, advertisingToken);

        assertThat(response).isEqualTo(Mapper.OBJECT_MAPPER.readTree("{\"body\":true,\"status\":\"success\"}"));
    }

    @ParameterizedTest(name = "/v2/identity/map - OPTOUT EMAIL, TRUE OPTOUT PARAM - {0} - {2} - Old Participant: {5}")
    @MethodSource({
            "suite.operator.TestData#tokenEmailArgsSpecialOptout",
            "suite.operator.TestData#tokenPhoneArgsSpecialOptout"
    })
    public void testV2IdentityMapSpecialOptoutParamTrue(String label, Operator operator, String operatorName, String type, String identity, boolean asOldParticipant) throws Exception {
        if (isPrivateOperator(operator)) {
            return;
        }

        String payload = "{\"" + type + "\": [\"" + identity + "\"], \"optout_check\":1}";
        JsonNode response = operator.v2IdentityMap(payload, asOldParticipant);

        assertThat(response.get("body").get("unmapped").get(0).get("reason").asText()).isEqualTo("optout");
    }

    @ParameterizedTest(name = "/v2/identity/map - OPTOUT EMAIL, FALSE OPTOUT PARAM - {0} - {2} - Old Participant: {5}")
    @MethodSource({
            "suite.operator.TestData#tokenEmailArgsSpecialOptout",
            "suite.operator.TestData#tokenPhoneArgsSpecialOptout"
    })
    public void testV2IdentityMapSpecialOptoutParamFalse(String label, Operator operator, String operatorName, String type, String identity, boolean asOldParticipant) throws Exception {
        if (isPrivateOperator(operator)) {
            return;
        }

        String payload = "{\"" + type + "\": [\"" + identity + "\"], \"optout_check\":0}";
        JsonNode response = operator.v2IdentityMap(payload, asOldParticipant);

        assertThat(response.get("body").get("unmapped").get(0).get("reason").asText()).isEqualTo("optout");
    }

    @ParameterizedTest(name = "/v2/identity/map - OPTOUT EMAIL, NO OPTOUT PARAM - {0} - {2} - Old Participant: {5}")
    @MethodSource({
            "suite.operator.TestData#tokenEmailArgsSpecialOptout",
            "suite.operator.TestData#tokenPhoneArgsSpecialOptout"
    })
    public void testV2IdentityMapSpecialOptoutNoParam(String label, Operator operator, String operatorName, String type, String identity, boolean asOldParticipant) throws Exception {
        if (isPrivateOperator(operator)) {
            return;
        }

        String payload = "{\"" + type + "\": [\"" + identity + "\"]}";
        JsonNode response = operator.v2IdentityMap(payload, asOldParticipant);

        assertThat(response.get("body").get("unmapped").get(0).get("reason").asText()).isEqualTo("optout");
    }

    @ParameterizedTest(name = "/v2/identity/map - VALIDATE EMAIL, OLD PARTICIPANT - {0} - {2}")
    @MethodSource({
            "suite.operator.TestData#tokenValidateEmailArgs",
            "suite.operator.TestData#tokenValidatePhoneArgs"
    }) // TODO: Can be removed after optout policy phase 3
    public void testV2IdentityMapValidateArgsOldParticipant(String label, Operator operator, String operatorName, String type, String identity) throws Exception {
        if (isPrivateOperator(operator)) {
            return;
        }

        String payload = "{\"" + type + "\": [\"" + identity + "\"], \"optout_check\":1}";
        JsonNode response = operator.v2IdentityMap(payload, true);

        assertThat(response.get("body").get("mapped").get(0).get("advertising_id").asText()).isNotNull();
    }

    @ParameterizedTest(name = "/v2/identity/map - BAD OPTOUT POLICY - {0} - {2} - Old Participant: {5}")
    @MethodSource({
            "suite.operator.TestData#identityMapBatchEmailArgsBadPolicy",
            "suite.operator.TestData#identityMapBatchPhoneArgsBadPolicy",
    }) // TODO: Can be removed after optout policy phase 3
    public void testV2IdentityMapBadPolicy(String label, Operator operator, String operatorName, String payload, boolean asOldParticipant) throws Exception {
        if (isPrivateOperator(operator)) {
            return;
        }

        JsonNode response = operator.v2IdentityMap(payload, asOldParticipant);

        // TODO: Assert the value
        assertThat(response.at("/status").asText()).isEqualTo("success");
    }

    @EnabledIf("common.EnabledCondition#isLocal")
    @ParameterizedTest(name = "/v2/token/client-generate - {0} - {2}")
    @MethodSource({
            "suite.operator.TestData#clientSideTokenGenerateArgs",
    })
    public void testV2ClientSideTokenGenerate(String label, Operator operator, String operatorName, String payload) throws Exception {
        if (isPrivateOperator(operator)) {
            return;
        }

        final JsonNode response = operator.v2ClientSideTokenGenerate(payload, true);

        assertThat(response.get("status").asText()).isEqualTo("success");
    }

    @EnabledIf("common.EnabledCondition#isLocal")
    @ParameterizedTest(name = "/v2/token/client-generate - INVALID ORIGIN - {0} - {2}")
    @MethodSource({
            "suite.operator.TestData#clientSideTokenGenerateArgs",
    })
    public void testV2ClientSideTokenGenerateInvalidOrigin(String label, Operator operator, String operatorName, String payload) {
        if (isPrivateOperator(operator)) {
            return;
        }

        HttpClient.HttpException e = assertThrows(HttpClient.HttpException.class, () -> {
            JsonNode response = operator.v2ClientSideTokenGenerate(payload, false);
        });

        assertAll(
                () -> assertThat(e.getCode()).isEqualTo(403),
                () -> assertThat(e.getResponseJson().get("status").asText()).isEqualTo("invalid_http_origin"),
                () -> assertThat(e.getResponseJson().get("message").asText()).isEqualTo("unexpected http origin")
        );
    }

    private boolean isPrivateOperator(Operator operator) {
        return operator.getType() == Operator.Type.PRIVATE;
    }
}
