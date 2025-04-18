package suite.optout;

import app.AppsMap;
import app.component.App;
import app.component.Operator;
import org.junit.jupiter.params.provider.Arguments;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public final class TestData {
    public static final String ADVERTISING_ID = "advertising_id";
    public static final String OPTED_OUT_SINCE = "opted_out_since";

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

        Set<List<String>> inputs = generateEmailSet(1);

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
        if (App.PHONE_SUPPORT) {
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
        Set<List<String>> inputs = generatePhoneSet(1);

        Set<Arguments> args = new HashSet<>();
        if (App.PHONE_SUPPORT) {
            for (Operator operator : operators) {
                for (List<String> input : inputs) {
                    args.add(Arguments.of(input.get(0), operator, operator.getName(), input.get(1), input.get(2)));
                }
            }
        }
        return args;
    }

    public static Set<Arguments> identityMapEmailArgs() {
        Set<Operator> operators = getPublicOperators();
        Set<List<String>> inputs = generateEmailSet(4);

        Set<Arguments> args = new HashSet<>();
        for (Operator operator : operators) {
            for (List<String> input : inputs) {
                args.add(Arguments.of(input.get(0), operator, operator.getName(), input.get(1), input.get(2), Boolean.parseBoolean(input.get(3))));
            }
        }
        return args;
    }

    public static Set<Arguments> identityMapPhoneArgs() {
        Set<Operator> operators = getPublicOperators();
        Set<List<String>> inputs = generatePhoneSet(4);

        Set<Arguments> args = new HashSet<>();
        for (Operator operator : operators) {
            for (List<String> input : inputs) {
                args.add(Arguments.of(input.get(0), operator, operator.getName(), input.get(1), input.get(2), Boolean.parseBoolean(input.get(3))));
            }
        }
        return args;
    }

    private static Set<List<String>> generateEmailSet(int count) {
        Random random = new Random();
        Set<List<String>> inputs = new HashSet<>(count);
        for (int i = 0; i < count; ++i) {
            int number = random.nextInt(100000000);
            String email = "test.email" + number + "@" + getRandomString(2, 10) + "." + getRandomString(2, 10);
            inputs.add(List.of("good email " + (i + 1), "email", email, i % 2 == 0 ? "true" : "false"));
        }

        return inputs;
    }

    private static Set<List<String>> generatePhoneSet(int count) {
        Random random = new Random();
        Set<List<String>> inputs = new HashSet<>();
        final long bound = (10000000000L - 1000000000L) + 1;
        for (int i = 0; i < count; ++i) {
            StringBuilder phone = new StringBuilder(String.valueOf(random.nextLong(bound) + 1000000000L));
            while (phone.length() < 10) {
                phone.insert(0, "0");
            }
            phone.insert(0, "+0");
            inputs.add(List.of("good phone " + (i + 1), "phone", phone.toString(),  i % 2 == 0 ? "true" : "false"));
        }
        return inputs;
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
