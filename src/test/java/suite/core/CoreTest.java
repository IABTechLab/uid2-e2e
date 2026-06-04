package suite.core;

import common.HttpClient;
import common.KmsHelper;
import app.component.Core;
import com.fasterxml.jackson.databind.JsonNode;
import com.uid2.shared.Const;
import com.uid2.shared.attest.JwtService;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

@EnabledIf("common.EnabledCondition#isLocal")
public class CoreTest {
    @ParameterizedTest(name = "/attest - {0}")
    @MethodSource({
            "suite.core.TestData#baseArgs"
    })
    public void testAttest_EmptyAttestationRequest(Core core) {
        HttpClient.HttpException exception = assertThrows(
                HttpClient.HttpException.class,
                () -> core.attest("")
        );

        String coreUrl = core.getBaseUrl();

        assertEquals("Unsuccessful POST request - URL: " + coreUrl + "/attest - Code: 400 Bad Request - Response body: {\"status\":\"no attestation_request attached\"}", exception.getMessage());
    }

    /**
     * Tests that an unknown / mistyped operator key is rejected with HTTP 401 and a body that
     * names the cause (reason=unrecognized_key) instead of a bare "Unauthorized".
     * This is the 4eyes.ai scenario (UID2-6717 / UID2-7235): a single transcription error in the
     * operator key. The actionable message propagates verbatim into the operator's startup log.
     */
    @ParameterizedTest(name = "/attest unrecognized key - {0}")
    @MethodSource({
            "suite.core.TestData#baseArgs"
    })
    public void testAttest_UnrecognizedOperatorKey(Core core) {
        // A well-formed but unknown key - not present in the operators store.
        String bogusOperatorKey = "UID2-O-L-000-thisKeyDoesNotExist000000000000000000000000=";

        HttpClient.HttpException exception = assertThrows(
                HttpClient.HttpException.class,
                () -> core.attestWithApiKey("{\"attestation_request\":\"AA==\"}", bogusOperatorKey)
        );

        assertEquals(401, exception.getCode(), "unknown operator key should be rejected with 401");
        JsonNode body = assertDoesNotThrow(exception::getResponseJson);
        assertAll("401 body should name the rejection cause",
                () -> assertEquals("unauthorized", body.get("status").asText()),
                () -> assertEquals("unrecognized_key", body.get("reason").asText()),
                () -> assertTrue(body.get("message").asText().toLowerCase().contains("not recognized"),
                        "message should tell the operator the key was not recognized"));
    }

    /**
     * Tests that a recognized-but-disabled operator key is rejected with HTTP 401 and reason=key_disabled,
     * distinguishing it from an unknown key. Relies on the disabled operator key seeded in
     * uid2-admin localstack (site_id 998, "Disabled Operator (E2E)").
     */
    @ParameterizedTest(name = "/attest disabled key - {0}")
    @MethodSource({
            "suite.core.TestData#baseArgs"
    })
    public void testAttest_DisabledOperatorKey(Core core) {
        String disabledOperatorKey = "UID2-O-L-998-d1sabledKeyForE2ETestOnly00000000000000000000=";

        HttpClient.HttpException exception = assertThrows(
                HttpClient.HttpException.class,
                () -> core.attestWithApiKey("{\"attestation_request\":\"AA==\"}", disabledOperatorKey)
        );

        assertEquals(401, exception.getCode(), "disabled operator key should be rejected with 401");
        JsonNode body = assertDoesNotThrow(exception::getResponseJson);
        assertAll("401 body should identify the key as disabled",
                () -> assertEquals("unauthorized", body.get("status").asText()),
                () -> assertEquals("key_disabled", body.get("reason").asText()),
                () -> assertTrue(body.get("message").asText().toLowerCase().contains("disabled"),
                        "message should tell the operator the key is disabled"));
    }

    /**
     * Tests valid attestation request with JWT signing.
     *
     * Since LocalStack generates its own RSA key material,
     * we dynamically fetch the public key from LocalStack's
     * KMS using GetPublicKey API to validate JWT signatures.
     */
    @ParameterizedTest(name = "/attest - {0}")
    @MethodSource({
            "suite.core.TestData#baseArgs"
    })
    public void testAttest_ValidAttestationRequest(Core core) throws Exception {
        final String validTrustedAttestationRequest = "{\"attestation_request\":\"AA==\",\"public_key\":\"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAl7kXK1wf15V9HgkQhbMK2nfJGbudmdXNrX7MZjFm07z6eEjaQsuqMteQumLRwn+RxEcXKVaxBAE3RQTFL9XsZc2OtRKOU+oMIQep8tmPFMgh83BzjLs5O5HZf510geFJO6qRqc3UHJT3ACxE7IkmRx1JIKFKPzTrthHdb2+D7bdJBPsVbwk7y+a36f5jvELGGzMC89LAvd7JpOmGsCAj6jwiEAKmmLId9bfe0YeLuebl95VfSzrQVdz82oGGQKXuJgKZtc/Xp1omZ9spm+zzVFJzsimxDdGdnaWMnas43VoTE04JDt+pucJTTbftIvu05frwkbZh3sQ2yBu5gBP7YwIDAQAB\",\"application_name\":\"uid2-operator\",\"application_version\":\"5.27.10-3f25586306\",\"components\":{\"uid2-attestation-api\":\"2.0.0-f968aec0e3\",\"uid2-shared\":\"7.2.4-SNAPSHOT\"}}";

        JsonNode response = core.attest(validTrustedAttestationRequest);

        assertAll("Attestation response should be successful",
                () -> assertNotNull(response.get("status")),
                () -> assertEquals("success", response.get("status").asText()));

        JsonNode body = response.get("body");
        assertAll("testAttest_ValidAttestationRequest - not-null body",
                () -> assertNotNull(body),
                () -> assertNotNull(body.get("attestation_token")),
                () -> assertNotNull(body.get("expiresAt")));

        // Verify JWTs are generated - LocalStack 4.x supports KMS Sign
        JsonNode jwtOptoutNode = body.get("attestation_jwt_optout");
        JsonNode jwtCoreNode = body.get("attestation_jwt_core");
        
        assertAll("JWTs should be generated by KMS Sign",
                () -> assertNotNull(jwtOptoutNode, "attestation_jwt_optout should not be null"),
                () -> assertFalse(jwtOptoutNode.isNull(), "attestation_jwt_optout should not be JSON null"),
                () -> assertFalse(jwtOptoutNode.asText().isEmpty(), "attestation_jwt_optout should not be empty"),
                () -> assertNotNull(jwtCoreNode, "attestation_jwt_core should not be null"),
                () -> assertFalse(jwtCoreNode.isNull(), "attestation_jwt_core should not be JSON null"),
                () -> assertFalse(jwtCoreNode.asText().isEmpty(), "attestation_jwt_core should not be empty"));

        // Verify JWT format (header.payload.signature)
        String jwtOptout = jwtOptoutNode.asText();
        String jwtCore = jwtCoreNode.asText();
        assertAll("JWTs should have valid format",
                () -> assertEquals(3, jwtOptout.split("\\.").length, "OptOut JWT should have 3 parts"),
                () -> assertEquals(3, jwtCore.split("\\.").length, "Core JWT should have 3 parts"));

        // Fetch the public key dynamically from LocalStack KMS and validate JWT signatures
        String publicKeyBase64 = KmsHelper.getPublicKeyFromLocalstack();
        JsonObject config = new JsonObject()
                .put(Const.Config.AwsKmsJwtSigningPublicKeysProp, publicKeyBase64);
        JwtService jwtService = new JwtService(config);
        
        // Validate optout JWT signature
        var optoutValidation = jwtService.validateJwt(jwtOptout, Core.OPTOUT_URL, Core.CORE_URL);
        assertTrue(optoutValidation.getIsValid(), "OptOut JWT signature should be valid");
        
        // Validate core JWT signature  
        var coreValidation = jwtService.validateJwt(jwtCore, Core.CORE_URL, Core.CORE_URL);
        assertTrue(coreValidation.getIsValid(), "Core JWT signature should be valid");

        String optoutUrl = body.get("optout_url").asText();
        assertAll("testAttest_ValidAttestationRequest OptOut URL not null",
                () -> assertNotNull(optoutUrl),
                () -> assertEquals(Core.OPTOUT_URL, optoutUrl));
    }

    @ParameterizedTest(name = "/operator/config - {0}")
    @MethodSource({
            "suite.core.TestData#baseArgs"
    })
    public void testOpertorConfig_ValidRequest(Core core) throws Exception {
        JsonNode response = core.getOperatorConfig();

        assertAll("testOpertorConfig_ValidRequest has valid response",
                () -> assertNotNull(response),
                () -> assertInstanceOf(Integer.class, response.get("version").asInt()),
                () -> {
                    JsonNode runtimeConfig = response.get("runtime_config");
                    assertNotNull(runtimeConfig, "runtime_config should not be null");
                }
        );
    }
}
