package helper;

import app.common.EnvUtil;

public final class DisableCondition {
    private static final String ENV = EnvUtil.getEnv("UID2_E2E_ENV");

    private DisableCondition() {
    }

    public static boolean isLocal() {
        return ENV.contains("local");
    }
}
