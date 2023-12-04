package app.component;

import app.common.EnvUtil;
import app.common.HttpClient;
import app.common.Mapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.uid2.client.*;

import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

public class Operator extends App {
    public enum Type {
        PUBLIC("Public"),
        PRIVATE("Private");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum CloudProvider {
        PUBLIC(""),
        AWS("AWS"),
        GCP("GCP-OIDC"),
        AZURE("Azure-CC");

        private final String name;

        CloudProvider(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private record V2Envelope(String envelope, byte[] nonce) {
    }

    private static final String CLIENT_API_KEY = EnvUtil.getEnv("UID2_E2E_API_KEY");
    private static final String CLIENT_API_SECRET = EnvUtil.getEnv("UID2_E2E_API_SECRET");
    private static final String CLIENT_API_KEY_BEFORE_OPTOUT_CUTOFF = EnvUtil.getEnv("UID2_E2E_API_KEY_OLD");
    private static final String CLIENT_API_SECRET_BEFORE_OPTOUT_CUTOFF = EnvUtil.getEnv("UID2_E2E_API_SECRET_OLD");
    private static final IdentityScope IDENTITY_SCOPE = IdentityScope.valueOf(EnvUtil.getEnv("UID2_E2E_IDENTITY_SCOPE"));
    private static final int TIMESTAMP_LENGTH = 8;
    private static final String TC_STRING = "CPhJRpMPhJRpMABAMBFRACBoALAAAEJAAIYgAKwAQAKgArABAAqAAA";

    private final Type type;
    private final PublisherUid2Client publisherClient;
    private final PublisherUid2Client oldPublisherClient;
    private final UID2Client dspClient;

    public Operator(String host, Integer port, String name, Type type) {
        super(host, port, name);

        this.type = type;

        this.publisherClient = new PublisherUid2Client(
                getBaseUrl(),
                CLIENT_API_KEY,
                CLIENT_API_SECRET
        );
        this.oldPublisherClient = new PublisherUid2Client(
                getBaseUrl(),
                CLIENT_API_KEY_BEFORE_OPTOUT_CUTOFF,
                CLIENT_API_SECRET_BEFORE_OPTOUT_CUTOFF
        );
        this.dspClient = new UID2Client(
                getBaseUrl(),
                CLIENT_API_KEY,
                CLIENT_API_SECRET,
                IDENTITY_SCOPE
        );
    }

    public Operator(String host, String name, Type type) {
        this(host, null, name, type);
    }

    public Type getType() {
        return type;
    }

    public JsonNode v0TokenGenerate(String type, String identity) throws Exception {
        String response = HttpClient.get(getBaseUrl() + "/token/generate?" + type + "=" + URLEncoder.encode(identity, StandardCharsets.UTF_8), CLIENT_API_KEY);
        return Mapper.OBJECT_MAPPER.readTree(response);
    }

    public JsonNode v0TokenRefresh(String token) throws Exception {
        String response = HttpClient.get(getBaseUrl() + "/token/refresh?refresh_token=" + URLEncoder.encode(token, StandardCharsets.UTF_8), CLIENT_API_KEY);
        return Mapper.OBJECT_MAPPER.readTree(response);
    }

    public JsonNode v0TokenValidate(String type, String identity, String advertisingToken) throws Exception {
        String response = HttpClient.get(getBaseUrl() + "/token/validate?" + type + "=" + URLEncoder.encode(identity, StandardCharsets.UTF_8) + "&token=" + URLEncoder.encode(advertisingToken, StandardCharsets.UTF_8), CLIENT_API_KEY);
        return Mapper.OBJECT_MAPPER.readTree(response);
    }

    public String v0TokenLogout(String type, String identity) throws Exception {
        return HttpClient.get(getBaseUrl() + "/token/logout?" + type + "=" + URLEncoder.encode(identity, StandardCharsets.UTF_8), CLIENT_API_KEY);
    }

    public String v0IdentityMap(String type, String identity) throws Exception {
        return HttpClient.get(getBaseUrl() + "/identity/map?" + type + "=" + URLEncoder.encode(identity, StandardCharsets.UTF_8), CLIENT_API_KEY);
    }

    public String v0StaticJsUid2Sdk() throws Exception {
        return HttpClient.get(getBaseUrl() + "/static/js/uid2-sdk-2.0.0.js", CLIENT_API_KEY);
    }

    public JsonNode v1TokenGenerate(String type, String identity) throws Exception {
        String response = HttpClient.get(getBaseUrl() + "/v1/token/generate?" + type + "=" + URLEncoder.encode(identity, StandardCharsets.UTF_8), CLIENT_API_KEY);
        return Mapper.OBJECT_MAPPER.readTree(response);
    }

    public JsonNode v1TokenRefresh(String token) throws Exception {
        String response = HttpClient.get(getBaseUrl() + "/v1/token/refresh?refresh_token=" + URLEncoder.encode(token, StandardCharsets.UTF_8), CLIENT_API_KEY);
        return Mapper.OBJECT_MAPPER.readTree(response);
    }

    public JsonNode v1TokenValidate(String type, String identity, String advertisingToken) throws Exception {
        String response = HttpClient.get(getBaseUrl() + "/v1/token/validate?" + type + "=" + URLEncoder.encode(identity, StandardCharsets.UTF_8) + "&token=" + URLEncoder.encode(advertisingToken, StandardCharsets.UTF_8), CLIENT_API_KEY);
        return Mapper.OBJECT_MAPPER.readTree(response);
    }

    public JsonNode v1IdentityMap(String type, String identity) throws Exception {
        String response = HttpClient.get(getBaseUrl() + "/v1/identity/map?" + type + "=" + URLEncoder.encode(identity, StandardCharsets.UTF_8), CLIENT_API_KEY);
        return Mapper.OBJECT_MAPPER.readTree(response);
    }

    public JsonNode v1IdentityMapBatch(String payload) throws Exception {
        String response = HttpClient.post(getBaseUrl() + "/v1/identity/map", payload, CLIENT_API_KEY);
        return Mapper.OBJECT_MAPPER.readTree(response);
    }

    public JsonNode v1KeyLatest() throws Exception {
        String response = HttpClient.get(getBaseUrl() + "/v1/key/latest", CLIENT_API_KEY);
        return Mapper.OBJECT_MAPPER.readTree(response);
    }

    public TokenGenerateResponse v2TokenGenerate(String type, String identity, boolean asOldParticipant) {
        TokenGenerateInput token;

        if ("email".equals(type)) {
            token = TokenGenerateInput.fromEmail(identity);
        } else if ("phone".equals(type)) {
            token = TokenGenerateInput.fromPhone(identity);
        } else {
            throw new IllegalArgumentException("Unsupported input type for token generation");
        }

        if (IDENTITY_SCOPE == IdentityScope.EUID) {
            token = token.withTransparencyAndConsentString(TC_STRING);
        }

        if (!asOldParticipant) {
            token = token.doNotGenerateTokensForOptedOut();
            return publisherClient.generateTokenResponse(token);
        } else {
            return oldPublisherClient.generateTokenResponse(token);
        }
    }

    public JsonNode v2TokenGenerateUsingPayload(String payload, boolean asOldParticipant) throws Exception {
        V2Envelope envelope = v2CreateEnvelope(payload, getClientApiSecret(asOldParticipant));
        String encryptedResponse = HttpClient.post(getBaseUrl() + "/v2/token/generate", envelope.envelope(), getClientApiKey(asOldParticipant));
        return v2DecryptEncryptedResponse(encryptedResponse, envelope.nonce(), getClientApiSecret(asOldParticipant));
    }

    public TokenRefreshResponse v2TokenRefresh(IdentityTokens identity) {
        return publisherClient.refreshToken(identity);
    }

    public JsonNode v2TokenRefresh(String refreshToken, String refreshResponseKey) throws Exception {
        String response = HttpClient.post(getBaseUrl() + "/v2/token/refresh", refreshToken, CLIENT_API_KEY);
        return v2DecryptRefreshResponse(response, refreshResponseKey);
    }

    public JsonNode v2TokenValidate(String type, String identity, String advertisingToken) throws Exception {
        String payload = "{\"%s\":\"%s\",\"token\":\"%s\"}".formatted(type, identity, advertisingToken);
        V2Envelope envelope = v2CreateEnvelope(payload, CLIENT_API_SECRET);
        String encryptedResponse = HttpClient.post(getBaseUrl() + "/v2/token/validate", envelope.envelope(), CLIENT_API_KEY);
        return v2DecryptEncryptedResponse(encryptedResponse, envelope.nonce(), CLIENT_API_SECRET);
    }

    public JsonNode v2TokenLogout(String type, String identity) throws Exception {
        String payload = "{\"%s\":\"%s\"}".formatted(type, identity);
        V2Envelope envelope = v2CreateEnvelope(payload, CLIENT_API_SECRET);
        String encryptedResponse = HttpClient.post(getBaseUrl() + "/v2/token/logout", envelope.envelope(), CLIENT_API_KEY);
        return v2DecryptEncryptedResponse(encryptedResponse, envelope.nonce(), CLIENT_API_SECRET);
    }

    public DecryptionResponse v2TokenDecrypt(String token) throws UID2ClientException {
        dspClient.refresh();
        return dspClient.decrypt(token);
    }

    public JsonNode v2IdentityMap(String payload, boolean asOldParticipant) throws Exception {
        V2Envelope envelope = v2CreateEnvelope(payload, getClientApiSecret(asOldParticipant));
        String encryptedResponse = HttpClient.post(getBaseUrl() + "/v2/identity/map", envelope.envelope(), getClientApiKey(asOldParticipant));
        return v2DecryptEncryptedResponse(encryptedResponse, envelope.nonce(), getClientApiSecret(asOldParticipant));
    }

    public JsonNode v2IdentityBuckets(String payload) throws Exception {
        V2Envelope envelope = v2CreateEnvelope(payload, CLIENT_API_SECRET);
        String encryptedResponse = HttpClient.post(getBaseUrl() + "/v2/identity/buckets", envelope.envelope(), CLIENT_API_KEY);
        return v2DecryptEncryptedResponse(encryptedResponse, envelope.nonce(), CLIENT_API_SECRET);
    }

    public JsonNode v2KeySharing() throws Exception {
        V2Envelope envelope = v2CreateEnvelope("", CLIENT_API_SECRET);
        String encryptedResponse = HttpClient.post(getBaseUrl() + "/v2/key/sharing", envelope.envelope(), CLIENT_API_KEY);
        return v2DecryptEncryptedResponse(encryptedResponse, envelope.nonce(), CLIENT_API_SECRET);
    }

    private String getClientApiKey(boolean asOldParticipant) {
        return asOldParticipant ? CLIENT_API_KEY_BEFORE_OPTOUT_CUTOFF : CLIENT_API_KEY;
    }

    private String getClientApiSecret(boolean asOldParticipant) {
        return asOldParticipant ? CLIENT_API_SECRET_BEFORE_OPTOUT_CUTOFF : CLIENT_API_SECRET;
    }

    private V2Envelope v2CreateEnvelope(String payload, String secret) throws Exception {
        // Unencrypted envelope payload = timestamp + nonce + raw payload
        Instant timestamp = Instant.now();

        int nonceLength = 8;
        byte[] nonce = new byte[nonceLength];
        new SecureRandom().nextBytes(nonce);

        byte[] payloadBytes = payload.getBytes(StandardCharsets.UTF_8);

        ByteBuffer writer = ByteBuffer.allocate(TIMESTAMP_LENGTH + nonce.length + payloadBytes.length);
        writer.putLong(timestamp.toEpochMilli());
        writer.put(nonce);
        writer.put(payloadBytes);

        // Encrypted envelope = 1 + iv + encrypted envelope payload + tag
        byte envelopeVersion = 1;

        byte[] encrypted = encryptGDM(writer.array(), base64ToByteArray(secret)); // iv + encrypted envelope payload + tag

        ByteBuffer envelopeBuffer = ByteBuffer.allocate(1 + encrypted.length);
        envelopeBuffer.put(envelopeVersion);
        envelopeBuffer.put(encrypted);
        return new V2Envelope(byteArrayToBase64(envelopeBuffer.array()), nonce);
    }

    private JsonNode v2DecryptEncryptedResponse(String encryptedResponse, byte[] nonceInRequest, String secret) throws Exception {
        Method decryptMethod = PublisherUid2Helper.class.getDeclaredMethod("decrypt", String.class, byte[].class, boolean.class, byte[].class);
        decryptMethod.setAccessible(true);
        String decryptedResponse = (String) decryptMethod.invoke(PublisherUid2Helper.class, encryptedResponse, base64ToByteArray(secret), false, nonceInRequest);
        return Mapper.OBJECT_MAPPER.readTree(decryptedResponse);
    }

    private JsonNode v2DecryptRefreshResponse(String refreshToken, String refreshResponseKey) throws Exception {
        Method decryptMethod = PublisherUid2Helper.class.getDeclaredMethod("decrypt", String.class, byte[].class, boolean.class, byte[].class);
        decryptMethod.setAccessible(true);
        String decryptedResponse = (String) decryptMethod.invoke(PublisherUid2Helper.class, refreshToken, base64ToByteArray(refreshResponseKey), true, null);
        return Mapper.OBJECT_MAPPER.readTree(decryptedResponse);
    }

    private byte[] encryptGDM(byte[] b, byte[] secretBytes) throws Exception {
        Class<?> clazz = Class.forName("com.uid2.client.Uid2Encryption");
        Method encryptGDMMethod = clazz.getDeclaredMethod("encryptGCM", byte[].class, byte[].class, byte[].class);
        encryptGDMMethod.setAccessible(true);
        return (byte[]) encryptGDMMethod.invoke(clazz, b, null, secretBytes);
    }

    private byte[] base64ToByteArray(String str) {
        return Base64.getDecoder().decode(str);
    }

    private String byteArrayToBase64(byte[] b) {
        return Base64.getEncoder().encodeToString(b);
    }
}
