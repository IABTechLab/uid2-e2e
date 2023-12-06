package suite.validator;

import app.common.HttpClient;
import app.component.Prometheus;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ValidatorTestBase {
    protected enum Metric {
        MATCH_METRIC_NAME("uid2_validation_total_matches_v2_total"),
        MISMATCH_METRIC_NAME("uid2_validation_total_mismatches_v2_total"),
        SKIP_METRIC_NAME("uid2_validation_skipped_v2_total");

        private final String value;

        Metric(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    protected interface TestInterface {
        void runTest() throws Exception;
    }

    private static final Pattern METRIC_VALUE_PATTERN = Pattern.compile("} ([0-9]+\\.[0-9]+)$");
    private static final int RUN_COUNT = 20;
    private static final int METRIC_READ_ATTEMPT_DELAY = 100;
    private static final int METRIC_READ_MAX_ATTEMPTS = 50;

    protected boolean compareMetricValues(Prometheus prometheus, Metric metric, String path, HttpClient.HttpMethod method, TestInterface testInterface) throws Exception {
        BigDecimal beforeMetricValue = getMetricValue(prometheus, metric, path, method);
        for (int i = 0; i < RUN_COUNT; i++) {
            testInterface.runTest();
        }

        var isPassing = false;
        var metricReadAttempts = 0;
        do {
            Thread.sleep(METRIC_READ_ATTEMPT_DELAY);
            isPassing = isPassingBasedOnMetrics(prometheus, metric, path, method, beforeMetricValue);
        } while (!isPassing && metricReadAttempts++ < METRIC_READ_MAX_ATTEMPTS);

        return isPassing;
    }

    private boolean isPassingBasedOnMetrics(Prometheus prometheus, Metric metric, String path, HttpClient.HttpMethod method, BigDecimal beforeMetricValue) throws Exception {
        BigDecimal afterMetricValue = getMetricValue(prometheus, metric, path, method);

        return beforeMetricValue != null
                && afterMetricValue != null
                && afterMetricValue.subtract(beforeMetricValue).subtract(new BigDecimal(RUN_COUNT)).compareTo(BigDecimal.ZERO) >= 0;
    }

    protected BigDecimal getMetricValue(Prometheus prometheus, Metric metric, String path, HttpClient.HttpMethod method) throws Exception {
        String metricLine = Arrays.stream(prometheus.getMetrics().split("\n"))
                .filter(metricString -> metricString.startsWith(metric.toString())
                        && metricString.contains("application=\"uid2-validator-v2\"")
                        && metricString.contains("matcher=\"%s\"".formatted(path))
                        && metricString.contains("method=\"%s\"".formatted(method)))
                .findFirst()
                .orElse("");
        Matcher matcher = METRIC_VALUE_PATTERN.matcher(metricLine);
        if (matcher.find()) {
            String valueString = matcher.group(1);
            return new BigDecimal(valueString);
        } else {
            return null;
        }
    }
}
