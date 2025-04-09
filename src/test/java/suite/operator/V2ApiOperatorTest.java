package suite.operator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uid2.shared.util.Mapper;
import common.Const;
import common.EnvUtil;
import app.component.Operator;
import com.fasterxml.jackson.databind.JsonNode;
import com.uid2.client.*;
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

    private static final ObjectMapper OBJECT_MAPPER = Mapper.getInstance();
    private static final String CLIENT_SITE_ID = EnvUtil.getEnv(Const.Config.Operator.CLIENT_SITE_ID);

    @ParameterizedTest(name = "/v2/token/generate - {0} - {2}")
    @MethodSource({
            "suite.operator.TestData#tokenEmailArgs",
            "suite.operator.TestData#tokenPhoneArgs"
    })
    public void testV2TokenGenerate(String label, Operator operator, String operatorName, String type, String identity) throws Exception {
        TokenGenerateResponse tokenGenerateResponse = operator.v2TokenGenerate(type, identity);
        IdentityTokens currentIdentity = tokenGenerateResponse.getIdentity();
        DecryptionResponse decrypted = operator.v2TokenDecrypt(currentIdentity.getAdvertisingToken());

        assertThat(decrypted.getUid().length()).isEqualTo(TestData.RAW_UID2_LENGTH);
    }

    @ParameterizedTest(name = "/v2/token/generate - OPTOUT EMAIL - {0} - {2}")
    @MethodSource({
            "suite.operator.TestData#tokenEmailArgsSpecialOptout",
            "suite.operator.TestData#tokenPhoneArgsSpecialOptout"
    })
    public void testV2TokenGenerateSpecialOptout(String label, Operator operator, String operatorName, String type, String identity) {
        TokenGenerateResponse tokenGenerateResponse = operator.v2TokenGenerate(type, identity);

        assertTrue(tokenGenerateResponse.isOptout());
    }

    @ParameterizedTest(name = "/v2/token/refresh - {0} - {2}")
    @MethodSource({
            "suite.operator.TestData#tokenEmailArgs",
            "suite.operator.TestData#tokenPhoneArgs"
    })
    public void testV2TokenRefresh(String label, Operator operator, String operatorName, String type, String identity) throws Exception {
        IdentityTokens currentIdentity = operator.v2TokenGenerate(type, identity).getIdentity();
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
        IdentityTokens currentIdentity = operator.v2TokenGenerate(type, identity).getIdentity();
        TokenRefreshResponse refreshed = operator.v2TokenRefresh(currentIdentity);

        assertTrue(refreshed.isOptout());
    }

    @ParameterizedTest(name = "/v2/token/validate - {0} - {2}")
    @MethodSource({
            "suite.operator.TestData#tokenValidateEmailArgs",
            "suite.operator.TestData#tokenValidatePhoneArgs"
    })
    public void testV2TokenValidate(String label, Operator operator, String operatorName, String type, String identity) throws Exception {
        IdentityTokens currentIdentity = operator.v2TokenGenerate(type, identity).getIdentity();
        String advertisingToken = currentIdentity.getAdvertisingToken();
        JsonNode response = operator.v2TokenValidate(type, identity, advertisingToken);

        assertThat(response).isEqualTo(OBJECT_MAPPER.readTree("{\"body\":true,\"status\":\"success\"}"));
    }

    @ParameterizedTest(name = "/v2/identity/map - {0} - {2}")
    @MethodSource({
            "suite.operator.TestData#identityMapBatchEmailArgs",
            "suite.operator.TestData#identityMapBatchPhoneArgs",
            "suite.operator.TestData#identityMapBatchBadEmailArgs",
            "suite.operator.TestData#identityMapBatchBadPhoneArgs"
    })
    public void testV2IdentityMap(String label, Operator operator, String operatorName, String payload) throws Exception {
        JsonNode response = operator.v2IdentityMap(payload);

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
        JsonNode response = operator.v2IdentityMap(payload);

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

        assertEquals(CLIENT_SITE_ID, response.at("/body/caller_site_id").asText());
    }
}
