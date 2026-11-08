package com.petClinic.javorek.petclinic.mcp;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// Talks to the real /mcp endpoint (Streamable HTTP) over HTTP, so it proves that the @McpTool bean is
// registered, the transport is configured, and the tool works against the DataLoader sample data.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class McpServerIntegrationTest {

    @LocalServerPort
    int port;

    private final HttpClient http = HttpClient.newHttpClient();
    private String sessionId;

    @BeforeEach
    void initializeSession() throws Exception {
        HttpResponse<String> response = post("""
                {"jsonrpc":"2.0","id":1,"method":"initialize","params":{
                  "protocolVersion":"2025-03-26","capabilities":{},
                  "clientInfo":{"name":"test","version":"1.0"}}}""");
        assertThat(response.statusCode()).isEqualTo(200);
        sessionId = response.headers().firstValue("Mcp-Session-Id").orElse(null);

        post("""
                {"jsonrpc":"2.0","method":"notifications/initialized"}""");
    }

    @Test
    void toolsListContainsFindOwners() throws Exception {
        HttpResponse<String> response = post("""
                {"jsonrpc":"2.0","id":2,"method":"tools/list"}""");

        List<String> names = JsonPath.read(jsonBody(response), "$.result.tools[*].name");
        assertThat(names).contains("find_owners");
    }

    @Test
    void findOwnersReturnsSampleOwnerWithPetIds() throws Exception {
        HttpResponse<String> response = post("""
                {"jsonrpc":"2.0","id":3,"method":"tools/call",
                 "params":{"name":"find_owners","arguments":{"lastName":"nov"}}}""");

        String body = jsonBody(response);
        assertThat((Boolean) JsonPath.read(body, "$.result.isError")).isFalse();
        // The tool result is JSON text; the sample owner Novák must come with his pets and their ids.
        String text = JsonPath.read(body, "$.result.content[0].text");
        assertThat((String) JsonPath.read(text, "$[0].lastName")).isEqualTo("Novák");
        List<Integer> petIds = JsonPath.read(text, "$[0].pets[*].id");
        assertThat(petIds).isNotEmpty();
    }

    private HttpResponse<String> post(String json) throws Exception {
        HttpRequest.Builder request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/mcp"))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json, text/event-stream")
                .POST(HttpRequest.BodyPublishers.ofString(json));
        if (sessionId != null) {
            request.header("Mcp-Session-Id", sessionId);
        }
        return http.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }

    // Streamable HTTP may answer with plain JSON or with a server-sent event ("data: {...}").
    private static String jsonBody(HttpResponse<String> response) {
        String body = response.body().strip();
        if (body.startsWith("{")) {
            return body;
        }
        return body.lines()
                .filter(line -> line.startsWith("data:"))
                .map(line -> line.substring("data:".length()).strip())
                .reduce((first, second) -> second)
                .orElseThrow(() -> new AssertionError("No JSON in response: " + body));
    }
}