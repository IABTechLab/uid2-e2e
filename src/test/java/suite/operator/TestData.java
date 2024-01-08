package suite.operator;

import app.AppsMap;
import app.common.EnvUtil;
import app.component.Operator;
import org.junit.jupiter.params.provider.Arguments;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class TestData {
    public static final int RAW_UID2_LENGTH = 44;
    private static final boolean PHONE_SUPPORT = Boolean.parseBoolean(EnvUtil.getEnv("UID2_E2E_PHONE_SUPPORT"));

    private TestData() {
    }

    public static Set<Arguments> baseArgs() {
        Set<Operator> operators = AppsMap.getApps(Operator.class);

        Set<Arguments> args = new HashSet<>();
        for (Operator operator : operators) {
            args.add(Arguments.of(operator, operator.getName()));
        }
        return args;
    }

    public static Set<Arguments> tokenEmailArgs() {
        Set<Operator> operators = AppsMap.getApps(Operator.class);
        Set<List<String>> inputs = Set.of(
                List.of("good email", "email", "test.user2@thetradedesk.com")
        );

        Set<Arguments> args = new HashSet<>();
        for (Operator operator : operators) {
            for (List<String> input : inputs) {
                args.add(Arguments.of(input.get(0), operator, operator.getName(), input.get(1), input.get(2)));
            }
        }
        return args;
    }

    public static Set<Arguments> tokenEmailArgsSpecialOptout() {
        Set<Operator> operators = AppsMap.getApps(Operator.class);

        Set<Arguments> args = new HashSet<>();
        for (Operator operator : operators) {
            args.add(Arguments.of("optout special email", operator, operator.getName(), "email", "optout@example.com", true));
            args.add(Arguments.of("optout special email", operator, operator.getName(), "email", "optout@example.com", false));
        }
        return args;
    }

    public static Set<Arguments> tokenEmailArgsSpecialRefreshOptout() {
        Set<Operator> operators = AppsMap.getApps(Operator.class);

        Set<Arguments> args = new HashSet<>();
        for (Operator operator : operators) {
            args.add(Arguments.of("optout refresh special email", operator, operator.getName(), "email", "refresh-optout@example.com"));
        }
        return args;
    }

    public static Set<Arguments> tokenEmailArgsLocalMockOptout() {
        Set<Operator> operators = AppsMap.getApps(Operator.class);

        Set<Arguments> args = new HashSet<>();
        for (Operator operator : operators) {
            args.add(Arguments.of("optout local mock email", operator, operator.getName(), "email", "local-mock-optout@example.com", false));
        }
        return args;
    }

    public static Set<Arguments> tokenPhoneArgs() {
        Set<Operator> operators = AppsMap.getApps(Operator.class);
        Set<List<String>> inputs = Set.of(
                List.of("good phone", "phone", "+10000000001")
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

    public static Set<Arguments> tokenPhoneArgsSpecialOptout() {
        Set<Operator> operators = AppsMap.getApps(Operator.class);

        Set<Arguments> args = new HashSet<>();
        if (PHONE_SUPPORT) {
            for (Operator operator : operators) {
                args.add(Arguments.of("optout special phone", operator, operator.getName(),  "phone", "+00000000000", true));
                args.add(Arguments.of("optout special phone", operator, operator.getName(),  "phone", "+00000000000", false));
            }
        }
        return args;
    }

    public static Set<Arguments> tokenPhoneArgsSpecialRefreshOptout() {
        Set<Operator> operators = AppsMap.getApps(Operator.class);

        Set<Arguments> args = new HashSet<>();
        if (PHONE_SUPPORT) {
            for (Operator operator : operators) {
                args.add(Arguments.of("optout special refresh phone", operator, operator.getName(), "phone", "+00000000002"));
            }
        }
        return args;
    }

    public static Set<Arguments> tokenBadEmailArgs() {
        Set<Operator> operators = AppsMap.getApps(Operator.class);
        Set<List<String>> inputs = Set.of(
                List.of("empty email", "email", ""),
                List.of("bad email", "email", "abc")
        );

        Set<Arguments> args = new HashSet<>();
        for (Operator operator : operators) {
            for (List<String> input : inputs) {
                args.add(Arguments.of(input.get(0), operator, operator.getName(), input.get(1), input.get(2)));
            }
        }
        return args;
    }

    public static Set<Arguments> tokenBadPhoneArgs() {
        Set<Operator> operators = AppsMap.getApps(Operator.class);
        Set<List<String>> inputs = Set.of(
                List.of("empty phone", "phone", ""),
                List.of("bad phone", "phone", "abc")
        );

        Set<Arguments> args = new HashSet<>();
        for (Operator operator : operators) {
            for (List<String> input : inputs) {
                args.add(Arguments.of(input.get(0), operator, operator.getName(), input.get(1), input.get(2)));
            }
        }
        return args;
    }

    public static Set<Arguments> tokenRefreshBadArgs() {
        Set<Operator> operators = AppsMap.getApps(Operator.class);
        Set<List<String>> inputs = Set.of(
                List.of("empty refresh token", ""),
                List.of("bad refresh token", "abc")
        );

        Set<Arguments> args = new HashSet<>();
        for (Operator operator : operators) {
            for (List<String> input : inputs) {
                args.add(Arguments.of(input.get(0), operator, operator.getName(), input.get(1)));
            }
        }
        return args;
    }

    public static Set<Arguments> tokenValidateEmailArgs() {
        Set<Operator> operators = AppsMap.getApps(Operator.class);
        Set<List<String>> inputs = Set.of(
                List.of("good email", "email", "validate@example.com")
        );

        Set<Arguments> args = new HashSet<>();
        for (Operator operator : operators) {
            for (List<String> input : inputs) {
                args.add(Arguments.of(input.get(0), operator, operator.getName(), input.get(1), input.get(2)));
            }
        }
        return args;
    }

    public static Set<Arguments> tokenValidateEmailHashArgs() {
        Set<Operator> operators = AppsMap.getApps(Operator.class);
        Set<List<String>> inputs = Set.of(
                List.of("good email hash", "email_hash", "ntI244ZRTXwAwpki6/M5cyBYW7h/Wq576lnN3l9+W/c=")
        );

        Set<Arguments> args = new HashSet<>();
        for (Operator operator : operators) {
            for (List<String> input : inputs) {
                args.add(Arguments.of(input.get(0), operator, operator.getName(), input.get(1), input.get(2)));
            }
        }
        return args;
    }

    public static Set<Arguments> tokenValidatePhoneArgs() {
        Set<Operator> operators = AppsMap.getApps(Operator.class);
        Set<List<String>> inputs = Set.of(
                List.of("good phone", "phone", "+12345678901")
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

    public static Set<Arguments> tokenValidatePhoneHashArgs() {
        Set<Operator> operators = AppsMap.getApps(Operator.class);
        Set<List<String>> inputs = Set.of(
                List.of("good phone hash", "phone_hash", "EObwtHBUqDNZR33LNSMdtt5cafsYFuGmuY4ZLenlue4=")
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

    public static Set<Arguments> identityMapEmailArgs() {
        Set<Operator> operators = AppsMap.getApps(Operator.class);
        Set<List<String>> inputs = Set.of(
                List.of("good email", "email", "user@example.com"),
                List.of("good email hash", "email_hash", "eVvLS/Vg+YZ6+z3i0NOpSXYyQAfEXqCZ7BTpAjFUBUc=")
        );

        Set<Arguments> args = new HashSet<>();
        for (Operator operator : operators) {
            for (List<String> input : inputs) {
                args.add(Arguments.of(input.get(0), operator, operator.getName(), input.get(1), input.get(2)));
            }
        }
        return args;
    }

    public static Set<Arguments> identityMapPhoneArgs() {
        Set<Operator> operators = AppsMap.getApps(Operator.class);
        Set<List<String>> inputs = Set.of(
                List.of("good phone ", "phone", "+1111111111"),
                List.of("good phone hash", "phone_hash", "eVvLS/Vg+YZ6+z3i0NOpSXYyQAfEXqCZ7BTpAjFUBUc=")
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

    public static Set<Arguments> identityMapBadEmailArgs() {
        Set<Operator> operators = AppsMap.getApps(Operator.class);
        Set<List<String>> inputs = Set.of(
                List.of("empty email", "email", ""),
                List.of("empty email hash", "email_hash", ""),
                List.of("bad email", "email", "abc"),
                List.of("bad email hash", "email_hash", "abc")
        );

        Set<Arguments> args = new HashSet<>();
        for (Operator operator : operators) {
            for (List<String> input : inputs) {
                args.add(Arguments.of(input.get(0), operator, operator.getName(), input.get(1), input.get(2)));
            }
        }
        return args;
    }

    public static Set<Arguments> identityMapBadPhoneArgs() {
        Set<Operator> operators = AppsMap.getApps(Operator.class);
        Set<List<String>> inputs = Set.of(
                List.of("empty phone", "phone", ""),
                List.of("empty phone hash", "phone_hash", ""),
                List.of("bad phone", "phone", "abc"),
                List.of("bad phone hash", "phone_hash", "abc")
        );

        Set<Arguments> args = new HashSet<>();
        for (Operator operator : operators) {
            for (List<String> input : inputs) {
                args.add(Arguments.of(input.get(0), operator, operator.getName(), input.get(1), input.get(2)));
            }
        }
        return args;
    }

    public static Set<Arguments> identityMapBatchEmailArgs() {
        Set<Operator> operators = AppsMap.getApps(Operator.class);
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
                args.add(Arguments.of(input.get(0), operator, operator.getName(), input.get(1)));
            }
        }
        return args;
    }

    public static Set<Arguments> identityMapBatchEmailArgsBadPolicy() {
        Set<Operator> operators = getPublicOperators();
        Set<List<String>> inputs = Set.of(
                List.of("good email list no policy", "{\"email\":[\"user@example.com\",\"user2@example.com\"]}"),
                List.of("good email hash list no policy", "{\"email_hash\":[\"eVvLS/Vg+YZ6+z3i0NOpSXYyQAfEXqCZ7BTpAjFUBUc=\",\"tMmiiTI7IaAcPpQPFQ65uMVCWH8av9jw4cwf/F5HVRQ=\"]}"),
                List.of("empty email list no policy", "{\"email\":[]}"),
                List.of("empty email hash list no policy", "{\"email_hash\":[]}"),
                List.of("good email list policy=0", "{\"email\":[\"user@example.com\",\"user2@example.com\"], \"policy\":0}"),
                List.of("good email hash list policy=0", "{\"email_hash\":[\"eVvLS/Vg+YZ6+z3i0NOpSXYyQAfEXqCZ7BTpAjFUBUc=\",\"tMmiiTI7IaAcPpQPFQ65uMVCWH8av9jw4cwf/F5HVRQ=\"], \"policy\":0}"),
                List.of("empty email list policy=0", "{\"email\":[], \"policy\":0}"),
                List.of("empty email hash list policy=0", "{\"email_hash\":[], \"policy\":0}"),
                List.of("good email list optout_check=0", "{\"email\":[\"user@example.com\",\"user2@example.com\"], \"optout_check\":0}"),
                List.of("good email hash list optout_check=0", "{\"email_hash\":[\"eVvLS/Vg+YZ6+z3i0NOpSXYyQAfEXqCZ7BTpAjFUBUc=\",\"tMmiiTI7IaAcPpQPFQ65uMVCWH8av9jw4cwf/F5HVRQ=\"], \"optout_check\":0}"),
                List.of("empty email list optout_check=0", "{\"email\":[], \"optout_check\":0}"),
                List.of("empty email hash list optout_check=0", "{\"email_hash\":[], \"optout_check\":0}")
        );

        Set<Arguments> args = new HashSet<>();
        for (Operator operator : operators) {
            for (List<String> input : inputs) {
                args.add(Arguments.of(input.get(0), operator, operator.getName(), input.get(1), true));
                args.add(Arguments.of(input.get(0), operator, operator.getName(), input.get(1), false));
            }
        }
        return args;
    }

    public static Set<Arguments> clientSideTokenGenerateArgs() {
        final Set<List<String>> inputs = new HashSet<>();

        inputs.add(List.of("email hash", "{\"email_hash\":\"eVvLS/Vg+YZ6+z3i0NOpSXYyQAfEXqCZ7BTpAjFUBUc=\"}"));

        if (PHONE_SUPPORT) {
            inputs.add(List.of("phone hash", "{\"phone_hash\":\"eVvLS/Vg+YZ6+z3i0NOpSXYyQAfEXqCZ7BTpAjFUBUc=\"}"));
        }

        final Set<Arguments> args = new HashSet<>();
        for (var operator : getPublicOperators()) {
            for (var input : inputs) {
                args.add(Arguments.of(input.get(0), operator, operator.getName(), input.get(1)));
            }
        }
        return args;
    }

    public static Set<Arguments> tokenGenerateEmailArgsBadPolicy() {
        Set<Operator> operators = getPublicOperators();
        Set<List<String>> inputs = Set.of(
                List.of("good email no policy", "{\"email\":\"user@example.com\"}"),
                List.of("good email hash no policy", "{\"email_hash\":\"eVvLS/Vg+YZ6+z3i0NOpSXYyQAfEXqCZ7BTpAjFUBUc=\"}"),
                List.of("good email policy=0", "{\"email\":\"user@example.com\",\"policy\":0}"),
                List.of("good email hash policy=0", "{\"email_hash\":\"eVvLS/Vg+YZ6+z3i0NOpSXYyQAfEXqCZ7BTpAjFUBUc=\",\"policy\":0}"),
                List.of("good email optout_check=0", "{\"email\":\"user@example.com\",\"optout_check\":0}"),
                List.of("good email hash optout_check=0", "{\"email_hash\":\"eVvLS/Vg+YZ6+z3i0NOpSXYyQAfEXqCZ7BTpAjFUBUc=\",\"optout_check\":0}")
        );

        Set<Arguments> args = new HashSet<>();
        for (Operator operator : operators) {
            for (List<String> input : inputs) {
                args.add(Arguments.of(input.get(0), operator, operator.getName(), input.get(1)));
            }
        }
        return args;
    }

    public static Set<Arguments> tokenGeneratePhoneArgsBadPolicy() {
        Set<Operator> operators = getPublicOperators();
        Set<List<String>> inputs = Set.of(
                List.of("good phone no policy", "{\"phone\":\"+1111111111\"}"),
                List.of("good phone hash no policy", "{\"phone_hash\":\"eVvLS/Vg+YZ6+z3i0NOpSXYyQAfEXqCZ7BTpAjFUBUc=\"}"),
                List.of("good phone policy=0", "{\"phone\":\"+1111111111\", \"policy\":0}"),
                List.of("good phone hash policy=0", "{\"phone_hash\":\"eVvLS/Vg+YZ6+z3i0NOpSXYyQAfEXqCZ7BTpAjFUBUc=\", \"policy\":0}"),
                List.of("good phone optout_check=0", "{\"phone\":\"+1111111111\", \"optout_check\":0}"),
                List.of("good phone hash optout_check=0", "{\"phone_hash\":\"eVvLS/Vg+YZ6+z3i0NOpSXYyQAfEXqCZ7BTpAjFUBUc=\", \"optout_check\":0}")
        );

        Set<Arguments> args = new HashSet<>();
        if (PHONE_SUPPORT) {
            for (Operator operator : operators) {
                for (List<String> input : inputs) {
                    args.add(Arguments.of(input.get(0), operator, operator.getName(), input.get(1)));
                }
            }
        }
        return args;
    }

    public static Set<Arguments> identityMapBatchPhoneArgs() {
        Set<Operator> operators = AppsMap.getApps(Operator.class);
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
        if (PHONE_SUPPORT) {
            for (Operator operator : operators) {
                for (List<String> input : inputs) {
                    args.add(Arguments.of(input.get(0), operator, operator.getName(), input.get(1)));
                }
            }
        }
        return args;
    }

    public static Set<Arguments> identityMapBatchPhoneArgsBadPolicy() {
        Set<Operator> operators = getPublicOperators();
        Set<List<String>> inputs = Set.of(
                List.of("good phone list no policy", "{\"phone\":[\"+1111111111\",\"+2222222222\"]}"),
                List.of("good phone hash list no policy", "{\"phone_hash\":[\"eVvLS/Vg+YZ6+z3i0NOpSXYyQAfEXqCZ7BTpAjFUBUc=\",\"tMmiiTI7IaAcPpQPFQ65uMVCWH8av9jw4cwf/F5HVRQ=\"]}"),
                List.of("empty phone list no policy", "{\"phone\":[]}"),
                List.of("empty phone hash list no policy", "{\"phone_hash\":[]}"),
                List.of("good phone list policy=0", "{\"phone\":[\"+1111111111\",\"+2222222222\"], \"policy\":0}"),
                List.of("good phone hash list policy=0", "{\"phone_hash\":[\"eVvLS/Vg+YZ6+z3i0NOpSXYyQAfEXqCZ7BTpAjFUBUc=\",\"tMmiiTI7IaAcPpQPFQ65uMVCWH8av9jw4cwf/F5HVRQ=\"], \"policy\":0}"),
                List.of("empty phone list policy=0", "{\"phone\":[], \"policy\":0}"),
                List.of("empty phone hash list policy=0", "{\"phone_hash\":[], \"policy\":0}"),
                List.of("good phone list optout_check=0", "{\"phone\":[\"+1111111111\",\"+2222222222\"], \"optout_check\":0}"),
                List.of("good phone hash list optout_check=0", "{\"phone_hash\":[\"eVvLS/Vg+YZ6+z3i0NOpSXYyQAfEXqCZ7BTpAjFUBUc=\",\"tMmiiTI7IaAcPpQPFQ65uMVCWH8av9jw4cwf/F5HVRQ=\"], \"optout_check\":0}"),
                List.of("empty phone list optout_check=0", "{\"phone\":[], \"optout_check\":0}"),
                List.of("empty phone hash list optout_check=0", "{\"phone_hash\":[], \"optout_check\":0}")
        );

        Set<Arguments> args = new HashSet<>();
        if (PHONE_SUPPORT) {
            for (Operator operator : operators) {
                for (List<String> input : inputs) {
                    args.add(Arguments.of(input.get(0), operator, operator.getName(), input.get(1), true));
                    args.add(Arguments.of(input.get(0), operator, operator.getName(), input.get(1), false));
                }
            }
        }
        return args;
    }

    public static Set<Arguments> identityMapBatchBadEmailArgs() {
        Set<Operator> operators = AppsMap.getApps(Operator.class);
        Set<List<String>> inputs = Set.of(
                List.of("bad email list policy=1", "{\"email\":[\"abc\",\"user2@example.com\"], \"policy\":1}"),
                List.of("bad email hash list policy=1", "{\"email_hash\":[\"eVvLS/Vg+YZ6+z3i0NOpSXYyQAfEXqCZ7BTpAjFUBUc=\",\"abc\"], \"policy\":1}"),
                List.of("bad email list optout_check=1", "{\"email\":[\"abc\",\"user2@example.com\"], \"optout_check\":1}"),
                List.of("bad email hash list optout_check=1", "{\"email_hash\":[\"eVvLS/Vg+YZ6+z3i0NOpSXYyQAfEXqCZ7BTpAjFUBUc=\",\"abc\"], \"optout_check\":1}")
        );

        Set<Arguments> args = new HashSet<>();
        for (Operator operator : operators) {
            for (List<String> input : inputs) {
                args.add(Arguments.of(input.get(0), operator, operator.getName(), input.get(1)));
            }
        }
        return args;
    }

    public static Set<Arguments> identityMapBatchBadPhoneArgs() {
        Set<Operator> operators = AppsMap.getApps(Operator.class);
        Set<List<String>> inputs = Set.of(
                List.of("bad phone list policy=1", "{\"phone\":[\"+1111111111\",\"abc\"], \"policy\":1}"),
                List.of("bad phone hash list policy=1", "{\"phone_hash\":[\"abc\",\"tMmiiTI7IaAcPpQPFQ65uMVCWH8av9jw4cwf/F5HVRQ=\"], \"policy\":1}"),
                List.of("bad phone list optout_check=1", "{\"phone\":[\"+1111111111\",\"abc\"], \"optout_check\":1}"),
                List.of("bad phone hash list optout_check=1", "{\"phone_hash\":[\"abc\",\"tMmiiTI7IaAcPpQPFQ65uMVCWH8av9jw4cwf/F5HVRQ=\"], \"optout_check\":1}")
        );

        Set<Arguments> args = new HashSet<>();
        if (PHONE_SUPPORT) {
            for (Operator operator : operators) {
                for (List<String> input : inputs) {
                    args.add(Arguments.of(input.get(0), operator, operator.getName(), input.get(1)));
                }
            }
        }
        return args;
    }

    public static Set<Arguments> identityBucketsArgs() {
        Set<Operator> operators = AppsMap.getApps(Operator.class);
        Set<List<String>> inputs = Set.of(
                List.of("good timestamp", "{\"since_timestamp\":\"2021-01-01T00:00:00\"}")
        );

        Set<Arguments> args = new HashSet<>();
        for (Operator operator : operators) {
            for (List<String> input : inputs) {
                args.add(Arguments.of(input.get(0), operator, operator.getName(), input.get(1)));
            }
        }
        return args;
    }

    private static Set<Operator> getPublicOperators() {
        return AppsMap.getApps(Operator.class).stream()
                .filter(s -> s.getType() != Operator.Type.PRIVATE)
                .collect(Collectors.toSet());
    }
}
