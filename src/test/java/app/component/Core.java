package app.component;

import app.common.EnvUtil;
import app.common.HttpClient;
import app.common.Mapper;
import com.fasterxml.jackson.databind.JsonNode;

public class Core extends App {
    private static final String CORE_API_TOKEN = EnvUtil.getEnv("UID2_E2E_CORE_API_TOKEN");
    private static final String OPTOUT_TO_CALL_CORE_API_TOKEN = EnvUtil.getEnv("UID2_E2E_OPTOUT_TO_CALL_CORE_API_TOKEN");
    public static final String CORE_URL = EnvUtil.getEnv("UID2_E2E_CORE_URL");
    public static final String OPTOUT_URL = EnvUtil.getEnv("UID2_E2E_OPTOUT_URL");

    public Core(String host, Integer port, String name) {
        super(host, port, name);
    }

    public Core(String host, String name) {
        super(host, null, name);
    }

    public JsonNode attest(String attestationRequest) throws Exception {
        String response = HttpClient.post(getBaseUrl() + "/attest", attestationRequest, CORE_API_TOKEN);
        return Mapper.OBJECT_MAPPER.readTree(response);
    }

    public JsonNode clientSideKeyPairRefresh() throws Exception {
        String response = HttpClient.get(getBaseUrl() + "/client_side_keypairs/refresh", CORE_API_TOKEN);
        return Mapper.OBJECT_MAPPER.readTree(response);
    }
    public JsonNode getPath(String path) throws Exception {
        String response = HttpClient.get(getBaseUrl() + path, CORE_API_TOKEN);
        return Mapper.OBJECT_MAPPER.readTree(response);
    }
    public JsonNode getPathForOptOut(String path) throws Exception {
        String response = HttpClient.get(getBaseUrl() + path, OPTOUT_TO_CALL_CORE_API_TOKEN);
        return Mapper.OBJECT_MAPPER.readTree(response);
    }
}
