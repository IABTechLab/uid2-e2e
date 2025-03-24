package app.component;


import common.EnvUtil;
import common.HttpClient;
import common.Mapper;
import com.fasterxml.jackson.databind.JsonNode;

public class Optout extends App {
    private static final String CORE_API_TOKEN = EnvUtil.getEnv("UID2_E2E_OPTOUT_TO_CALL_CORE_API_TOKEN");
    public Optout(String host, Integer port, String name) {
        super(host, port, name);
    }

    public JsonNode getPath(String path) throws Exception {
        String response = HttpClient.get(getBaseUrl() + path, CORE_API_TOKEN);
        return Mapper.OBJECT_MAPPER.readTree(response);
    }

}
