package suite.operator;

import app.AppsMap;
import app.component.Operator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uid2.shared.util.Mapper;
import common.Const;
import common.EnvUtil;
import common.HttpClient;
import okhttp3.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;


public class IdentityMapDirectHttpTest {

    private static final ObjectMapper OBJECT_MAPPER = Mapper.getInstance();
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String CLIENT_API_KEY = EnvUtil.getEnv(Const.Config.Operator.CLIENT_API_KEY);
    private static final String CLIENT_API_SECRET = EnvUtil.getEnv(Const.Config.Operator.CLIENT_API_SECRET);
    private static final int TIMESTAMP_LENGTH = 8;

    private record V2Envelope(String envelope, byte[] nonce) {}

    /**
     * Test 1: JSON content type with base64 encoding (standard approach)
     */
    @Test
    public void test1_JsonWithBase64() throws Exception {
        System.out.println("🧪 Test 1: JSON content type + Base64 encoding");
        
        Operator operator = getFirstOperator();
        String jsonPayload = createTestPayload();
        
        // Create base64-encoded envelope
        V2Envelope envelope = createV2Envelope(jsonPayload, CLIENT_API_SECRET);
        
        // Send with JSON content type
        JsonNode response = sendWithJsonContentType(operator, envelope.envelope(), envelope.nonce());
        
        validateSuccessResponse(response, "Test 1");
        System.out.println("✅ Test 1 completed successfully\n");
    }

    /**
     * Test 2: Octet-stream content type with base64 encoding
     */
    @Test 
    public void test2_OctetStreamWithBase64() throws Exception {
        System.out.println("🧪 Test 2: Octet-stream content type + Base64 encoding");
        
        Operator operator = getFirstOperator();
        String jsonPayload = createTestPayload();
        
        // Create base64-encoded envelope
        V2Envelope envelope = createV2Envelope(jsonPayload, CLIENT_API_SECRET);
        
        // Send with octet-stream content type
        JsonNode response = sendWithOctetStreamContentType(operator, envelope.envelope().getBytes(StandardCharsets.UTF_8), envelope.nonce());
        
        validateSuccessResponse(response, "Test 2");
        System.out.println("✅ Test 2 completed successfully\n");
    }

    // Helper methods

    private Operator getFirstOperator() {
        return AppsMap.getApps(Operator.class).iterator().next();
    }

    private String createTestPayload() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("email", List.of("test.user@example.com", "user2@test.com"));
        payload.put("policy", 1);
        
        String jsonPayload = OBJECT_MAPPER.writeValueAsString(payload);
        System.out.println("Request payload: " + jsonPayload);
        return jsonPayload;
    }

    /**
     * Send request with JSON content type (string body)
     */
    private JsonNode sendWithJsonContentType(Operator operator, String body, byte[] nonce) throws Exception {
        String encryptedResponse = HttpClient.post(
            operator.getBaseUrl() + "/v2/identity/map", 
            body, 
            CLIENT_API_KEY
        );
        
        return decryptV2Response(encryptedResponse, nonce);
    }

    /**
     * Send request with octet-stream content type
     */
    private JsonNode sendWithOctetStreamContentType(Operator operator, byte[] bodyBytes, byte[] nonce) throws Exception {
        Request request = new Request.Builder()
            .url(operator.getBaseUrl() + "/v2/identity/map")
            .addHeader("Authorization", "Bearer " + CLIENT_API_KEY)
            .post(RequestBody.create(bodyBytes, MediaType.get("application/octet-stream")))
            .build();

        String encryptedResponse = executeRequest(request);
        return decryptV2Response(encryptedResponse, nonce);
    }

    /**
     * Execute HTTP request and return response body
     */
    private String executeRequest(Request request) throws Exception {
        try (Response response = HttpClient.RAW_CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("HTTP request failed: " + response.code() + " " + response.message());
            }
            return Objects.requireNonNull(response.body()).string();
        }
    }

    /**
     * Creates encrypted V2 envelope with base64 encoding
     */
    private V2Envelope createV2Envelope(String payload, String secret) throws Exception {
        // Create unencrypted envelope: timestamp + nonce + payload
        Instant timestamp = Instant.now();
        
        byte[] nonce = new byte[8];
        SECURE_RANDOM.nextBytes(nonce);
        
        byte[] payloadBytes = payload.getBytes(StandardCharsets.UTF_8);
        
        ByteBuffer writer = ByteBuffer.allocate(TIMESTAMP_LENGTH + nonce.length + payloadBytes.length);
        writer.putLong(timestamp.toEpochMilli());
        writer.put(nonce);
        writer.put(payloadBytes);
        
        // Encrypt the envelope
        byte envelopeVersion = 1;
        byte[] encrypted = encryptGCM(writer.array(), Base64.getDecoder().decode(secret));
        
        ByteBuffer envelopeBuffer = ByteBuffer.allocate(1 + encrypted.length);
        envelopeBuffer.put(envelopeVersion);
        envelopeBuffer.put(encrypted);
        
        return new V2Envelope(Base64.getEncoder().encodeToString(envelopeBuffer.array()), nonce);
    }

    /**
     * Decrypts V2 response using UID2 internal methods
     */
    private JsonNode decryptV2Response(String encryptedResponse, byte[] nonce) throws Exception {
        Class<?> uid2HelperClass = Class.forName("com.uid2.client.Uid2Helper");
        Constructor<?> constructor = uid2HelperClass.getDeclaredConstructor(String.class);
        constructor.setAccessible(true);
        Object uid2Helper = constructor.newInstance(CLIENT_API_SECRET);
        
        Method decryptMethod = uid2HelperClass.getDeclaredMethod("decrypt", String.class, byte[].class);
        decryptMethod.setAccessible(true);
        String decryptedResponse = (String) decryptMethod.invoke(uid2Helper, encryptedResponse, nonce);
        
        return OBJECT_MAPPER.readTree(decryptedResponse);
    }

    /**
     * Encrypts data using GCM encryption via UID2 internal methods
     */
    private byte[] encryptGCM(byte[] data, byte[] secretBytes) throws Exception {
        Class<?> encryptionClass = Class.forName("com.uid2.client.Uid2Encryption");
        Method encryptMethod = encryptionClass.getDeclaredMethod("encryptGCM", byte[].class, byte[].class, byte[].class);
        encryptMethod.setAccessible(true);
        return (byte[]) encryptMethod.invoke(encryptionClass, data, null, secretBytes);
    }

    /**
     * Validates successful response structure
     */
    private void validateSuccessResponse(JsonNode response, String testName) {
        System.out.println(testName + " - Response status: " + response.get("status").asText());
        
        // Validate successful response
        assertThat(response.get("status").asText()).isEqualTo("success");
        assertThat(response.has("body")).isTrue();
        
        JsonNode body = response.get("body");
        assertThat(body.has("mapped")).isTrue();
        
        JsonNode mapped = body.get("mapped");
        assertThat(mapped.isArray()).isTrue();
        assertThat(mapped.size()).isEqualTo(2);
        
        // Validate each mapped identity has required fields
        for (JsonNode mappedIdentity : mapped) {
            assertThat(mappedIdentity.has("advertising_id")).isTrue();
            assertThat(mappedIdentity.has("bucket_id")).isTrue();
            assertThat(mappedIdentity.get("advertising_id").asText()).isNotBlank();
            assertThat(mappedIdentity.get("bucket_id").asText()).isNotBlank();
        }
        
        System.out.println(testName + " - Successfully mapped 2 identities");
    }
} 