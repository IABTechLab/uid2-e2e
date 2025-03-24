package app.component;

import com.uid2.client.IdentityScope;
import common.Const;
import common.EnvUtil;
import common.HttpClient;
import lombok.Getter;

@Getter
public abstract class App {
    public static final String ENV = EnvUtil.getEnv(Const.Config.ENV);
    public static final IdentityScope IDENTITY_SCOPE = IdentityScope.valueOf(EnvUtil.getEnv(Const.Config.IDENTITY_SCOPE));
    public static final boolean PHONE_SUPPORT = Boolean.parseBoolean(EnvUtil.getEnv(Const.Config.PHONE_SUPPORT));

    private final String host;
    private final Integer port;
    private final String name;

    public App(String host, Integer port, String name) {
        this.host = host;
        this.port = port;
        this.name = name;
    }

    public String getBaseUrl() {
        return getPort() == null ? getHost() : "%s:%d".formatted(getHost(), getPort());
    }

    public boolean isHealthy() throws Exception {
        return HttpClient.get(getBaseUrl() + "/ops/healthcheck").equals("OK");
    }

    @Override
    public String toString() {
        return getName();
    }
}
