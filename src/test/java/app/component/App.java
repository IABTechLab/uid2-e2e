package app.component;

import common.HttpClient;

public abstract class App {
    private final String host;
    private final Integer port;
    private final String name;

    public App(String host, Integer port, String name) {
        this.host = host;
        this.port = port;
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public String getName() {
        return name;
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
