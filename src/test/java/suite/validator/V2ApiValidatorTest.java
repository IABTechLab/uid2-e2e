package suite.validator;

import app.common.HttpClient;
import app.component.Operator;
import app.component.Prometheus;
import com.uid2.client.IdentityTokens;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("unused")
public class V2ApiValidatorTest extends ValidatorTestBase {
    @ParameterizedTest(name = "/v2/token/generate - {0} - {2} - {4}")
    @MethodSource({
            "suite.validator.TestData#tokenEmailArgs",
            "suite.validator.TestData#tokenPhoneArgs"
    })
    public void testV2TokenGenerateViaMetrics(String label, Operator operator, String operatorName, Prometheus prometheus, String prometheusName, String type, String identity) throws Exception {
        assertThat(compareMetricValues(prometheus, Metric.MATCH_METRIC_NAME, "/v2/token/generate", HttpClient.HttpMethod.POST, () ->
                operator.v2TokenGenerate(type, identity, false)
        )).isTrue();
    }

    @ParameterizedTest(name = "/v2/token/refresh - {0} - {2} - {4}")
    @MethodSource({
            "suite.validator.TestData#tokenEmailArgs",
            "suite.validator.TestData#tokenPhoneArgs"
    })
    public void testV2TokenRefreshViaMetrics(String label, Operator operator, String operatorName, Prometheus prometheus, String prometheusName, String type, String identity) throws Exception {
        assertThat(compareMetricValues(prometheus, Metric.MATCH_METRIC_NAME, "/v2/token/refresh", HttpClient.HttpMethod.POST, () -> {
            IdentityTokens currentIdentity = operator.v2TokenGenerate(type, identity, false).getIdentity();
            operator.v2TokenRefresh(currentIdentity);
        })).isTrue();
    }

    @ParameterizedTest(name = "/v2/identity/map - {0} - {2} - {4}")
    @MethodSource({
            "suite.validator.TestData#identityMapBatchEmailArgs",
            "suite.validator.TestData#identityMapBatchPhoneArgs",
            "suite.validator.TestData#identityMapBatchBadEmailArgs",
            "suite.validator.TestData#identityMapBatchBadPhoneArgs"
    })
    public void testV2IdentityMapViaMetrics(String label, Operator operator, String operatorName, Prometheus prometheus, String prometheusName, String payload) throws Exception {
        assertThat(compareMetricValues(prometheus, Metric.MATCH_METRIC_NAME, "/v2/identity/map", HttpClient.HttpMethod.POST, () ->
                operator.v2IdentityMap(payload, false)
        )).isTrue();
    }

    @ParameterizedTest(name = "/v2/identity/buckets - {0} - {2} - {4}")
    @MethodSource({
            "suite.validator.TestData#identityBucketsArgs"
    })
    public void testV2IdentityBucketsViaMetrics(String label, Operator operator, String operatorName, Prometheus prometheus, String prometheusName, String payload) throws Exception {
        assertThat(compareMetricValues(prometheus, Metric.MATCH_METRIC_NAME, "/v2/identity/buckets", HttpClient.HttpMethod.POST, () ->
                operator.v2IdentityBuckets(payload)
        )).isTrue();
    }

    @ParameterizedTest(name = "/v2/key/sharing - {1} - {3}")
    @MethodSource({
            "suite.validator.TestData#baseArgs"
    })
    public void testV2KeySharingViaMetrics(Operator operator, String operatorName, Prometheus prometheus, String prometheusName) throws Exception {
        assertThat(compareMetricValues(prometheus, Metric.MATCH_METRIC_NAME, "/v2/key/sharing", HttpClient.HttpMethod.POST,
                operator::v2KeySharing
        )).isTrue();
    }
}
