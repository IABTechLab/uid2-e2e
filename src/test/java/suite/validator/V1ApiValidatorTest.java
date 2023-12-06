package suite.validator;

import app.common.HttpClient;
import app.component.Operator;
import app.component.Prometheus;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("unused")
public class V1ApiValidatorTest extends ValidatorTestBase {
    @ParameterizedTest(name = "/v1/token/generate - {0} - {2} - {4}")
    @MethodSource({
            "suite.validator.TestData#tokenEmailArgs",
            "suite.validator.TestData#tokenPhoneArgs"
    })
    public void testV1TokenGenerateViaMetrics(String label, Operator operator, String operatorName, Prometheus prometheus, String prometheusName, String type, String identity) throws Exception {
        assertThat(compareMetricValues(prometheus, Metric.MATCH_METRIC_NAME, "/v1/token/generate", HttpClient.HttpMethod.GET, () ->
                operator.v1TokenGenerate(type, identity)
        )).isTrue();
    }

    @ParameterizedTest(name = "/v1/token/refresh - {0} - {2} - {4}")
    @MethodSource({
            "suite.validator.TestData#tokenEmailArgs",
            "suite.validator.TestData#tokenPhoneArgs"
    })
    public void testV1TokenRefreshViaMetrics(String label, Operator operator, String operatorName, Prometheus prometheus, String prometheusName, String type, String identity) throws Exception {
        assertThat(compareMetricValues(prometheus, Metric.MATCH_METRIC_NAME, "/v1/token/refresh", HttpClient.HttpMethod.GET, () -> {
            JsonNode generateResponse = operator.v1TokenGenerate(type, identity);
            String refreshToken = generateResponse.at("/body/refresh_token").asText();
            operator.v1TokenRefresh(refreshToken);
        })).isTrue();
    }

    @ParameterizedTest(name = "/v1/identity/map - {0} - {2} - {4}")
    @MethodSource({
            "suite.validator.TestData#identityMapEmailArgs",
            "suite.validator.TestData#identityMapPhoneArgs"
    })
    public void testV1IdentityMapViaMetrics(String label, Operator operator, String operatorName, Prometheus prometheus, String prometheusName, String type, String identity) throws Exception {
        assertThat(compareMetricValues(prometheus, Metric.MATCH_METRIC_NAME, "/v1/identity/map", HttpClient.HttpMethod.GET, () ->
                operator.v1IdentityMap(type, identity)
        )).isTrue();
    }

    @ParameterizedTest(name = "/v1/identity/map - POST - {0} - {2} - {4}")
    @MethodSource({
            "suite.validator.TestData#identityMapBatchEmailArgs",
            "suite.validator.TestData#identityMapBatchPhoneArgs",
            "suite.validator.TestData#identityMapBatchBadEmailArgs",
            "suite.validator.TestData#identityMapBatchBadPhoneArgs"
    })
    public void testV1IdentityMapBatchViaMetrics(String label, Operator operator, String operatorName, Prometheus prometheus, String prometheusName, String payload) throws Exception {
        assertThat(compareMetricValues(prometheus, Metric.MATCH_METRIC_NAME, "/v1/identity/map", HttpClient.HttpMethod.POST, () ->
                operator.v1IdentityMapBatch(payload)
        )).isTrue();
    }
}
