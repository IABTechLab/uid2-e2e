package common;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.GetPublicKeyRequest;
import software.amazon.awssdk.services.kms.model.GetPublicKeyResponse;

import java.net.URI;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Helper class for interacting with KMS (or LocalStack KMS) in e2e tests.
 * 
 * This allows tests to dynamically fetch public keys from KMS rather than
 * relying on hardcoded keys, which is necessary when using LocalStack since
 * it generates its own RSA key material.
 */
public final class KmsHelper {
    
    private static final String DEFAULT_LOCALSTACK_ENDPOINT = "http://localhost:5001";
    private static final String KMS_KEY_ID = "ff275b92-0def-4dfc-b0f6-87c96b26c6c7";
    private static final Region REGION = Region.US_EAST_1;
    
    private KmsHelper() {
    }
    
    private static String getLocalstackEndpoint() {
        String endpoint = EnvUtil.getEnv(Const.Config.Core.LOCALSTACK_URL, false);
        return (endpoint != null && !endpoint.isBlank()) ? endpoint : DEFAULT_LOCALSTACK_ENDPOINT;
    }
    
    /**
     * Fetches the public key from LocalStack KMS for the configured key ID.
     * 
     * @return The public key as a base64-encoded string (without PEM headers)
     * @throws Exception if the key cannot be fetched or parsed
     */
    public static String getPublicKeyFromLocalstack() throws Exception {
        try (KmsClient kmsClient = createLocalstackKmsClient()) {
            GetPublicKeyRequest request = GetPublicKeyRequest.builder()
                    .keyId(KMS_KEY_ID)
                    .build();
            
            GetPublicKeyResponse response = kmsClient.getPublicKey(request);
            
            // The public key is returned as raw DER-encoded bytes
            byte[] publicKeyBytes = response.publicKey().asByteArray();
            
            // Return as base64-encoded string (format expected by JwtService)
            return Base64.getEncoder().encodeToString(publicKeyBytes);
        }
    }
    
    /**
     * Fetches the public key from LocalStack KMS and returns it as a Java PublicKey object.
     * 
     * @return The PublicKey object
     * @throws Exception if the key cannot be fetched or parsed
     */
    public static PublicKey getPublicKeyObjectFromLocalstack() throws Exception {
        try (KmsClient kmsClient = createLocalstackKmsClient()) {
            GetPublicKeyRequest request = GetPublicKeyRequest.builder()
                    .keyId(KMS_KEY_ID)
                    .build();
            
            GetPublicKeyResponse response = kmsClient.getPublicKey(request);
            
            // The public key is returned as raw DER-encoded X.509 SubjectPublicKeyInfo
            byte[] publicKeyBytes = response.publicKey().asByteArray();
            
            // Convert to Java PublicKey
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            return keyFactory.generatePublic(keySpec);
        }
    }
    
    private static KmsClient createLocalstackKmsClient() {
        String endpoint = getLocalstackEndpoint();
        return KmsClient.builder()
                .endpointOverride(URI.create(endpoint))
                .region(REGION)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("test", "test")))
                .build();
    }
}
