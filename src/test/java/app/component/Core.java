package app.component;

import app.common.EnvUtil;
import app.common.HttpClient;

public class Core extends App {
    private static final String CORE_API_TOKEN = EnvUtil.getEnv("UID2_E2E_CORE_API_TOKEN");
    public static final String CORE_URL = EnvUtil.getEnv("UID2_E2E_CORE_URL");
    public static final String OPTOUT_URL = EnvUtil.getEnv("UID2_E2E_OPTOUT_URL");

    public Core(String host, Integer port, String name) {
        super(host, port, name);
    }

    public String attest(String attestationRequest) throws Exception {
        String response = HttpClient.post(getBaseUrl() + "/attest", attestationRequest, CORE_API_TOKEN);
        return response;
    }

}
