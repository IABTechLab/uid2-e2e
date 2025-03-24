package app.component;

import common.HttpClient;

public class Prometheus extends App {
    public Prometheus(String host, Integer port, String name) {
        super(host, port, name);
    }

    public String getMetrics() throws Exception {
        return HttpClient.get(getBaseUrl() + "/metrics");
    }

    @Override
    public boolean isHealthy() throws Exception {
        return !getMetrics().isBlank();
    }
}
