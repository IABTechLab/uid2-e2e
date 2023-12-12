package suite.operator;


import app.component.Operator;
import com.uid2.client.DecryptionResponse;
import com.uid2.client.IdentityTokens;
import com.uid2.client.TokenGenerateResponse;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("unused")
public class V2ApiOperatorLocalOnlyTest {
    @ParameterizedTest(name = "/v2/token/generate - LOCAL MOCK OPTOUT - {0} - {2}")
    @MethodSource({
            "suite.operator.TestData#tokenEmailArgsLocalMockOptout"
    })
    public void testV2TokenGenerateLocalMockOptout(String label, Operator operator, String operatorName, String type, String identity) {
        TokenGenerateResponse tokenGenerateResponse = operator.v2TokenGenerate(type, identity, false);
        IdentityTokens currentIdentity = tokenGenerateResponse.getIdentity();

        assertThat(currentIdentity).isNull();
    }
}
