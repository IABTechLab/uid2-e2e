package app.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uid2.shared.util.Mapper;
import common.Const;
import common.EnvUtil;
import common.HttpClient;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;

public class Core extends App {
    private static final ObjectMapper OBJECT_MAPPER = Mapper.getInstance();
    private static final String OPERATOR_API_KEY = EnvUtil.getEnv(Const.Config.Core.OPERATOR_API_KEY);
    private static final String OPTOUT_API_KEY = EnvUtil.getEnv(Const.Config.Core.OPTOUT_API_KEY);
    public static final String CORE_URL = EnvUtil.getEnv(Const.Config.Core.CORE_URL);
    public static final String OPTOUT_URL = EnvUtil.getEnv(Const.Config.Core.OPTOUT_URL);

    public Core(String host, Integer port, String name) {
        super(host, port, name);
    }

    public Core(String host, String name) {
        super(host, null, name);
    }

    public JsonNode attest(String attestationRequest) throws Exception {
        String response = HttpClient.post(getBaseUrl() + "/attest", attestationRequest, OPERATOR_API_KEY);
        return OBJECT_MAPPER.readTree(response);
    }

    public JsonNode getWithCoreApiToken(String path) throws Exception {
        return getWithCoreApiToken(path, false);
    }

    public JsonNode getWithCoreApiToken(String path, boolean encrypted) throws Exception {
        Map<String, String> headers = new HashMap<>();
        if (encrypted)
            headers.put("Encrypted", "true");
        String response = HttpClient.get(getBaseUrl() + path, OPERATOR_API_KEY, headers);
        return OBJECT_MAPPER.readTree(response);
    }

    public JsonNode getWithOptOutApiToken(String path) throws Exception {
        String response = HttpClient.get(getBaseUrl() + path, OPTOUT_API_KEY);
        return OBJECT_MAPPER.readTree(response);
    }

    public JsonNode getOperatorConfig() throws Exception {
        Map<String, String> headers = new HashMap<>();
        String response = HttpClient.get(getBaseUrl() + "/operator/config", OPERATOR_API_KEY, headers);
        return OBJECT_MAPPER.readTree(response);
    }
}
