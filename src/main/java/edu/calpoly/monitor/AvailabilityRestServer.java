package edu.calpoly.monitor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import javax.swing.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * REST provider for the current data-source availability state.
 *
 * <p>The server exposes the existing {@link MonitorAvailability} state through
 * {@code GET /availability}. Availability rules are not duplicated here.</p>
 *
 * @author Adrian Valenzuela (adrian0427)
 * @version September 29, 2026
 */
public class AvailabilityRestServer {

    private final MonitorAvailability monitor;
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpServer server;

    /**
     * Creates an availability REST provider.
     *
     * @param monitor the shared availability monitor that provides current state
     * @param port the HTTP port on which the provider should listen
     * @throws IOException if the HTTP server cannot be created
     */
    public AvailabilityRestServer(
            MonitorAvailability monitor,
            int port) throws IOException {

        this.monitor = monitor;

        server = HttpServer.create(
                new InetSocketAddress(port),
                0
        );

        server.createContext(
                "/availability",
                this::handleAvailability
        );
    }

    /**
     * Starts the REST provider.
     */
    public void start() {
        server.start();

        System.out.println(
                "REST provider: http://localhost:"
                        + server.getAddress().getPort()
                        + "/availability"
        );
    }

    /**
     * Stops the REST provider.
     */
    public void stop() {
        server.stop(0);
    }

    private void handleAvailability(
            HttpExchange exchange) throws IOException {

        if (!"GET".equals(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().set(
                    "Allow",
                    "GET"
            );

            sendJson(
                    exchange,
                    405,
                    "{\"error\":\"Method not allowed\"}"
            );

            return;
        }

        Map<String, String> response =
                createAvailabilityResponse();

        String json =
                mapper.writeValueAsString(response);

        sendJson(
                exchange,
                200,
                json
        );
    }

    private Map<String, String> createAvailabilityResponse() {
        Map<String, String> response =
                new LinkedHashMap<>();

        for (Map.Entry<DataSource, Boolean> entry
                : monitor.getAvailabilitySnapshot().entrySet()) {

            String source =
                    entry.getKey().name().toLowerCase();

            String status =
                    entry.getValue()
                            ? "AVAILABLE"
                            : "UNAVAILABLE";

            response.put(source, status);
        }

        return response;
    }

    private void sendJson(
            HttpExchange exchange,
            int status,
            String body) throws IOException {

        byte[] bytes =
                body.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json"
        );

        exchange.sendResponseHeaders(
                status,
                bytes.length
        );

        exchange.getResponseBody().write(bytes);
        exchange.close();
    }

    /**
     * Starts the availability monitor and REST provider on port 8080.
     *
     * @param args command-line arguments; not used
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                MonitorAvailability monitor =
                        new MonitorAvailability();

                monitor.startReceiving();

                AvailabilityRestServer server =
                        new AvailabilityRestServer(
                                monitor,
                                8080
                        );

                server.start();

            } catch (IOException e) {
                System.err.println(
                        "Could not start REST provider: "
                                + e.getMessage()
                );
            }
        });
    }
}