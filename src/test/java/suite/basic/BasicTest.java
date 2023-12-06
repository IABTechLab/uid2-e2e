package suite.basic;

import app.AppsMap;
import app.component.App;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("unused")
public class BasicTest {
    @ParameterizedTest(name = "Healthcheck - {1}")
    @MethodSource({
            "args"
    })
    public void testHealthcheck(App app, String name) {
        try {
            assertThat(app.isHealthy()).isTrue();
        } catch (Exception e) {
            Assertions.fail("Failed to get health check for " + app.getName(), e);
        }
    }

    private static Set<Arguments> args() {
        Set<App> apps = AppsMap.getApps();

        Set<Arguments> args = new HashSet<>();
        for (App app : apps) {
            args.add(Arguments.of(app, app.getName()));
        }
        return args;
    }
}
