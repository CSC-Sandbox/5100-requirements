package edu.calpoly.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.calpoly.storage.FileMessageReader;
import edu.calpoly.storage.FileMessageStore;
import edu.calpoly.storage.MessageService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for MessageRestServer: runs the real server on a port
 * against a temp data file and exercises it with real HTTP calls.
 *
 * @author Edgard Aviles
 */
class MessageRestServerTest {

    private static final int PORT = 8099;
    private static final String BASE_URL = "http://localhost:" + PORT + "/messages";

    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private MessageRestServer server;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        String dataFile = tempDir.resolve("test-messages.csv").toString();
        FileMessageStore store = new FileMessageStore(dataFile);
        FileMessageReader reader = new FileMessageReader(dataFile);
        server = new MessageRestServer(new MessageService(store, reader), PORT);
        server.start();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void postThenGet_roundTripsAStoredMessage() throws Exception {
        HttpResponse<String> postResponse = post("{\"message\":\"LIDAR,1.0,2.0,3.0\"}");
        assertEquals(201, postResponse.statusCode());

        HttpResponse<String> getResponse = get();
        assertEquals(200, getResponse.statusCode());

        JsonNode records = mapper.readTree(getResponse.body());
        assertTrue(records.isArray());
        assertEquals(1, records.size());
        assertEquals("LIDAR,1.0,2.0,3.0", records.get(0).get("message").asText());
        assertNotNull(records.get(0).get("timestamp"));
    }

    @Test
    void post_rejectsInvalidMessage_withoutCrashingServer() throws Exception {
        // LIDAR requires 3 values; this only has 2.
        HttpResponse<String> postResponse = post("{\"message\":\"LIDAR,1.0,2.0\"}");
        assertEquals(400, postResponse.statusCode());

        // Server should still be up and serving requests afterward.
        HttpResponse<String> getResponse = get();
        assertEquals(200, getResponse.statusCode());
        assertEquals("[]", getResponse.body());
    }

    @Test
    void post_rejectsMalformedJson_withoutCrashingServer() throws Exception {
        HttpResponse<String> postResponse = post("not json at all");
        assertEquals(400, postResponse.statusCode());

        HttpResponse<String> getResponse = get();
        assertEquals(200, getResponse.statusCode());
    }

    @Test
    void post_rejectsMissingMessageField_withoutCrashingServer() throws Exception {
        HttpResponse<String> postResponse = post("{\"notMessage\":\"LIDAR,1.0,2.0,3.0\"}");
        assertEquals(400, postResponse.statusCode());
    }

    @Test
    void get_withNoStoredMessages_returnsEmptyArray() throws Exception {
        HttpResponse<String> getResponse = get();
        assertEquals(200, getResponse.statusCode());

        JsonNode records = mapper.readTree(getResponse.body());
        assertTrue(records.isArray());
        assertEquals(0, records.size());
    }

    @Test
    void unsupportedMethod_returns405() throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(BASE_URL)).DELETE().build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, response.statusCode());
    }

    private HttpResponse<String> post(String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        return http.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> get() throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(BASE_URL)).GET().build();
        return http.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
