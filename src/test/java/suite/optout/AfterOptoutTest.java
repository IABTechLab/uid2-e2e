package suite.optout;

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
public class AfterOptoutTest {
    @ParameterizedTest(name = "/v0/token/refresh after {3} generate and {4} logout - {0} - {2}")
    @MethodSource({
            "refreshTokenArgs"
    })
    public void testV0TokenRefresh(String label, Operator operator, String operatorName, String tokenGenerateVersion, String tokenLogoutVersion, String refreshToken, String refreshResponseKey) throws Exception {
        assumeThat(tokenGenerateVersion).isEqualTo("v0");

        JsonNode response = operator.v0TokenRefresh(refreshToken);

        assertThat(response).isEqualTo(Mapper.OBJECT_MAPPER.readTree("{\"advertisement_token\":\"\",\"advertising_token\":\"\",\"refresh_token\":\"\"}"));
    }

    @ParameterizedTest(name = "/v1/token/refresh after {3} generate and {4} logout - {0} - {2}")
    @MethodSource({
            "refreshTokenArgs"
    })
    public void testV1TokenRefresh(String label, Operator operator, String operatorName, String tokenGenerateVersion, String tokenLogoutVersion, String refreshToken, String refreshResponseKey) throws Exception {
        assumeThat(tokenGenerateVersion).isEqualTo("v1");

        JsonNode response = operator.v1TokenRefresh(refreshToken);

        assertThat(response).isEqualTo(Mapper.OBJECT_MAPPER.readTree("{\"status\":\"optout\"}"));
    }

    @ParameterizedTest(name = "/v2/token/refresh after {3} generate and {4} logout - {0} - {2}")
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
                // Copy and paste BeforeOptoutTest output here
                // WARNING: DO NOT COMMIT ANYTHING PASTED INTO THIS SET
                List.of("old participant good email", "Public Operator", "v2", "v2", "AAAAAALQH/fTAriLtt7afAgIUkxaNyAHVvXT2bq9Uj8IrsYOdWJQ6Jo5e1y2Kt+PN/uIZlmBhC/HTYIoTyr4JfAw1lzHwFGCvF9qEM6kEdFPD6Mf9hqs1XqGKn2SBIXsCJBIPV1UkmzAbrFEqXgnaw5Vtga+aCKOnGcfdb5rqgNyuJZd1KfXXN+T+JApfWaWqGszIDhbD9p1BNH8ANSAzTHHhZONu2A/UHrvSqB1ezzTePpqT3rjQu6sYiWp8aQffenYz9LgL4n9q8Y0CXXU33gTF+bB2B2Tr0xwbsf//VKd1yfcBKE80d2cBlxnD1rXe+CCSTwfTRKKCD7byb0/FgCKw/Rq8HlfJLAX9qLdzs/HqZLbKNcVQEzxv2y5VP5Sbb8R", "CAoGJvsMpY5aE/w4hL5uTt9rbvSLsB8KltnrZiogvPs="),
                List.of("old participant good phone", "Public Operator", "v2", "v2", "AAAAAAJ7XplHNqliX33AY0lyp0qaZqEDNQn7uu7gmccMNLLHfbZ5Xw4JEx8k1earXm2zO2BhHNTiEibHmsf0Mt6IPBGKT9wPLyr/a1jBFGenDH/4ImzKoEWBGpLwRoLVk6JqS7qPHKYTSz5GDJguuCFs+o6lUn5Ugcrpmy0kY3S+/uvfN17/07SLT1aH8bufTjve71+J500ytuzPwyKW8qgxIe+KfMAuhwb2Svs+xQJm6EhHZ0UQQ0tkmefv6BAWNQCFZR008OT3a0NvimDO5jc5CX0ETWGo9kGfRl07I905H8kcCW+oDaHD/DMF3M1llDzS6laJTeJDBzOolvu+nNCeSudwwYG2i8UPIkdHmHT5DXsVo/NOYK0QXGil5CV2zdIO", "VYmdH2PGzo137ewT1u+NAlq09KMWxrPRf45HCi+R2n0="),
                List.of("good email", "Public Operator", "v2", "v2", "AAAAAAK1/5BChOvErqyK5GRyG6jc5lz9zKPXHmsqC5me2zAW2YwDyH+p1tZW7Ou+61WkQEz2JbLcm7+dUOpFlBJDNT4xM1S9B/+5+BqDD+h3ZPpOmiVW7LYRdNsEoMx1Cb1bDcIlhsyC92OcMzY+hR/FpKJO+JSwRmX4kWWNufF9TyBh3BF25qPsP56RNTjoYygBRip7NOOekuKBsYnvTlqmO0HyF85zKKPOU6jqI2Fs3saL7zK7Pbg2uk8NMKSTWvCw/ewUOuUpe30gH8FJmT4zLnua7Zh18gSJCxaci9ApbZuTr6u0Pz/VuHukRBOebc83q/R3pWPEASf2rkT+jjaAUdZYCDf8yRbx18v1SaRhWq0aM2BwEDV+HaxMBqZzA4vo", "Mi2WNSLq+m+2FksgzWcCaGsnvZRM3C4VDEbmtTIch48="),
                List.of("good phone", "Public Operator", "v2", "v2", "AAAAAAI5uGKsIlsyIar2VXdP6kSgv0xYpadTUoTAcKk+/Arq2gJIgY+FWoAZGlQC5JCoZuH6L9yu/dVy46/D37++iTIvYp3SBqv/C4IbWdEK5HX47qHOwrUskCbigTIR4g/1Rr7hfy6p8rYaty5i2bJoMk263ihICDtwtF58y1mshJgQInfFldLzzhZHLZryfp/dyTjVO5jWeCQVKO3GzWpAc8XJHyeFHXa+WlVZRzfuIzRxXosk+RhLzdkjRSQv2rbMvaZdH0ohCNua92foEmxtIqhba2nNvjUEh2JGVtscNlsYcxhmfkoZwv6K/fm+qXxMUUzDiHaJLPnMmTkDbsHXBKpuOmsx8zJ3zMEXXFybTowHcNQrAnA6f3x3l6YjHN4J", "HRhlAeOHEzj+Eqy/KsvJh46zO+KoabpRYGoWqNvrXz0="),
                List.of("good email", "Public Operator", "v1", "v2", "A3AAAAACoQQw2iCTGPM3PE7PgObBXjM3mIwA6v9XeQoB1OyNOu7RorKXNBpXfrPrruAWO/EnIsqxHkhomETrSbFAHDelJKnC9Dpw8Fa3NiFHYn6r8kDf3YlhoR6iZEjOQ6sIApO9WVaJS30OOVY1dUIju+qq+zVHCSZXEA==", ""),
                List.of("good phone", "Public Operator", "v1", "v2", "B3AAAAACBML46ergcjBi5avygycspmJM4VO50k7HBsifeqksgfWbQouRKjXi8a9AGafpD48X4zoyGalAF4sgKvLhLBxsud5frLr5GcyrDjGLgkk0rsCw3EZ1AA/fmI4B549dXxQkH7b1I32280M3ZXnPRO9ltnjkRIcBlw==", ""),
                List.of("good email", "Public Operator", "v0", "v2", "A3AAAAACYQHWg9f2RmxXUq1bTDp2U1MgLe3od72uxlS7c0RSC94p5+/OB7pkjyFMEFkS5l/gqDHzWRMzn4tsgpHFTx0aGCYrZH7/1NgGqPqmlfgurLB1J6p8IRmuxUPJFuD7Z2bJBpXbkn/4Zs3n/wDGA6CGa46dZ3SpwA==", "")
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
