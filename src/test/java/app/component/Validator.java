package app.component;

import common.HttpClient;
import com.uid2.client.*;
import okhttp3.*;

import java.io.IOException;

public class Validator extends App {
    private final PublisherUid2Helper publisherHelper;
    private final Headers standardHeaders;
    private final MediaType FORM = MediaType.get("application/x-www-form-urlencoded");;

    public Validator(String host, Integer port, String name, String clientApiKey, String clientSecret) {
        super(host, port, name);

        publisherHelper = new PublisherUid2Helper(clientSecret);
        standardHeaders = new Headers.Builder()
                .add("Authorization", "Bearer " + clientApiKey)
                .add("X-UID2-Client-Version: java-e2e-test")
                .build();
    }

    public Response triggerGenerateTokenFromEmail(String email) throws IOException {
        var envelope = publisherHelper.createEnvelopeForTokenGenerateRequest(TokenGenerateInput.fromEmail(email));

        Request request = new Request.Builder()
                .url(getBaseUrl() + "/v2/token/generate")
                .headers(standardHeaders)
                .post(RequestBody.create(envelope.getEnvelope(), MediaType.get("application/x-www-form-urlencoded")))
                .build();

        Call call = HttpClient.RAW_CLIENT.newCall(request);
        Response response = call.execute();
        response.close();
        return response;
    }

    public Response triggerGenerateTokenFromPhone(String phone) throws IOException {
        var envelope = publisherHelper.createEnvelopeForTokenGenerateRequest(TokenGenerateInput.fromPhone(phone));

        Request request = new Request.Builder()
                .url(getBaseUrl() + "/v2/token/generate")
                .headers(standardHeaders)
                .post(RequestBody.create(envelope.getEnvelope(), FORM))
                .build();

        Call call = HttpClient.RAW_CLIENT.newCall(request);
        Response response = call.execute();
        response.close();
        return response;
    }

    public Response refresh(IdentityTokens identityTokens) throws IOException {
        Request request = new Request.Builder()
                .url(getBaseUrl() + "/v2/token/refresh")
                .headers(standardHeaders)
                .post(RequestBody.create(identityTokens.getRefreshToken(), FORM))
                .build();


        Call call = HttpClient.RAW_CLIENT.newCall(request);
        Response response = call.execute();
        response.close();
        return response;
    }
}
