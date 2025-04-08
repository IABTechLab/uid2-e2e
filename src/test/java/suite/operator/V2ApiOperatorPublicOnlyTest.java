package suite.operator;

import common.HttpClient;
import app.component.Operator;
import com.fasterxml.jackson.databind.JsonNode;
import com.uid2.client.IdentityTokens;
import com.uid2.client.TokenRefreshResponse;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unused")
public class V2ApiOperatorPublicOnlyTest {
    private static final String EMAIL_OPTOUT_ID = "optout@unifiedid.com";
    private static final String PHONE_OPTOUT_ID = "+00000000001";

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
