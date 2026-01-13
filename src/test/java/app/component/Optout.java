package app.component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uid2.shared.util.Mapper;
import common.Const;
import common.EnvUtil;
import common.HttpClient;

/**
 * Component for interacting with the UID2 Optout service.
 */
public class Optout extends App {
    private static final ObjectMapper OBJECT_MAPPER = Mapper.getInstance();
    private static final String OPTOUT_INTERNAL_API_KEY = EnvUtil.getEnv(Const.Config.Core.OPTOUT_INTERNAL_API_KEY);
    public static final String OPTOUT_URL = EnvUtil.getEnv(Const.Config.Core.OPTOUT_URL);

    // The SQS delta producer runs on port 8082 (8081 + 1)
    private static final int DELTA_PRODUCER_PORT_OFFSET = 1;

    public Optout(String host, Integer port, String name) {
        super(host, port, name);
    }

    public Optout(String host, String name) {
        super(host, null, name);
    }

    /**
     * Triggers delta production on the optout service.
     * This reads from the SQS queue and produces delta files.
     * The endpoint is on port 8082 (optout port + 1).
     */
    public JsonNode triggerDeltaProduce() throws Exception {
        String deltaProduceUrl = getDeltaProducerBaseUrl() + "/optout/deltaproduce";
        String response = HttpClient.post(deltaProduceUrl, "", OPTOUT_INTERNAL_API_KEY);
        return OBJECT_MAPPER.readTree(response);
    }

    /**
     * Gets the status of the current delta production job.
     */
    public JsonNode getDeltaProduceStatus() throws Exception {
        String statusUrl = getDeltaProducerBaseUrl() + "/optout/deltaproduce/status";
        String response = HttpClient.get(statusUrl, OPTOUT_INTERNAL_API_KEY);
        return OBJECT_MAPPER.readTree(response);
    }

    /**
     * Triggers delta production and waits for it to complete.
     * @param maxWaitSeconds Maximum time to wait for completion
     * @return true if delta production completed successfully
     */
    public boolean triggerDeltaProduceAndWait(int maxWaitSeconds) throws Exception {
        triggerDeltaProduce();
        
        long startTime = System.currentTimeMillis();
        long maxWaitMs = maxWaitSeconds * 1000L;
        
        while (System.currentTimeMillis() - startTime < maxWaitMs) {
            Thread.sleep(2000); // Poll every 2 seconds
            
            JsonNode status = getDeltaProduceStatus();
            String state = status.path("state").asText();
            
            if ("COMPLETED".equals(state) || "FAILED".equals(state)) {
                return "COMPLETED".equals(state);
            }
        }
        
        return false; // Timed out
    }

    private String getDeltaProducerBaseUrl() {
        // Delta producer runs on optout port + 1
        if (getPort() != null) {
            return "http://" + getHost() + ":" + (getPort() + DELTA_PRODUCER_PORT_OFFSET);
        }
        // If port not specified, assume default optout port (8081) + 1
        return "http://" + getHost() + ":8082";
    }
}
