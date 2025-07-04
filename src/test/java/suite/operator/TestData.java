package suite.operator;

import app.AppsMap;
import app.component.App;
import app.component.Operator;
import com.uid2.client.DecryptionStatus;
import com.uid2.client.IdentityMapInput;
import com.uid2.client.IdentityMapV3Input;
import org.junit.jupiter.params.provider.Arguments;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Named.named;

public final class TestData {
    public static final int RAW_UID2_LENGTH = 44;
    private static final Random RANDOM = new Random();

    private TestData() {
    }

    public record Client(String apiKey, String apiSecret) {
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
        if (App.PHONE_SUPPORT) {
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
        if (App.PHONE_SUPPORT) {
            for (Operator operator : operators) {
                args.add(Arguments.of("optout special phone", operator, operator.getName(), "phone", "+00000000000", true));
                args.add(Arguments.of("optout special phone", operator, operator.getName(), "phone", "+00000000000", false));
            }
        }
        return args;
    }

    public static Set<Arguments> tokenPhoneArgsSpecialRefreshOptout() {
        Set<Operator> operators = AppsMap.getApps(Operator.class);

        Set<Arguments> args = new HashSet<>();
        if (App.PHONE_SUPPORT) {
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
        if (App.PHONE_SUPPORT) {
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
        if (App.PHONE_SUPPORT) {
            for (Operator operator : operators) {
                for (List<String> input : inputs) {
                    args.add(Arguments.of(input.get(0), operator, operator.getName(), input.get(1), input.get(2)));
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
        if (App.PHONE_SUPPORT) {
            for (Operator operator : operators) {
                for (List<String> input : inputs) {
                    args.add(Arguments.of(input.get(0), operator, operator.getName(), input.get(1)));
                }
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

    public static Set<Arguments> identityMapArgs() {
        Set<Operator> operators = AppsMap.getApps(Operator.class);

        List<String> emails = new ArrayList<>();
        List<String> phones = new ArrayList<>();
        List<String> emailHashes = new ArrayList<>();
        List<String> phoneHashes = new ArrayList<>();
        for (int i = 0; i < 10_000; i++) {
            emails.add(randomEmail());
            phones.add(randomPhoneNumber());
            emailHashes.add(randomHash());
            phoneHashes.add(randomHash());
        }

        Set<Arguments> args = new HashSet<>();
        for (Operator operator : operators) {
            args.add(Arguments.of("10k emails", operator, operator.getName(), IdentityMapInput.fromEmails(emails), emails));
            args.add(Arguments.of("10k phones", operator, operator.getName(), IdentityMapInput.fromPhones(phones), phones));
            args.add(Arguments.of("10k email hashes", operator, operator.getName(), IdentityMapInput.fromHashedEmails(emailHashes), emailHashes));
            args.add(Arguments.of("10k phone hashes", operator, operator.getName(), IdentityMapInput.fromHashedPhones(phoneHashes), phoneHashes));
        }
        return args;
    }

    public static Set<Arguments> identityMapV3Args() {
        Set<Operator> operators = AppsMap.getApps(Operator.class);

        List<String> emails = new ArrayList<>();
        List<String> phones = new ArrayList<>();
        List<String> emailHashes = new ArrayList<>();
        List<String> phoneHashes = new ArrayList<>();
        List<String> mixedDIIs = new ArrayList<>();

        IdentityMapV3Input emailInput = IdentityMapV3Input.fromEmails(emails);
        IdentityMapV3Input phoneInput = IdentityMapV3Input.fromPhones(phones);
        IdentityMapV3Input emailHashInput = IdentityMapV3Input.fromHashedEmails(emailHashes);
        IdentityMapV3Input phoneHashInput = IdentityMapV3Input.fromHashedPhones(phoneHashes);
        IdentityMapV3Input mixedInput = IdentityMapV3Input.fromHashedPhones(phoneHashes);
        for (int i = 0; i < 10_000; i++) {
            String email = randomEmail();
            String phone = randomPhoneNumber();
            String emailHash = randomHash();
            String phoneHash = randomHash();

            emails.add(email);
            phones.add(phone);
            emailHashes.add(emailHash);
            phoneHashes.add(phoneHash);

            emailInput.withEmail(email);
            phoneInput.withPhone(phone);
            emailHashInput.withHashedEmail(emailHash);
            phoneHashInput.withHashedPhone(phoneHash);

            if (i < 2_500) { // all 4 DII types in the same collection, so we only need 2.5k of each
                mixedDIIs.add(email);
                mixedDIIs.add(phone);
                mixedDIIs.add(emailHash);
                mixedDIIs.add(phoneHash);

                mixedInput.withEmail(email).withPhone(phone).withHashedEmail(emailHash).withHashedPhone(phoneHash);
            }

        }

        Set<Arguments> args = new HashSet<>();
        for (Operator operator : operators) {
            args.add(Arguments.of("10k emails", operator, operator.getName(), emailInput, emails));
            args.add(Arguments.of("10k phones", operator, operator.getName(), phoneInput, phones));
            args.add(Arguments.of("10k email hashes", operator, operator.getName(), emailHashInput, emailHashes));
            args.add(Arguments.of("10k phone hashes", operator, operator.getName(), phoneHashInput, phoneHashes));
            args.add(Arguments.of("10k mixed DIIs", operator, operator.getName(), mixedInput, mixedDIIs));
        }
        return args;
    }

    public static Set<Arguments> identityMapV3BatchBadEmailArgs() {
        Set<Operator> operators = AppsMap.getApps(Operator.class);
        Set<List<String>> inputs = Set.of(
                List.of("bad email", "{\"email\":[\"abc\"], \"email_hash\":[], \"phone\":[], \"phone_hash\":[]}", "email"),
                List.of("bad email hash", "{\"email\":[], \"email_hash\":[\"abc\"], \"phone\":[], \"phone_hash\":[]}", "email_hash")
        );

        Set<Arguments> args = new HashSet<>();
        for (Operator operator : operators) {
            for (List<String> input : inputs) {
                args.add(Arguments.of(input.get(0), operator, operator.getName(), input.get(1), input.get(2)));
            }
        }
        return args;
    }

    public static Set<Arguments> identityMapV3BatchBadPhoneArgs() {
        Set<Operator> operators = AppsMap.getApps(Operator.class);
        Set<List<String>> inputs = Set.of(
                List.of("bad phone", "{\"email\":[], \"email_hash\":[], \"phone\":[\"abc\"], \"phone_hash\":[]}", "phone"),
                List.of("bad phone hash", "{\"email\":[], \"email_hash\":[], \"phone\":[], \"phone_hash\":[\"abc\"]}", "phone_hash")
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


    private static String randomEmail() {
        return "email_" + Math.abs(RANDOM.nextLong()) + "@example.com";
    }

    private static String randomPhoneNumber() {
        // Phone numbers with 15 digits are technically valid but are not used in any country
        return "+" + String.format("%015d", Math.abs(RANDOM.nextLong() % 1_000_000_000_000_000L));
    }

    private static String randomHash() {
        // This isn't really a hashed DII but looks like one to UID2
        byte[] randomBytes = new byte[32];
        RANDOM.nextBytes(randomBytes);
        return Base64.getEncoder().encodeToString(randomBytes);
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

        if (App.PHONE_SUPPORT) {
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
        if (App.PHONE_SUPPORT) {
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
        if (App.PHONE_SUPPORT) {
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
        if (App.PHONE_SUPPORT) {
            for (Operator operator : operators) {
                for (List<String> input : inputs) {
                    args.add(Arguments.of(input.get(0), operator, operator.getName(), input.get(1), true));
                    args.add(Arguments.of(input.get(0), operator, operator.getName(), input.get(1), false));
                }
            }
        }
        return args;
    }

    public static Set<Arguments> identityMapBadInputArgs() {
        Set<Operator> operators = AppsMap.getApps(Operator.class);

        List<String> badEmails = List.of("abc", "user2@example.com");
        List<String> badEmailHashes = List.of("eVvLS/Vg+YZ6+z3i0NOpSXYyQAfEXqCZ7BTpAjFUBUc=", "abc");

        Set<Arguments> args = new HashSet<>();
        for (Operator operator : operators) {
            args.add(Arguments.of("bad emails", operator, operator.getName(), IdentityMapInput.fromEmails(badEmails), badEmails));
            args.add(Arguments.of("bad email hashes", operator, operator.getName(), IdentityMapInput.fromHashedEmails(badEmailHashes), badEmailHashes));
        }

        List<String> badPhones = List.of("+1111111111", "abc");
        List<String> badPhoneHashes = List.of("abc", "tMmiiTI7IaAcPpQPFQ65uMVCWH8av9jw4cwf/F5HVRQ=");

        if (App.PHONE_SUPPORT) {
            for (Operator operator : operators) {
                args.add(Arguments.of("bad phones", operator, operator.getName(), IdentityMapInput.fromPhones(badPhones), badPhones));
                args.add(Arguments.of("bad phone hashes", operator, operator.getName(), IdentityMapInput.fromHashedPhones(badPhoneHashes), badPhoneHashes));
            }
        }
        return args;
    }

    public static Set<Arguments> identityMapV3BadInputArgs() {
        Set<Operator> operators = AppsMap.getApps(Operator.class);

        List<String> badEmailHashes = List.of("eVvLS/Vg+YZ6+z3i0NOpSXYyQAfEXqCZ7BTpAjFUBUc=", "abc");

        Set<Arguments> args = new HashSet<>();
        for (Operator operator : operators) {
            args.add(Arguments.of("bad email hashes", operator, operator.getName(), IdentityMapV3Input.fromHashedEmails(badEmailHashes), badEmailHashes));
        }

        List<String> badPhoneHashes = List.of("abc", "tMmiiTI7IaAcPpQPFQ65uMVCWH8av9jw4cwf/F5HVRQ=");

        if (App.PHONE_SUPPORT) {
            for (Operator operator : operators) {
                args.add(Arguments.of("bad phone hashes", operator, operator.getName(), IdentityMapV3Input.fromHashedPhones(badPhoneHashes), badPhoneHashes));
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

    public static Stream<Arguments> sharingArgs() {
        var privateOperator = named("PRIVATE OPERATOR", getOperator(Operator.Type.PRIVATE).orElse(null));
        var publicOperator = named("PUBLIC OPERATOR", getOperator(Operator.Type.PUBLIC).orElse(null));

        var client = named("CLIENT", new Client(Operator.CLIENT_API_KEY, Operator.CLIENT_API_SECRET));
        var sharingRecipient = named("SHARING RECIPIENT", new Client(Operator.CLIENT_API_KEY_SHARING_RECIPIENT, Operator.CLIENT_API_SECRET_SHARING_RECIPIENT));
        var nonSharingRecipient = named("NON-SHARING RECIPIENT", new Client(Operator.CLIENT_API_KEY_NON_SHARING_RECIPIENT, Operator.CLIENT_API_SECRET_NON_SHARING_RECIPIENT));

        return Stream.of(
                // CLIENT shares with CLIENT.
                Arguments.of(client, privateOperator, client, privateOperator, DecryptionStatus.SUCCESS),
                Arguments.of(client, privateOperator, client, publicOperator, DecryptionStatus.SUCCESS),
                Arguments.of(client, publicOperator, client, privateOperator, DecryptionStatus.SUCCESS),
                Arguments.of(client, publicOperator, client, publicOperator, DecryptionStatus.SUCCESS),

                // CLIENT shares with SHARING RECIPIENT.
                // Private operator only has site data for CLIENT, so SHARING RECIPIENT must decrypt using public operator.
                Arguments.of(client, privateOperator, sharingRecipient, publicOperator, DecryptionStatus.SUCCESS),
                Arguments.of(client, publicOperator, sharingRecipient, publicOperator, DecryptionStatus.SUCCESS),

                // CLIENT does not share with NON-SHARING RECIPIENT.
                // Private operator only has site data for CLIENT, so NON-SHARING RECIPIENT must decrypt using public operator.
                Arguments.of(client, privateOperator, nonSharingRecipient, publicOperator, DecryptionStatus.NOT_AUTHORIZED_FOR_KEY),
                Arguments.of(client, publicOperator, nonSharingRecipient, publicOperator, DecryptionStatus.NOT_AUTHORIZED_FOR_KEY),

                // SHARING RECIPIENT shares with CLIENT.
                // Private operator only has site data for CLIENT, so SHARING RECIPIENT must encrypt using public operator.
                Arguments.of(sharingRecipient, publicOperator, client, privateOperator, DecryptionStatus.SUCCESS),
                Arguments.of(sharingRecipient, publicOperator, client, publicOperator, DecryptionStatus.SUCCESS),

                // NON-SHARING RECIPIENT does not share with CLIENT.
                // Private operator only has site data for CLIENT, so NON-SHARING RECIPIENT must encrypt using public operator.
                Arguments.of(nonSharingRecipient, publicOperator, client, publicOperator, DecryptionStatus.NOT_AUTHORIZED_FOR_KEY),
                Arguments.of(nonSharingRecipient, publicOperator, client, privateOperator, DecryptionStatus.NOT_AUTHORIZED_FOR_KEY)
        );
    }

    private static Optional<Operator> getOperator(Operator.Type type) {
        return AppsMap.getApps(Operator.class)
                .stream()
                .filter(operator -> operator.getType() == type)
                .findFirst();
    }

    private static Set<Operator> getPublicOperators() {
        return AppsMap.getApps(Operator.class).stream()
                .filter(s -> s.getType() != Operator.Type.PRIVATE)
                .collect(Collectors.toSet());
    }
}
