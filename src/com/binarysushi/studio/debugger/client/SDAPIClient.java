package com.binarysushi.studio.debugger.client;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

public class SDAPIClient {
    private String baseURL;

    public SDAPIClient(String hostname, String username, String password, String clientId) {
        baseURL = "https://" + hostname + "/s/-/dw/debugger/v1_0";
        setClientConfig(username, password, clientId);
    }

    private void setClientConfig(String username, String password, String clientId) {
        Unirest.config()
                .setDefaultBasicAuth(username, password)
                .setDefaultHeader("x-dw-client-id", clientId)
                .setDefaultHeader("Content-Type", "application/json");
    }

    public HttpResponse<JsonNode> createSession() {
        return Unirest.post(baseURL + "/client").asJson();
    }

    public HttpResponse<JsonNode> deleteSession() {
        return Unirest.delete(baseURL + "/client").asJson();
    }
}
