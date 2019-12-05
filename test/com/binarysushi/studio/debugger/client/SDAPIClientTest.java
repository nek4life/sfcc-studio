package com.binarysushi.studio.debugger.client;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SDAPIClientTest {
    SDAPIClient client = new SDAPIClient("", "", "", "");

    @Test
    void createSession() {
        HttpResponse<JsonNode> response = client.createSession();
        assertTrue(response.isSuccess());
    }

    @Test
    void createBreakpoint() {
    }

    @Test
    void deleteSession() {
        HttpResponse<JsonNode> response = client.deleteSession();
        assertTrue(response.isSuccess());
    }
}
