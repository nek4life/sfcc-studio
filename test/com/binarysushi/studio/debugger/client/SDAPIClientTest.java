package com.binarysushi.studio.debugger.client;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SDAPIClientTest {
    SDAPIClient client;

    @BeforeEach
    void setUp() {
        client = new SDAPIClient("", "", "", "StudioDebuggerClient");
    }

    @Test
    void createSession() {
        HttpResponse<JsonNode> response = client.createSession();
        assertTrue(response.isSuccess());
    }

    @Test
    void deleteSession() {
    }
}
