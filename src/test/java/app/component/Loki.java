package app.component;

import app.common.HttpClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Loki extends App {
    public Loki(String host, Integer port, String name) {
        super(host, port, name);
    }

    public String getLogs(String app) throws Exception {
        return HttpClient.get(getBaseUrl()
                + "/loki/api/v1/query_range?query="
                + URLEncoder.encode("{app=\"%s\"}".formatted(app), StandardCharsets.UTF_8)
                +"&limit=5000"
        );
    }

    @Override
    public boolean isHealthy() throws Exception {
        return HttpClient.get(getBaseUrl() + "/ready").contains("ready");
    }
}
