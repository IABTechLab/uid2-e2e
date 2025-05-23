package common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uid2.shared.util.Mapper;
import lombok.Getter;
import okhttp3.*;

import java.util.Map;
import java.util.Objects;

public final class HttpClient {
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
        try (Response response = RAW_CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new HttpException(method, request.url().toString(), response.code(), response.message(), Objects.requireNonNull(response.body()).string());
            }
            return Objects.requireNonNull(response.body()).string();
        }
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
