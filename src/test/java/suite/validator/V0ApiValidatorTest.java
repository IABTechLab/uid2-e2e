package suite.validator;

import common.HttpClient;
import app.component.Operator;
import app.component.Prometheus;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("unused")
public class V0ApiValidatorTest extends ValidatorTestBase {
    @ParameterizedTest(name = "/token/generate - {0} - {2} - {4}")
    @MethodSource({
            "suite.validator.TestData#tokenEmailArgs"
    })
    public void testV0TokenGenerateViaMetrics(String label, Operator operator, String operatorName, Prometheus prometheus, String prometheusName, String type, String identity) throws Exception {
        assertThat(compareMetricValues(prometheus, Metric.MATCH_METRIC_NAME, "/token/generate", HttpClient.HttpMethod.GET, () ->
                operator.v0TokenGenerate(type, identity)
        )).isTrue();
    }

    @ParameterizedTest(name = "/token/refresh - {0} - {2} - {4}")
    @MethodSource({
            "suite.validator.TestData#tokenEmailArgs"
    })
    public void testV0TokenRefreshViaMetrics(String label, Operator operator, String operatorName, Prometheus prometheus, String prometheusName, String type, String identity) throws Exception {
        assertThat(compareMetricValues(prometheus, Metric.MATCH_METRIC_NAME, "/token/refresh", HttpClient.HttpMethod.GET, () -> {
            JsonNode generateResponse = operator.v0TokenGenerate(type, identity);
            String refreshToken = generateResponse.at("/refresh_token").asText();
            operator.v0TokenRefresh(refreshToken);
        })).isTrue();
    }

    @ParameterizedTest(name = "/identity/map - {0} - {2} - {4}")
    @MethodSource({
            "suite.validator.TestData#identityMapEmailArgs"
    })
    public void testV0IdentityMapViaMetrics(String label, Operator operator, String operatorName, Prometheus prometheus, String prometheusName, String type, String identity) throws Exception {
        assertThat(compareMetricValues(prometheus, Metric.MATCH_METRIC_NAME, "/identity/map", HttpClient.HttpMethod.GET, () ->
                operator.v0IdentityMap(type, identity)
        )).isTrue();
    }

    @ParameterizedTest(name = "/static/js/uid2-sdk-2.0.0.js - {1} - {3}")
    @MethodSource({
            "suite.validator.TestData#baseArgs"
    })
    public void testV0StaticJsUid2SdkViaMetrics(Operator operator, String operatorName, Prometheus prometheus, String prometheusName) throws Exception {
        assertThat(compareMetricValues(prometheus, Metric.MATCH_METRIC_NAME, "/static/js/uid2-sdk-2.0.0.js", HttpClient.HttpMethod.GET,
                operator::v0StaticJsUid2Sdk
        )).isTrue();
    }
}
