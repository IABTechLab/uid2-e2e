package app.component;

import app.common.HttpClient;

public class Localstack extends App {
    public Localstack(String host, Integer port, String name) {
        super(host, port, name);
    }

    @Override
    public boolean isHealthy() throws Exception {
        return HttpClient.get(getBaseUrl() + "/test-core-bucket/").contains("<Name>test-core-bucket</Name>");
    }
}
