package suite.operator;

import app.common.EnvUtil;
import app.common.Mapper;
import app.component.Operator;
import com.fasterxml.jackson.databind.JsonNode;
import com.uid2.client.*;
import helper.EnabledCondition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unused")
public class V2ApiOperatorTest {
    /*
    TODO:
        /v2/token/generate - Add failure case
        /v2/token/refresh - Add failure case
        /v2/identity/map - Add failure case
        /v2/identity/buckets - Add failure case/more cases
        /v2/key/sharing - Add failure case/more cases
     */
    // The advertiser token will be different on every call due to randomness used in encryption,
    // so we can't assert on it

    private static final String SITE_ID = EnvUtil.getEnv("UID2_E2E_SITE_ID");

    @ParameterizedTest(name = "/v2/token/generate - {0} - {2}")
    @MethodSource({
            "suite.operator.TestData#tokenEmailArgs",
            "suite.operator.TestData#tokenPhoneArgs"
    })
    public void testV2TokenGenerate(String label, Operator operator, String operatorName, String type, String identity) throws Exception {
        TokenGenerateResponse tokenGenerateResponse = operator.v2TokenGenerate(type, identity, false);
        IdentityTokens currentIdentity = tokenGenerateResponse.getIdentity();
        DecryptionResponse decrypted = operator.v2TokenDecrypt(currentIdentity.getAdvertisingToken());

        assertThat(decrypted.getUid().length()).isEqualTo(TestData.RAW_UID2_LENGTH);
    }

    @ParameterizedTest(name = "/v2/token/generate - OPTOUT EMAIL - {0} - {2}")
    @MethodSource({
            "suite.operator.TestData#tokenEmailArgsSpecialOptout",
            "suite.operator.TestData#tokenPhoneArgsSpecialOptout"
    })
    public void testV2TokenGenerateSpecialOptout(String label, Operator operator, String operatorName, String type, String identity, boolean asOldParticipant) {
        TokenGenerateResponse tokenGenerateResponse = operator.v2TokenGenerate(type, identity, false);

        assertTrue(tokenGenerateResponse.isOptout());
    }

    @ParameterizedTest(name = "/v2/token/refresh - {0} - {2}")
    @MethodSource({
            "suite.operator.TestData#tokenEmailArgs",
            "suite.operator.TestData#tokenPhoneArgs"
    })
    public void testV2TokenRefresh(String label, Operator operator, String operatorName, String type, String identity) throws Exception {
        IdentityTokens currentIdentity = operator.v2TokenGenerate(type, identity, false).getIdentity();
        TokenRefreshResponse refreshed = operator.v2TokenRefresh(currentIdentity);
        DecryptionResponse decrypted = operator.v2TokenDecrypt(refreshed.getIdentity().getAdvertisingToken());

        assertThat(decrypted.getUid().length()).isEqualTo(TestData.RAW_UID2_LENGTH);
    }

    @ParameterizedTest(name = "/v2/token/refresh - OPTOUT EMAIL - {0} - {2}")
    @MethodSource({
            "suite.operator.TestData#tokenEmailArgsSpecialRefreshOptout",
            "suite.operator.TestData#tokenPhoneArgsSpecialRefreshOptout"
    })
    public void testV2SpecialRefreshOptout(String label, Operator operator, String operatorName, String type, String identity) {
        IdentityTokens currentIdentity = operator.v2TokenGenerate(type, identity, false).getIdentity();
        TokenRefreshResponse refreshed = operator.v2TokenRefresh(currentIdentity);

        assertTrue(refreshed.isOptout());
    }

    @EnabledIf("helper.EnabledCondition#isLocal")
    @ParameterizedTest(name = "/v2/token/generate - LOCAL MOCK OPTOUT - {0} - {2}")
    @MethodSource({
            "suite.operator.TestData#tokenEmailArgsLocalMockOptout"
    })
    public void testV2TokenGenerateLocalMockOptout(String label, Operator operator, String operatorName, String type, String identity) {
        TokenGenerateResponse tokenGenerateResponse = operator.v2TokenGenerate(type, identity, false);
        IdentityTokens currentIdentity = tokenGenerateResponse.getIdentity();

        assertThat(currentIdentity).isNull();
    }

    @ParameterizedTest(name = "/v2/token/validate - {0} - {2}")
    @MethodSource({
            "suite.operator.TestData#tokenValidateEmailArgs",
            "suite.operator.TestData#tokenValidatePhoneArgs"
    })
    public void testV2TokenValidate(String label, Operator operator, String operatorName, String type, String identity) throws Exception {
        IdentityTokens currentIdentity = operator.v2TokenGenerate(type, identity, false).getIdentity();
        String advertisingToken = currentIdentity.getAdvertisingToken();
        JsonNode response = operator.v2TokenValidate(type, identity, advertisingToken);

        assertThat(response).isEqualTo(Mapper.OBJECT_MAPPER.readTree("{\"body\":true,\"status\":\"success\"}"));
    }

    @ParameterizedTest(name = "/v2/identity/map - {0} - {2}")
    @MethodSource({
            "suite.operator.TestData#identityMapBatchEmailArgs",
            "suite.operator.TestData#identityMapBatchPhoneArgs",
            "suite.operator.TestData#identityMapBatchBadEmailArgs",
            "suite.operator.TestData#identityMapBatchBadPhoneArgs"
    })
    public void testV2IdentityMap(String label, Operator operator, String operatorName, String payload) throws Exception {
        JsonNode response = operator.v2IdentityMap(payload, false);

        // TODO: Assert the value
        assertThat(response.at("/status").asText()).isEqualTo("success");
    }

    @ParameterizedTest(name = "/v2/identity/map - VALIDATE EMAIL - {0} - {2}")
    @MethodSource({
            "suite.operator.TestData#tokenValidateEmailArgs",
            "suite.operator.TestData#tokenValidatePhoneArgs"
    })
    public void testV2IdentityMapValidateArgs(String label, Operator operator, String operatorName, String type, String identity) throws Exception {
        String payload = "{\"" + type + "\": [\"" + identity + "\"], \"optout_check\":1}";
        JsonNode response = operator.v2IdentityMap(payload, false);

        assertThat(response.get("body").get("mapped").get(0).get("advertising_id").asText()).isNotNull();
    }

    @ParameterizedTest(name = "/v2/identity/buckets - {0} - {2}")
    @MethodSource({
            "suite.operator.TestData#identityBucketsArgs"
    })
    public void testV2IdentityBuckets(String label, Operator operator, String operatorName, String payload) throws Exception {
        JsonNode response = operator.v2IdentityBuckets(payload);

        // TODO: Assert the value
        assertThat(response.at("/status").asText()).isEqualTo("success");
    }

    @ParameterizedTest(name = "/v2/key/sharing - {0} - {2}")
    @MethodSource({
            "suite.operator.TestData#baseArgs"
    })
    public void testV2KeySharing(Operator operator, String operatorName) throws Exception {
        JsonNode response = operator.v2KeySharing();

        assertEquals(SITE_ID, response.at("/body/caller_site_id").asText());
    }
}
