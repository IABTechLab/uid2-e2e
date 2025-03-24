package suite.operator;

import common.HttpClient;
import common.Mapper;
import app.component.Operator;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("unused")
public class V1ApiOperatorTest {
    /*
    TODO:
        /v1/identity/map - POST - Add failure case
     */

    @ParameterizedTest(name = "/v1/token/generate - {0} - {2}")
    @MethodSource({
            "suite.operator.TestData#tokenEmailArgs",
            "suite.operator.TestData#tokenPhoneArgs"
    })
    public void testV1TokenGenerate(String label, Operator operator, String operatorName, String type, String identity) throws Exception {
        JsonNode response = operator.v1TokenGenerate(type, identity);

        // TODO: Assert the value
        assertThat(response.at("/status").asText()).isEqualTo("success");
    }

    @ParameterizedTest(name = "/v1/token/generate - FAIL - {0} - {2}")
    @MethodSource({
            "suite.operator.TestData#tokenBadEmailArgs",
            "suite.operator.TestData#tokenBadPhoneArgs"
    })
    public void testV1TokenGenerateFailed(String label, Operator operator, String operatorName, String type, String identity) throws Exception {
        HttpClient.HttpException exception = assertThrows(
                HttpClient.HttpException.class,
                () -> operator.v1TokenGenerate(type, identity)
        );

        assertThat(exception.getResponseJson().findValue("status").asText()).isEqualTo("client_error");
    }

    @ParameterizedTest(name = "/v1/token/refresh - {0} - {2}")
    @MethodSource({
            "suite.operator.TestData#tokenEmailArgs",
            "suite.operator.TestData#tokenPhoneArgs"
    })
    public void testV1TokenRefresh(String label, Operator operator, String operatorName, String type, String identity) throws Exception {
        JsonNode generateResponse = operator.v1TokenGenerate(type, identity);
        String refreshToken = generateResponse.at("/body/refresh_token").asText();
        JsonNode refreshResponse = operator.v1TokenRefresh(refreshToken);

        // TODO: Assert the value
        assertThat(refreshResponse.at("/status").asText()).isEqualTo("success");
    }

    @ParameterizedTest(name = "/v1/token/refresh - FAIL - {0} - {2}")
    @MethodSource({
            "suite.operator.TestData#tokenRefreshBadArgs"
    })
    public void testV1TokenRefreshFailed(String label, Operator operator, String operatorName, String refreshToken) throws Exception {
        HttpClient.HttpException exception = assertThrows(
                HttpClient.HttpException.class,
                () -> operator.v1TokenRefresh(refreshToken)
        );

        assertThat(exception.getResponseJson()).isEqualTo(Mapper.OBJECT_MAPPER.readTree("{\"message\":\"Invalid Token presented %s\",\"status\":\"invalid_token\"}".formatted(refreshToken)));
    }

    @ParameterizedTest(name = "/v1/token/validate - {0} - {2}")
    @MethodSource({
            "suite.operator.TestData#tokenValidateEmailArgs",
            "suite.operator.TestData#tokenValidateEmailHashArgs",
            "suite.operator.TestData#tokenValidatePhoneArgs",
            "suite.operator.TestData#tokenValidatePhoneHashArgs"
    })
    public void testV1TokenValidate(String label, Operator operator, String operatorName, String type, String identity) throws Exception {
        JsonNode generateResponse = operator.v1TokenGenerate(type, identity);
        String advertisingToken = generateResponse.at("/body/advertising_token").asText();
        JsonNode response = operator.v1TokenValidate(type, identity, advertisingToken);

        assertThat(response).isEqualTo(Mapper.OBJECT_MAPPER.readTree("{\"body\":true,\"status\":\"success\"}"));
    }

    @ParameterizedTest(name = "/v1/identity/map - {0} - {2}")
    @MethodSource({
            "suite.operator.TestData#identityMapEmailArgs",
            "suite.operator.TestData#identityMapPhoneArgs"
    })
    public void testV1IdentityMap(String label, Operator operator, String operatorName, String type, String identity) throws Exception {
        JsonNode response = operator.v1IdentityMap(type, identity);

        // TODO: Assert the value
        assertThat(response.at("/status").asText()).isEqualTo("success");
    }

    @ParameterizedTest(name = "/v1/identity/map - FAIL - {0} - {2}")
    @MethodSource({
            "suite.operator.TestData#identityMapBadEmailArgs",
            "suite.operator.TestData#identityMapBadPhoneArgs"
    })
    public void testV1IdentityMapFailed(String label, Operator operator, String operatorName, String type, String identity) throws Exception {
        HttpClient.HttpException exception = assertThrows(
                HttpClient.HttpException.class,
                () -> operator.v1IdentityMap(type, identity)
        );

        assertThat(exception.getResponseJson().findValue("status").asText()).isEqualTo("client_error");
    }

    @ParameterizedTest(name = "/v1/identity/map - POST - {0} - {2}")
    @MethodSource({
            "suite.operator.TestData#identityMapBatchEmailArgs",
            "suite.operator.TestData#identityMapBatchPhoneArgs",
            "suite.operator.TestData#identityMapBatchBadEmailArgs",
            "suite.operator.TestData#identityMapBatchBadPhoneArgs"
    })
    public void testV1IdentityMapBatch(String label, Operator operator, String operatorName, String payload) throws Exception {
        JsonNode response = operator.v1IdentityMapBatch(payload);

        // TODO: Assert the value
        assertThat(response.at("/status").asText()).isEqualTo("success");
    }

    @ParameterizedTest(name = "/v1/key/latest - {2}")
    @MethodSource({
            "suite.operator.TestData#baseArgs"
    })
    public void testV1KeyLatest(Operator operator, String operatorName) throws Exception {
        JsonNode response = operator.v1KeyLatest();

        // TODO: Assert the value
        assertThat(response.at("/status").asText()).isEqualTo("success");
    }
}
