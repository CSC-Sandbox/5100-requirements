package edu.calpoly.eye;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * REST provider for the current gaze position.
 *
 * GET /gaze returns the current gaze position as JSON.
 */
public class GazeRestServer {

    private final GazeService service;
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpServer server;

    public GazeRestServer(GazeService service, int port) throws IOException {
        this.service = service;
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/gaze", this::handleGaze);
    }

    public void start() {
        server.start();
        System.out.println(
                "REST provider: http://localhost:"
                        + server.getAddress().getPort() + "/gaze");
    }

    public void stop() {
        server.stop(0);
    }

    private void handleGaze(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            String json = mapper.writeValueAsString(service.getGazePoint());
            sendJson(exchange, 200, json);
        } else {
            exchange.getResponseHeaders().set("Allow", "GET");
            sendJson(exchange, 405, "{\"error\":\"Method not allowed\"}");
        }
    }

    private void sendJson(HttpExchange exchange, int status, String body)
            throws IOException {

        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type", "application/json");

        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }

    public static void main(String[] args) throws IOException {
        new GazeRestServer(new GazeService(), 8080).start();
    }
}