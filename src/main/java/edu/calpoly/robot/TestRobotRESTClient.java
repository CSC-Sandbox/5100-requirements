package edu.calpoly.robot;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

/**
 * Tests successful and invalid REST robot responses.
 *
 * @author Jess A (0x10jalencas)
 * @version 1.0.0 (9/28/2026)
 */
public final class TestRobotRESTClient {
    private static final String VALID_JSON =
            "{\"jointAngles\":[2.0,1.75,1.0,0.5,0.25,0.0],"
            + "\"position\":[0.5,-10.0,5.25]}";

    private TestRobotRESTClient() {
    }

    /**
     * Runs the REST client test cases.
     *
     * @param args command-line arguments; unused
     * @throws IOException if the local test server cannot start
     */
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(
                new InetSocketAddress(0),
                0);

        server.createContext(
                "/robot/1",
                exchange -> reply(exchange, 200, VALID_JSON));
        server.createContext(
                "/missing",
                exchange -> reply(exchange, 404, "Not found"));
        server.createContext(
                "/invalid",
                exchange -> reply(exchange, 200, "Invalid JSON"));
        server.start();

        try {
            String baseURL = "http://localhost:"
                    + server.getAddress().getPort();

            RobotBlackBoard blackboard = new RobotBlackBoard();

            RobotRESTClient validClient = new RobotRESTClient(
                    baseURL + "/robot/1",
                    blackboard);

            if (validClient.fetch().isEmpty()
                    || !blackboard.hasData()) {
                throw new AssertionError(
                        "Valid robot data was not consumed.");
            }

            blackboard.clear();

            RobotRESTClient missingClient = new RobotRESTClient(
                    baseURL + "/missing",
                    blackboard);

            if (missingClient.fetch().isPresent()
                    || blackboard.hasData()) {
                throw new AssertionError(
                        "Missing data should not update the blackboard.");
            }

            RobotRESTClient invalidClient = new RobotRESTClient(
                    baseURL + "/invalid",
                    blackboard);

            if (invalidClient.fetch().isPresent()
                    || blackboard.hasData()) {
                throw new AssertionError(
                        "Invalid data should not update the blackboard.");
            }

            System.out.println("REST client tests passed: 3/3");
        } finally {
            server.stop(0);
        }
    }

    private static void reply(
            HttpExchange exchange,
            int statusCode,
            String body) throws IOException {
        byte[] response = body.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json");
        exchange.sendResponseHeaders(
                statusCode,
                response.length);

        try (OutputStream output = exchange.getResponseBody()) {
            output.write(response);
        }

        exchange.close();
    }
}