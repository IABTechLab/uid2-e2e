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
    
    // The SQS delta producer runs on port 8082 (8081 + 1)
    private static final int DELTA_PRODUCER_PORT_OFFSET = 1;
    
    // Loaded lazily to avoid crashing when env var is missing
    private String optoutInternalApiKey;

    public Optout(String host, Integer port, String name) {
        super(host, port, name);
        // Load API key lazily - only fail when actually used
        this.optoutInternalApiKey = EnvUtil.getEnv(Const.Config.Core.OPTOUT_INTERNAL_API_KEY, false);
    }

    public Optout(String host, String name) {
        super(host, null, name);
        this.optoutInternalApiKey = EnvUtil.getEnv(Const.Config.Core.OPTOUT_INTERNAL_API_KEY, false);
    }
    
    private String getOptoutInternalApiKey() {
        if (optoutInternalApiKey == null || optoutInternalApiKey.isEmpty()) {
            throw new IllegalStateException("Missing environment variable: " + Const.Config.Core.OPTOUT_INTERNAL_API_KEY);
        }
        return optoutInternalApiKey;
    }

    /**
     * Triggers delta production on the optout service.
     * This reads from the SQS queue and produces delta files.
     * The endpoint is on port 8082 (optout port + 1).
     * 
     * @return JsonNode with response, or null if job already running (409)
     */
    public JsonNode triggerDeltaProduce() throws Exception {
        String deltaProduceUrl = getDeltaProducerBaseUrl() + "/optout/deltaproduce";
        try {
            String response = HttpClient.post(deltaProduceUrl, "", getOptoutInternalApiKey());
            return OBJECT_MAPPER.readTree(response);
        } catch (HttpClient.HttpException e) {
            if (e.getCode() == 409) {
                // Job already running - this is fine, we'll just wait for it
                return null;
            }
            throw e;
        }
    }

    /**
     * Gets the status of the current delta production job.
     */
    public JsonNode getDeltaProduceStatus() throws Exception {
        String statusUrl = getDeltaProducerBaseUrl() + "/optout/deltaproduce/status";
        String response = HttpClient.get(statusUrl, getOptoutInternalApiKey());
        return OBJECT_MAPPER.readTree(response);
    }

    /**
     * Triggers delta production and waits for it to complete.
     * If a job is already running, waits for that job instead.
     * @param maxWaitSeconds Maximum time to wait for completion
     * @return true if delta production completed successfully
     */
    public boolean triggerDeltaProduceAndWait(int maxWaitSeconds) throws Exception {
        // Try to trigger - will return null if job already running (409)
        triggerDeltaProduce();
        
        long startTime = System.currentTimeMillis();
        long maxWaitMs = maxWaitSeconds * 1000L;
        
        while (System.currentTimeMillis() - startTime < maxWaitMs) {
            Thread.sleep(2000); // Poll every 2 seconds
            
            JsonNode status = getDeltaProduceStatus();
            String state = status.path("state").asText();
            
            if ("completed".equalsIgnoreCase(state) || "failed".equalsIgnoreCase(state)) {
                return "completed".equalsIgnoreCase(state);
            }
            
            // If idle (no job), try to trigger again
            if ("idle".equalsIgnoreCase(state) || "none".equalsIgnoreCase(state) || state.isEmpty()) {
                triggerDeltaProduce();
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
