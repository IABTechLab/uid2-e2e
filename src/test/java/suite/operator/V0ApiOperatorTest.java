package suite.operator;

import common.HttpClient;
import common.Mapper;
import app.component.Operator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.JsonAssert;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("unused")
public class V0ApiOperatorTest {
    private static final ObjectMapper OBJECT_MAPPER = Mapper.OBJECT_MAPPER;

    @ParameterizedTest(name = "/token/generate - {0} - {2}")
    @MethodSource({
            "suite.operator.TestData#tokenEmailArgs"
    })
    public void testV0TokenGenerate(String label, Operator operator, String operatorName, String type, String identity) throws Exception {
        JsonNode response = operator.v0TokenGenerate(type, identity);

        // TODO: Assert the value
        assertThat(JsonAssert.hasTopLevelFields(
                response, List.of("advertisement_token", "advertising_token", "refresh_token"),
                false
        )).isTrue();
    }

    @ParameterizedTest(name = "/token/generate - FAIL - {0} - {2}")
    @MethodSource({
            "suite.operator.TestData#tokenPhoneArgs",
            "suite.operator.TestData#tokenBadEmailArgs",
            "suite.operator.TestData#tokenBadPhoneArgs"
    })
    public void testV0TokenGenerateFailed(String label, Operator operator, String operatorName, String type, String identity) throws Exception {
        HttpClient.HttpException exception = assertThrows(
                HttpClient.HttpException.class,
                () -> operator.v0TokenGenerate(type, identity)
        );

        // TODO: Assert the value
        assertEquals("client_error", exception.getResponseJson().get("status").asText());
    }

    @ParameterizedTest(name = "/token/refresh - {0} - {2}")
    @MethodSource({
            "suite.operator.TestData#tokenEmailArgs"
    })
    public void testV0TokenRefresh(String label, Operator operator, String operatorName, String type, String identity) throws Exception {
        JsonNode generateResponse = operator.v0TokenGenerate(type, identity);
        String refreshToken = generateResponse.at("/refresh_token").asText();
        JsonNode refreshResponse = operator.v0TokenRefresh(refreshToken);

        // TODO: Assert the value
        assertThat(JsonAssert.hasContentInFields(
                refreshResponse, List.of("/advertisement_token", "/advertising_token", "/refresh_token")
        )).isTrue();
    }

    @ParameterizedTest(name = "/token/refresh - FAIL - {0} - {2}")
    @MethodSource({
            "suite.operator.TestData#tokenRefreshBadArgs"
    })
    public void testV0TokenRefreshFailed(String label, Operator operator, String operatorName, String refreshToken) throws Exception {
        JsonNode response = operator.v0TokenRefresh(refreshToken);

        assertThat(response).isEqualTo(Mapper.OBJECT_MAPPER.readTree("{\"advertisement_token\":\"\",\"advertising_token\":\"\",\"refresh_token\":\"\"}"));
    }

    @ParameterizedTest(name = "/token/validate - {0} - {2}")
    @MethodSource({
            "suite.operator.TestData#tokenValidateEmailArgs",
            "suite.operator.TestData#tokenValidateEmailHashArgs"
    })
    public void testV0TokenValidate(String label, Operator operator, String operatorName, String type, String identity) throws Exception {
        JsonNode generateResponse = operator.v0TokenGenerate(type, identity);
        String advertisingToken = generateResponse.at("/advertising_token").asText();
        JsonNode response = operator.v0TokenValidate(type, identity, advertisingToken);

        assertThat(response.asBoolean()).isTrue();
    }

    @ParameterizedTest(name = "/identity/map - {0} - {2}")
    @MethodSource({
            "suite.operator.TestData#identityMapEmailArgs"
    })
    public void testV0IdentityMap(String label, Operator operator, String operatorName, String type, String identity) throws Exception {
        String response = operator.v0IdentityMap(type, identity);

        // TODO: Assert the value
        assertThat(response.isBlank()).isFalse();
    }

    @ParameterizedTest(name = "/identity/map - FAIL - {0} - {2}")
    @MethodSource({
            "suite.operator.TestData#identityMapPhoneArgs",
            "suite.operator.TestData#identityMapBadEmailArgs",
            "suite.operator.TestData#identityMapBadPhoneArgs"
    })
    public void testV0IdentityMapFailed(String label, Operator operator, String operatorName, String type, String identity) throws Exception {
        HttpClient.HttpException exception = assertThrows(
                HttpClient.HttpException.class,
                () -> operator.v0IdentityMap(type, identity)
        );

        // TODO: Assert the value
        assertEquals("client_error", exception.getResponseJson().get("status").asText());
    }

    @ParameterizedTest(name = "/static/js/uid2-sdk-2.0.0.js - {1}")
    @MethodSource({
            "suite.operator.TestData#baseArgs"
    })
    public void testV0StaticJsUid2Sdk(Operator operator, String operatorName) throws Exception {
        String response = operator.v0StaticJsUid2Sdk();

        // TODO: Assert the value
        assertThat(response.isBlank()).isFalse();
    }
}
