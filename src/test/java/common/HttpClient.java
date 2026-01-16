package common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uid2.shared.util.Mapper;
import lombok.Getter;
import okhttp3.*;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import java.util.Map;

public final class HttpClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClient.class);
    private static final ObjectMapper OBJECT_MAPPER = Mapper.getInstance();

    public static final OkHttpClient RAW_CLIENT = new OkHttpClient();
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public enum HttpMethod {
        GET,
        POST
    }

    @Getter
    public static class HttpException extends Exception {
        private final HttpMethod method;
        private final String url;
        private final int code;
        private final String codeMessage;
        private final String response;

        public HttpException(HttpMethod method, String url, int code, String codeMessage, String response) {
            super(createErrorMessage(method, url, code, codeMessage, response));

            this.method = method;
            this.url = url;
            this.code = code;
            this.codeMessage = codeMessage;
            this.response = response;
        }

        public JsonNode getResponseJson() throws Exception {
            return OBJECT_MAPPER.readTree(response);
        }

        private static String createErrorMessage(HttpMethod method, String url, int code, String message, String response) {
            return "Unsuccessful %s request - URL: %s - Code: %d %s - Response body: %s".formatted(
                    method, url, code, message, response
            );
        }
    }

    private HttpClient() {
    }

    public static String get(String url, String bearerToken, Map<String, String> additionalHeaders) throws Exception {
        Request request = buildRequest(HttpMethod.GET, url, null, bearerToken, additionalHeaders);
        return execute(request, HttpMethod.GET);
    }

    public static String get(String url, String bearerToken) throws Exception {
        return get(url, bearerToken, null);
    }

    public static String get(String url) throws Exception {
        return get(url, null);
    }

    public static String post(String url, String body, String bearerToken) throws Exception {
        Request request = buildRequest(HttpMethod.POST, url, body, bearerToken);
        return execute(request, HttpMethod.POST);
    }

    public static String execute(Request request, HttpMethod method) throws Exception {
        final String url = request.url().toString();
        final String requestBodyForLog = extractRequestBodyForLog(request);
        final String authHeader = extractAuthHeader(request);
        final Headers headers = request.headers();
        
        LOGGER.info(() -> String.format(
            "[HTTP REQUEST] %s %s%n" +
            "  Authorization: %s%n" +
            "  Request Body: %s%n" +
            "  Headers: %s",
            method, url,
            authHeader,
            requestBodyForLog,
            headers.toString()
        ));
        
        try (Response response = RAW_CLIENT.newCall(request).execute()) {
            final ResponseBody body = response.body();
            // Read the FULL response body - don't truncate the actual data!
            final String fullResponseBody = (body != null) ? body.string() : "";
            final int statusCode = response.code();
            final String statusMessage = response.message();
            final Headers responseHeaders = response.headers();
            
            // Only truncate for logging display
            final String responseBodyForLog = truncateForLog(fullResponseBody, 1000);
            
            LOGGER.info(() -> String.format(
                "[HTTP RESPONSE] %s %s%n" +
                "  Status: %d %s%n" +
                "  Response Headers: %s%n" +
                "  Response Body: %s",
                method, url,
                statusCode, statusMessage,
                responseHeaders.toString(),
                responseBodyForLog
            ));
            
            if (!response.isSuccessful()) {
                LOGGER.error(() -> String.format(
                    "[HTTP ERROR] Request failed: %s %s - Status: %d %s - Response: %s",
                    method, url, statusCode, statusMessage, responseBodyForLog
                ));
                throw new HttpException(method, url, statusCode, statusMessage, fullResponseBody);
            }
            // Return the FULL response body, not truncated
            return fullResponseBody;
        }
    }
    
    private static String truncateForLog(String text, int maxLength) {
        if (text == null || text.isEmpty()) {
            return "[empty]";
        }
        if (text.length() > maxLength) {
            return text.substring(0, maxLength) + "... [truncated, total length=" + text.length() + "]";
        }
        return text;
    }
    
    private static String extractRequestBodyForLog(Request request) {
        if (request.body() == null) {
            return "[empty]";
        }
        try {
            okio.Buffer buffer = new okio.Buffer();
            request.body().writeTo(buffer);
            String body = buffer.readUtf8();
            return truncateForLog(body, 500);
        } catch (Exception e) {
            return "[unable to read request body: " + e.getMessage() + "]";
        }
    }
    
    private static String extractAuthHeader(Request request) {
        Headers headers = request.headers();
        for (int i = 0; i < headers.size(); i++) {
            if ("Authorization".equalsIgnoreCase(headers.name(i))) {
                String authValue = headers.value(i);
                // Mask the token for security, but show first/last few chars
                if (authValue.length() > 20) {
                    return authValue.substring(0, 10) + "..." + authValue.substring(authValue.length() - 10);
                } else {
                    return "[masked]";
                }
            }
        }
        return "[none]";
    }

    private static Request buildRequest(HttpMethod method, String url, String body, String bearerToken) {
        return buildRequest(method, url, body, bearerToken, null);
    }

    private static Request buildRequest(HttpMethod method, String url, String body, String bearerToken, Map<String, String> additionalHeaders) {
        Request.Builder requestBuilder = new Request.Builder()
                .url(url);

        if (bearerToken != null) {
            requestBuilder = requestBuilder.addHeader("Authorization", "Bearer " + bearerToken);
        }

        if (additionalHeaders != null) {
            for (Map.Entry<String, String> header : additionalHeaders.entrySet()) {
                requestBuilder = requestBuilder.addHeader(header.getKey(), header.getValue());
            }
        }

        return (switch (method) {
            case GET -> requestBuilder.get();
            case POST -> requestBuilder.post(RequestBody.create(body, JSON));
        }).build();
    }
}
