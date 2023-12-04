package app;

import app.common.EnvUtil;
import app.component.App;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class AppsMap {
    private static final Map<String, String> APP_MAP;
    private static final String ENV = EnvUtil.getEnv("UID2_E2E_ENV");

    private static final Apps APPS;

    static {
        try {
            APP_MAP = Map.of(
                    "local", "app.LocalApps",
                    "local-public", "app.LocalPublicApps",
                    "local-private", "app.LocalPrivateApps",
                    "uid2-integ", "app.Uid2IntegApps",
                    "uid2-prod", "app.Uid2ProdApps",
                    "euid-integ", "app.EuidIntegApps",
                    "euid-prod", "app.EuidProdApps",
                    "github-test-pipeline", "app.GitHubTestPipelineApps"
            );

            APPS = (Apps) Class.forName(APP_MAP.get(ENV)).getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private AppsMap() {
    }

    public static Set<App> getApps() {
        return APPS.getApps();
    }

    public static <T extends App> Set<T> getApps(Class<T> clazz) {
        return APPS.getApps().stream()
                .filter(app -> clazz.equals(app.getClass()))
                .map(clazz::cast)
                .collect(Collectors.toUnmodifiableSet());
    }
}
