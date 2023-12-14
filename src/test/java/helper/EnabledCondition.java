package helper;

import app.common.EnvUtil;

public final class EnabledCondition {
    private static final String ENV = EnvUtil.getEnv("UID2_E2E_ENV");

    private EnabledCondition() {
    }

    public static boolean isLocal() {
        return ENV.contains("local");
    }
}
