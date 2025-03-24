package common;

public final class EnabledCondition {
    private static final String ENV = EnvUtil.getEnv(Const.Config.ENV);

    private EnabledCondition() {
    }

    public static boolean isLocal() {
        return ENV.contains("local");
    }
}
