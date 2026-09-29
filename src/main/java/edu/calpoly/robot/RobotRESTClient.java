package edu.calpoly.robot;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Retrieves robot data through REST and posts it to a RobotBlackBoard.
 *
 * @author Jess A (0x10jalencas)
 * @version 1.0.0 (9/28/2026)
 */
public final class RobotRESTClient {
    private final URI uri;
    private final RobotBlackBoard blackboard;
    private final HttpClient client;
    private final ObjectMapper mapper;

    /**
     * Creates a REST client for a robot-data endpoint.
     *
     * @param url robot-data endpoint URL
     * @param blackboard destination for received robot data
     */
    public RobotRESTClient(
            String url,
            RobotBlackBoard blackboard) {
        this.uri = URI.create(url);
        this.blackboard = Objects.requireNonNull(blackboard);
        this.client = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
    }

    /**
     * Retrieves the latest robot data and posts it to the blackboard.
     *
     * @return the received message, or empty if retrieval fails
     */
    public Optional<RobotMessage> fetch() {
        HttpRequest request = HttpRequest.newBuilder(uri)
                .GET()
                .build();

        try {
            HttpResponse<byte[]> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() != 200) {
                System.err.println(
                        "REST request failed: HTTP "
                        + response.statusCode());
                return Optional.empty();
            }

            RobotMessage message = mapper.readValue(
                    response.body(),
                    RobotMessage.class);
            blackboard.post(message);
            return Optional.of(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("REST request interrupted.");
            return Optional.empty();
        } catch (IOException | RuntimeException e) {
            System.err.println(
                    "Unable to retrieve robot data: "
                    + e.getMessage());
            return Optional.empty();
        }
    }
}