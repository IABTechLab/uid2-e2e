package suite.core;

import app.common.HttpClient;
import app.component.Core;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.webtoken.JsonWebSignature;
import com.uid2.shared.attest.JwtService;
import com.uid2.shared.attest.JwtValidationResponse;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

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

    @ParameterizedTest(name = "/attest - {0}")
    @MethodSource({
            "suite.core.TestData#baseArgs"
    })
    public void testAttest_ValidAttestationRequest(Core core) throws Exception {
        final String validTrustedAttestationRequest = "{\"attestation_request\":\"AA==\",\"public_key\":\"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAl7kXK1wf15V9HgkQhbMK2nfJGbudmdXNrX7MZjFm07z6eEjaQsuqMteQumLRwn+RxEcXKVaxBAE3RQTFL9XsZc2OtRKOU+oMIQep8tmPFMgh83BzjLs5O5HZf510geFJO6qRqc3UHJT3ACxE7IkmRx1JIKFKPzTrthHdb2+D7bdJBPsVbwk7y+a36f5jvELGGzMC89LAvd7JpOmGsCAj6jwiEAKmmLId9bfe0YeLuebl95VfSzrQVdz82oGGQKXuJgKZtc/Xp1omZ9spm+zzVFJzsimxDdGdnaWMnas43VoTE04JDt+pucJTTbftIvu05frwkbZh3sQ2yBu5gBP7YwIDAQAB\",\"application_name\":\"uid2-operator\",\"application_version\":\"5.27.10-3f25586306\",\"components\":{\"uid2-attestation-api\":\"2.0.0-f968aec0e3\",\"uid2-shared\":\"7.2.4-SNAPSHOT\"}}";

        JsonNode response = core.attest(validTrustedAttestationRequest);

        assertAll("",
                () -> assertNotNull(response.get("status")),
                () -> assertEquals("success", response.get("status").asText()));

        JsonNode body = response.get("body");
        assertAll("testAttest_ValidAttestationRequest - not-null body",
                () -> assertNotNull(body),
                () -> assertNotNull(body.get("attestation_token")),
                () -> assertNotNull(body.get("expiresAt")));

        JwtService jwtService = new JwtService(getConfig());
        assertNotNull(body.get("attestation_jwt_optout"));
        JwtValidationResponse validationResponseOptOut = jwtService.validateJwt(body.get("attestation_jwt_optout").asText(), Core.OPTOUT_URL, Core.CORE_URL);
        assertAll("testAttest_ValidAttestationRequest valid OptOut JWT. Local OptOut URL: '" + Core.OPTOUT_URL + "', Core URL: '" + Core.CORE_URL + "'",
                () -> assertNotNull(validationResponseOptOut),
                () -> assertTrue(validationResponseOptOut.getIsValid()));

        assertNotNull(body.get("attestation_jwt_core"));
        JwtValidationResponse validationResponseCore = jwtService.validateJwt(body.get("attestation_jwt_core").asText(), Core.CORE_URL, Core.CORE_URL);
        assertAll("testAttest_ValidAttestationRequest valid Core JWT. Local Core URL: '" + Core.CORE_URL + "'",
                () -> assertNotNull(validationResponseCore),
                () -> assertTrue(validationResponseCore.getIsValid()));

        String optoutUrl = body.get("optout_url").asText();
        assertAll("testAttest_ValidAttestationRequest OptOut URL not null",
                () -> assertNotNull(optoutUrl),
                () -> assertEquals(Core.OPTOUT_URL, optoutUrl));
    }

    private static JsonObject getConfig() {
        return new JsonObject("{  \"aws_kms_jwt_signing_public_keys\": \"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmvwB41qI5Fe41PDbXqcX5uOvSvfKh8l9QV0O3M+NsB4lKqQEP0t1hfoiXTpOgKz1ArYxHsQ2LeXifX4uwEbYJFlpVM+tyQkTWQjBOw6fsLYK2Xk4X2ylNXUUf7x3SDiOVxyvTh3OZW9kqrDBN9JxSoraNLyfw0hhW0SHpfs699SehgbQ7QWep/gVlKRLIz0XAXaZNw24s79ORcQlrCE6YD0PgQmpI/dK5xMML82n6y3qcTlywlGaU7OGIMdD+CTXA3BcOkgXeqZTXNaX1u6jCTa1lvAczun6avp5VZ4TFiuPo+y4rJ3GU+14cyT5NckEcaTKSvd86UdwK5Id9tl3bQIDAQAB\"}");
    }
}
