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

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unused")
public class OperatorTest {
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
    private static final int RAW_UID_SIZE = 44;

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
            "suite.operator.TestData#identityMapBatchBadEmailArgs",
            "suite.operator.TestData#identityMapBatchBadPhoneArgs"
    })
    public void testV2IdentityMapUnmapped(String label, Operator operator, String operatorName, String payload) throws Exception {
        System.out.println("========================================");
        System.out.println("TEST: testV2IdentityMapUnmapped");
        System.out.println("Label: " + label);
        System.out.println("Operator: " + operatorName);
        System.out.println("Payload: " + payload);
        
        JsonNode response = operator.v2IdentityMap(payload);
        
        System.out.println("Response: " + response.toString());
        System.out.println("Status: " + response.at("/status").asText());
        System.out.println("Unmapped reason: " + response.at("/body/unmapped/0/reason").asText());
        System.out.println("========================================");

        assertThat(response.at("/status").asText()).isEqualTo("success");
        assertThat(response.at("/body/unmapped/0/reason").asText()).isEqualTo("invalid identifier");
    }

    @ParameterizedTest(name = "/v2/identity/map - {0} - {2}")
    @MethodSource({
            "suite.operator.TestData#identityMapBatchEmailArgs",
            "suite.operator.TestData#identityMapBatchPhoneArgs",
    })
    public void testV2IdentityMapMapped(String label, Operator operator, String operatorName, String payload) throws Exception {
        JsonNode response = operator.v2IdentityMap(payload);

        // TODO: Assert the value
        assertThat(response.at("/status").asText()).isEqualTo("success");
    }

    @ParameterizedTest(name = "/v2/identity/map - {0} - {2}")
    @MethodSource({"suite.operator.TestData#identityMapArgs"})
    public void testV2IdentityMap(
            String label,
            Operator operator,
            String operatorName,
            IdentityMapInput input,
            List<String> diis
    ) {
        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> { // Validate we didn't make mapping too slow.
            var response = operator.v2IdentityMap(input);

            assertThat(response.isSuccess()).isTrue();

            assertThat(response.getUnmappedIdentities()).isEmpty();

            var allMappedDiis = response.getMappedIdentities();
            assertThat(allMappedDiis.size()).isEqualTo(10_000);

            for (var dii : diis) {
                var mappedDii = allMappedDiis.get(dii);
                assertThat(mappedDii).isNotNull();
                assertThat(mappedDii.getRawUid().length()).isEqualTo(RAW_UID_SIZE);
                assertThat(mappedDii.getBucketId()).isNotBlank();
            }
        });
    }

    @ParameterizedTest(name = "/v3/identity/map - {0} - {2}")
    @MethodSource({"suite.operator.TestData#identityMapV3Args"})
    public void testV3IdentityMapLargeBatch(
            String label,
            Operator operator,
            String operatorName,
            IdentityMapV3Input input,
            List<String> diis
    ) {
        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> { // Validate we didn't make mapping too slow.
            var response = operator.v3IdentityMap(input);

            assertThat(response.isSuccess()).isTrue();

            assertThat(response.getUnmappedIdentities()).isEmpty();

            var allMappedDiis = response.getMappedIdentities();
            assertThat(allMappedDiis.size()).isEqualTo(10_000);

            for (var dii : diis) {
                var mappedDii = allMappedDiis.get(dii);
                assertThat(mappedDii).isNotNull();

                // Current UID should always be there and should have correct length
                assertThat(mappedDii.getCurrentRawUid().length()).isEqualTo(RAW_UID_SIZE);

                // Previous UID is there for 90 days after rotation only, then it's null.
                // If it's there, it should have the correct size
                assertThat(mappedDii.getPreviousRawUid()).satisfiesAnyOf(
                        uid -> assertThat(uid).isNull(),
                        uid -> assertThat(uid).hasSize(RAW_UID_SIZE)
                );

                // Sanity check that refresh from is a date not too far in the past.
                // If it is, either there is an Operator issue or salt rotation hasn't been running for a long time.
                assertThat(mappedDii.getRefreshFrom()).isAfter(Instant.now().minus(Duration.ofHours(1)));
            }
        });
    }

    @ParameterizedTest(name = "/v3/identity/map - {0} - {2}")
    @MethodSource({
            "suite.operator.TestData#identityMapV3BatchBadEmailArgs",
            "suite.operator.TestData#identityMapV3BatchBadPhoneArgs"
    })
    public void testV3IdentityMapUnmapped(String label, Operator operator, String operatorName, String payload, String identityType) throws Exception {
        JsonNode response = operator.v3IdentityMap(payload);

        assertThat(response.at("/status").asText()).isEqualTo("success");
        assertThat(response.at("/body/" + identityType + "/0/e").asText()).isEqualTo("invalid identifier");
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
