package suite.validator;

import app.AppsMap;
import app.component.Operator;
import app.component.Prometheus;
import org.junit.jupiter.params.provider.Arguments;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class TestData {
    private TestData() {
    }

    public static Set<Arguments> baseArgs() {
        Set<Operator> operators = getPublicOperators();
        Prometheus prometheus = getPrometheus();

        Set<Arguments> args = new HashSet<>();
        for (Operator operator : operators) {
            args.add(Arguments.of(operator, operator.getName(), prometheus, prometheus.getName()));
        }
        return args;
    }

    public static Set<Arguments> tokenEmailArgs() {
        Set<Operator> operators = getPublicOperators();
        Prometheus prometheus = getPrometheus();
        Set<List<String>> inputs = Set.of(
                List.of("good email", "email", "test.user@gmail.com")
        );

        Set<Arguments> args = new HashSet<>();
        for (Operator operator : operators) {
            for (List<String> input : inputs) {
                args.add(Arguments.of(input.get(0), operator, operator.getName(), prometheus, prometheus.getName(), input.get(1), input.get(2)));
            }
        }
        return args;
    }

    public static Set<Arguments> tokenPhoneArgs() {
        Set<Operator> operators = getPublicOperators();
        Prometheus prometheus = getPrometheus();
        Set<List<String>> inputs = Set.of(
                List.of("good phone", "phone", "+12345678901")
        );

        Set<Arguments> args = new HashSet<>();
        for (Operator operator : operators) {
            for (List<String> input : inputs) {
                args.add(Arguments.of(input.get(0), operator, operator.getName(), prometheus, prometheus.getName(), input.get(1), input.get(2)));
            }
        }
        return args;
    }

    public static Set<Arguments> identityMapEmailArgs() {
        Set<Operator> operators = getPublicOperators();
        Prometheus prometheus = getPrometheus();
        Set<List<String>> inputs = Set.of(
                List.of("good email", "email", "user@example.com"),
                List.of("good email hash", "email_hash", "eVvLS/Vg+YZ6+z3i0NOpSXYyQAfEXqCZ7BTpAjFUBUc=")
        );

        Set<Arguments> args = new HashSet<>();
        for (Operator operator : operators) {
            for (List<String> input : inputs) {
                args.add(Arguments.of(input.get(0), operator, operator.getName(), prometheus, prometheus.getName(), input.get(1), input.get(2)));
            }
        }
        return args;
    }

    public static Set<Arguments> identityMapPhoneArgs() {
        Set<Operator> operators = getPublicOperators();
        Prometheus prometheus = getPrometheus();
        Set<List<String>> inputs = Set.of(
                List.of("good phone ", "phone", "+1111111111"),
                List.of("good phone hash", "phone_hash", "eVvLS/Vg+YZ6+z3i0NOpSXYyQAfEXqCZ7BTpAjFUBUc=")
        );

        Set<Arguments> args = new HashSet<>();
        for (Operator operator : operators) {
            for (List<String> input : inputs) {
                args.add(Arguments.of(input.get(0), operator, operator.getName(), prometheus, prometheus.getName(), input.get(1), input.get(2)));
            }
        }
        return args;
    }

    public static Set<Arguments> identityMapBatchEmailArgs() {
        Set<Operator> operators = getPublicOperators();
        Prometheus prometheus = getPrometheus();
        Set<List<String>> inputs = Set.of(
                List.of("good email list policy=1", "{\"email\":[\"user@example.com\",\"user2@example.com\"], \"policy\":1}"),
                List.of("good email hash list policy=1", "{\"email_hash\":[\"eVvLS/Vg+YZ6+z3i0NOpSXYyQAfEXqCZ7BTpAjFUBUc=\",\"tMmiiTI7IaAcPpQPFQ65uMVCWH8av9jw4cwf/F5HVRQ=\"], \"policy\":1}"),
                List.of("empty email list policy=1", "{\"email\":[], \"policy\":1}"),
                List.of("empty email hash list policy=1", "{\"email_hash\":[], \"policy\":1}"),
                List.of("good email list optout_check=1", "{\"email\":[\"user@example.com\",\"user2@example.com\"], \"optout_check\":1}"),
                List.of("good email hash list optout_check=1", "{\"email_hash\":[\"eVvLS/Vg+YZ6+z3i0NOpSXYyQAfEXqCZ7BTpAjFUBUc=\",\"tMmiiTI7IaAcPpQPFQ65uMVCWH8av9jw4cwf/F5HVRQ=\"], \"optout_check\":1}"),
                List.of("empty email list optout_check=1", "{\"email\":[], \"optout_check\":1}"),
                List.of("empty email hash list optout_check=1", "{\"email_hash\":[], \"optout_check\":1}")
        );

        Set<Arguments> args = new HashSet<>();
        for (Operator operator : operators) {
            for (List<String> input : inputs) {
                args.add(Arguments.of(input.get(0), operator, operator.getName(), prometheus, prometheus.getName(), input.get(1)));
            }
        }
        return args;
    }

    public static Set<Arguments> identityMapBatchPhoneArgs() {
        Set<Operator> operators = getPublicOperators();
        Prometheus prometheus = getPrometheus();
        Set<List<String>> inputs = Set.of(
                List.of("good phone list policy=1", "{\"phone\":[\"+1111111111\",\"+2222222222\"], \"policy\":1}"),
                List.of("good phone hash list policy=1", "{\"phone_hash\":[\"eVvLS/Vg+YZ6+z3i0NOpSXYyQAfEXqCZ7BTpAjFUBUc=\",\"tMmiiTI7IaAcPpQPFQ65uMVCWH8av9jw4cwf/F5HVRQ=\"], \"policy\":1}"),
                List.of("empty phone list policy=1", "{\"phone\":[], \"policy\":1}"),
                List.of("empty phone hash list policy=1", "{\"phone_hash\":[], \"policy\":1}"),
                List.of("good phone list optout_check=1", "{\"phone\":[\"+1111111111\",\"+2222222222\"], \"optout_check\":1}"),
                List.of("good phone hash list optout_check=1", "{\"phone_hash\":[\"eVvLS/Vg+YZ6+z3i0NOpSXYyQAfEXqCZ7BTpAjFUBUc=\",\"tMmiiTI7IaAcPpQPFQ65uMVCWH8av9jw4cwf/F5HVRQ=\"], \"optout_check\":1}"),
                List.of("empty phone list optout_check=1", "{\"phone\":[], \"optout_check\":1}"),
                List.of("empty phone hash list optout_check=1", "{\"phone_hash\":[], \"optout_check\":1}")
        );

        Set<Arguments> args = new HashSet<>();
        for (Operator operator : operators) {
            for (List<String> input : inputs) {
                args.add(Arguments.of(input.get(0), operator, operator.getName(), prometheus, prometheus.getName(), input.get(1)));
            }
        }
        return args;
    }

    public static Set<Arguments> identityMapBatchBadEmailArgs() {
        Set<Operator> operators = getPublicOperators();
        Prometheus prometheus = getPrometheus();
        Set<List<String>> inputs = Set.of(
                List.of("bad email list policy=1", "{\"email\":[\"abc\",\"user2@example.com\"], \"policy\":1}"),
                List.of("bad email hash list policy=1", "{\"email_hash\":[\"rYsn2sTT1lRlZerX+4DXZMQ+DfcejOXoMVczcF2jCLM=\",\"abc\"], \"policy\":1}"),
                List.of("bad email list optout_check=1", "{\"email\":[\"abc\",\"user2@example.com\"], \"optout_check\":1}"),
                List.of("bad email hash list optout_check=1", "{\"email_hash\":[\"rYsn2sTT1lRlZerX+4DXZMQ+DfcejOXoMVczcF2jCLM=\",\"abc\"], \"optout_check\":1}")
        );

        Set<Arguments> args = new HashSet<>();
        for (Operator operator : operators) {
            for (List<String> input : inputs) {
                args.add(Arguments.of(input.get(0), operator, operator.getName(), prometheus, prometheus.getName(), input.get(1)));
            }
        }
        return args;
    }

    public static Set<Arguments> identityMapBatchBadPhoneArgs() {
        Set<Operator> operators = getPublicOperators();
        Prometheus prometheus = getPrometheus();
        Set<List<String>> inputs = Set.of(
                List.of("bad phone list policy=1", "{\"phone\":[\"+1111111111\",\"abc\"], \"policy\":1}"),
                List.of("bad phone hash list policy=1", "{\"phone_hash\":[\"abc\",\"tMmiiTI7IaAcPpQPFQ65uMVCWH8av9jw4cwf/F5HVRQ=\"], \"policy\":1}"),
                List.of("bad phone list optout_check=1", "{\"phone\":[\"+1111111111\",\"abc\"], \"optout_check\":1}"),
                List.of("bad phone hash optout_check=1", "{\"phone_hash\":[\"abc\",\"tMmiiTI7IaAcPpQPFQ65uMVCWH8av9jw4cwf/F5HVRQ=\"], \"optout_check\":1}")
        );

        Set<Arguments> args = new HashSet<>();
        for (Operator operator : operators) {
            for (List<String> input : inputs) {
                args.add(Arguments.of(input.get(0), operator, operator.getName(), prometheus, prometheus.getName(), input.get(1)));
            }
        }
        return args;
    }

    public static Set<Arguments> identityBucketsArgs() {
        Set<Operator> operators = getPublicOperators();
        Prometheus prometheus = getPrometheus();
        Set<List<String>> inputs = Set.of(
                List.of("good timestamp", "{\"since_timestamp\":\"2021-01-01T00:00:00\"}")
        );

        Set<Arguments> args = new HashSet<>();
        for (Operator operator : operators) {
            for (List<String> input : inputs) {
                args.add(Arguments.of(input.get(0), operator, operator.getName(), prometheus, prometheus.getName(), input.get(1)));
            }
        }
        return args;
    }

    private static Set<Operator> getPublicOperators() {
        return AppsMap.getApps(Operator.class).stream()
                .filter(operator -> operator.getType() == Operator.Type.PUBLIC)
                .collect(Collectors.toUnmodifiableSet());
    }

    private static Prometheus getPrometheus() {
        return AppsMap.getApps(Prometheus.class).stream()
                .filter(prometheus -> prometheus.getName().contains("Validator Prometheus"))
                .findFirst()
                .orElse(null);
    }
}
