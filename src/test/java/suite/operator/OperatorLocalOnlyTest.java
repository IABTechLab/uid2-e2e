package suite.operator;

import app.component.App;
import app.component.Operator;
import com.uid2.client.*;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.security.SecureRandom;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnabledIf("common.EnabledCondition#isLocal")
public class OperatorLocalOnlyTest {
    @ParameterizedTest(name = "{index} ==> Sender {0} encrypts with {1}, recipient {2} decrypts with {3}, expected result is {4}")
    @MethodSource({
            "suite.operator.TestData#sharingArgs",
    })
    public void testSharing(TestData.Client sender, Operator senderOperator, TestData.Client recipient, Operator recipientOperator, DecryptionStatus expectedDecryptionStatus) throws Exception {
        assumeThat(senderOperator).isNotNull();
        assumeThat(recipientOperator).isNotNull();

        final var rawUidBytes = new byte[33];
        new SecureRandom().nextBytes(rawUidBytes);
        rawUidBytes[0] = 0;

        final var rawUid = Base64.getEncoder().encodeToString(rawUidBytes);

        final var senderClient = new UID2Client(
                senderOperator.getBaseUrl(),
                sender.apiKey(),
                sender.apiSecret(),
                App.IDENTITY_SCOPE
        );

        final var recipientClient = new UID2Client(
                recipientOperator.getBaseUrl(),
                recipient.apiKey(),
                recipient.apiSecret(),
                App.IDENTITY_SCOPE
        );

        senderClient.refresh();
        final var encrypted = senderClient.encrypt(rawUid);
        assertTrue(encrypted.isSuccess());

        recipientClient.refresh();
        final var decrypted = recipientClient.decrypt(encrypted.getEncryptedData());
        final var expectedRawUid = expectedDecryptionStatus == DecryptionStatus.SUCCESS ? rawUid : null;

        assertAll(
                () -> assertThat(decrypted.getStatus()).isEqualTo(expectedDecryptionStatus),
                () -> assertThat(decrypted.getUid()).isEqualTo(expectedRawUid)
        );
    }

    @ParameterizedTest(name = "/v2/token/generate - LOCAL MOCK OPTOUT - {0} - {2}")
    @MethodSource({
            "suite.operator.TestData#tokenEmailArgsLocalMockOptout"
    })
    public void testV2TokenGenerateLocalMockOptout(String label, Operator operator, String operatorName, String type, String identity) {
        TokenGenerateResponse tokenGenerateResponse = operator.v2TokenGenerate(type, identity);
        IdentityTokens currentIdentity = tokenGenerateResponse.getIdentity();

        assertThat(currentIdentity).isNull();
    }
}
