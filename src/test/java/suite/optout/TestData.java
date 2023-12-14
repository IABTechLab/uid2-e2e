package suite.optout;

import app.AppsMap;
import app.common.EnvUtil;
import app.component.Operator;
import org.junit.jupiter.params.provider.Arguments;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public final class TestData {
    private static final boolean PHONE_SUPPORT = Boolean.parseBoolean(EnvUtil.getEnv("UID2_E2E_PHONE_SUPPORT"));

    private TestData() {
    }

    public static Set<Arguments> tokenEmailArgs() {
        Set<Operator> operators = getPublicOperators();
        Set<List<String>> inputs = Set.of(
                List.of("good email", "email", "test.user1@thetradedesk.com")
        );

        Set<Arguments> args = new HashSet<>();
        for (Operator operator : operators) {
            for (List<String> input : inputs) {
                args.add(Arguments.of(input.get(0), operator, operator.getName(), input.get(1), input.get(2)));
            }
        }
        return args;
    }

    public static Set<Arguments> optoutTokenEmailArgs() {
        Set<Operator> operators = getPublicOperators();
        Random random = new Random();
        int number = random.nextInt(100000000);
        String email = "test.email" + number + "@" + getRandomString(2, 10) + "." + getRandomString(2, 10);
        Set<List<String>> inputs = Set.of(
                List.of("good email", "email", email)
        );

        Set<Arguments> args = new HashSet<>();
        for (Operator operator : operators) {
            for (List<String> input : inputs) {
                args.add(Arguments.of(input.get(0), operator, operator.getName(), input.get(1), input.get(2)));
            }
        }
        return args;
    }

    public static Set<Arguments> tokenPhoneArgs() {
        Set<Operator> operators = getPublicOperators();
        Set<List<String>> inputs = Set.of(
                List.of("good phone", "phone", "+10000001111")
        );

        Set<Arguments> args = new HashSet<>();
        if (PHONE_SUPPORT) {
            for (Operator operator : operators) {
                for (List<String> input : inputs) {
                    args.add(Arguments.of(input.get(0), operator, operator.getName(), input.get(1), input.get(2)));
                }
            }
        }
        return args;
    }

    public static Set<Arguments> optoutTokenPhoneArgs() {
        Set<Operator> operators = getPublicOperators();
        Random random = new Random();
        StringBuilder phone = new StringBuilder(String.valueOf(random.nextLong((10000000000L - 1000000000L) +1) + 1000000000L));
        while (phone.length() < 10) {
            phone.insert(0, "0");
        }
        phone.insert(0, "+0");
        Set<List<String>> inputs = Set.of(
                List.of("good phone", "phone", phone.toString())
        );

        Set<Arguments> args = new HashSet<>();
        if (PHONE_SUPPORT) {
            for (Operator operator : operators) {
                for (List<String> input : inputs) {
                    args.add(Arguments.of(input.get(0), operator, operator.getName(), input.get(1), input.get(2)));
                }
            }
        }
        return args;
    }

    private static String getRandomString(int minLength, int maxLength) {
        String alphabet = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder s = new StringBuilder();
        Random rnd = new Random();
        int length = rnd.nextInt(minLength, maxLength + 1);
        while (s.length() < length) { // length of the random string.
            int index = rnd.nextInt(alphabet.length());
            s.append(alphabet.charAt(index));
        }
        return s.toString();
    }

    private static Set<Operator> getPublicOperators() {
        return AppsMap.getApps(Operator.class).stream()
                .filter(s -> s.getType() != Operator.Type.PRIVATE)
                .collect(Collectors.toSet());
    }
}
