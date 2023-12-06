package suite.logout;

import app.AppsMap;
import app.common.Mapper;
import app.component.Operator;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

// Tests in this class are currently run manually
@SuppressWarnings("unused")
public class AfterLogoutTest {
    @ParameterizedTest(name = "/v0/token/refresh after {3} generate and {4} logout - {0}")
    @MethodSource({
            "refreshTokenArgs"
    })
    public void testV0TokenRefresh(String label, Operator operator, String operatorName, String tokenGenerateVersion, String tokenLogoutVersion, String refreshToken, String refreshResponseKey) throws Exception {
        assumeThat(tokenGenerateVersion).isEqualTo("v0");

        JsonNode response = operator.v0TokenRefresh(refreshToken);

        assertThat(response).isEqualTo(Mapper.OBJECT_MAPPER.readTree("{\"advertisement_token\":\"\",\"advertising_token\":\"\",\"refresh_token\":\"\"}"));
    }

    @ParameterizedTest(name = "/v1/token/refresh after {3} generate and {4} logout - {0}")
    @MethodSource({
            "refreshTokenArgs"
    })
    public void testV1TokenRefresh(String label, Operator operator, String operatorName, String tokenGenerateVersion, String tokenLogoutVersion, String refreshToken, String refreshResponseKey) throws Exception {
        assumeThat(tokenGenerateVersion).isEqualTo("v1");

        JsonNode response = operator.v1TokenRefresh(refreshToken);

        assertThat(response).isEqualTo(Mapper.OBJECT_MAPPER.readTree("{\"status\":\"optout\"}"));
    }

    @ParameterizedTest(name = "/v2/token/refresh after {3} generate and {4} logout - {0}")
    @MethodSource({
            "refreshTokenArgs"
    })
    public void testV2TokenRefresh(String label, Operator operator, String operatorName, String tokenGenerateVersion, String tokenLogoutVersion, String refreshToken, String refreshResponseKey) throws Exception {
        assumeThat(tokenGenerateVersion).isEqualTo("v2");

        JsonNode response = operator.v2TokenRefresh(refreshToken, refreshResponseKey);

        assertThat(response).isEqualTo(Mapper.OBJECT_MAPPER.readTree("{\"status\":\"optout\"}"));
    }

    private static Set<Arguments> refreshTokenArgs() {
        Set<Operator> operators = AppsMap.getApps(Operator.class);
        Set<List<String>> refreshTokens = Set.of(
                // Copy and paste BeforeLogoutTest output here
                // WARNING: DO NOT COMMIT ANYTHING PASTED INTO THIS SET
        );

        Set<Arguments> args = new HashSet<>();
        for (Operator operator : operators) {
            for (List<String> refreshToken : refreshTokens) {
                String operatorNameInTokenGenerate = refreshToken.get(1);
                if (!operatorNameInTokenGenerate.equals(operator.getName())) {
                    continue;
                }

                args.add(Arguments.of(refreshToken.get(0), operator, refreshToken.get(1), refreshToken.get(2), refreshToken.get(3), refreshToken.get(4), refreshToken.get(5)));
            }
        }
        return args;
    }
}
