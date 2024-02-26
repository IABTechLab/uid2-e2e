package app.component;

import app.common.EnvUtil;
import app.common.HttpClient;

public class Core extends App {
    private static final String CORE_API_TOKEN = EnvUtil.getEnv("core_api_token");
    public static final String CORE_URL = EnvUtil.getEnv("CORE_URL");
    public static final String OPTOUT_URL = EnvUtil.getEnv("OPTOUT_URL");

    public Core(String host, Integer port, String name) {
        super(host, port, name);
    }

    public String attest(String attestationRequest) throws Exception {
        String response = HttpClient.post(getBaseUrl() + "/attest", attestationRequest, CORE_API_TOKEN);
        return response;
    }

}
