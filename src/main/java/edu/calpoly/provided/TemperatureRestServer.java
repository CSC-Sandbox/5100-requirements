package edu.calpoly.provided;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * REST provider for the temperature example.
 *
 * GET  /temperature returns the current temperature.
 * POST /temperature replaces the current temperature using JSON.
 *
 * @author Javier Gonzalez-Sanchez
 * @version 1.0 (2026-09-25)
 */
public class TemperatureRestServer {

    private final TemperatureService service;
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpServer server;

    public TemperatureRestServer(TemperatureService service, int port) throws IOException {
        this.service = service;
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/temperature", this::handleTemperature);
    }

    public void start() {
        server.start();
        System.out.println("REST provider: http://localhost:" + server.getAddress().getPort() + "/temperature");
    }

    public void stop() {
        server.stop(0);
    }

    private void handleTemperature(HttpExchange exchange) throws IOException {
        try {
            switch (exchange.getRequestMethod()) {
                case "GET" -> sendJson(exchange, 200, mapper.writeValueAsString(service.getTemperature()));
                case "POST" -> {
                    Temperature temperature =
                            mapper.readValue(exchange.getRequestBody(), Temperature.class);
                    service.setTemperature(temperature);
                    sendJson(exchange, 200, mapper.writeValueAsString(service.getTemperature()));
                }
                default -> {
                    exchange.getResponseHeaders().set("Allow", "GET, POST");
                    sendJson(exchange, 405, "{\"error\":\"Method not allowed\"}");
                }
            }
        } catch (IllegalArgumentException e) {
            sendJson(exchange, 400, "{\"error\":\"Invalid temperature\"}");
        } catch (Exception e) {
            sendJson(exchange, 400, "{\"error\":\"Invalid JSON\"}");
        }
    }

    private void sendJson(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }

    public static void main(String[] args) throws IOException {
        new TemperatureRestServer(new TemperatureService(), 8080).start();
    }
}
