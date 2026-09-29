package edu.calpoly.monitor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Provides current data-activity information through the REST contract.
 * Requests delegate to the same activity service used by the MQTT provider.
 *
 * @author Dylan Gururajan
 * @version September 27, 2026
 */
public final class ActivityRestServer implements AutoCloseable {
    public static final String PATH = "/activity";

    private final ProvideDataActivity provider;
    private final ObjectMapper mapper;
    private final HttpServer server;

    /**
     * Creates an activity REST server.
     *
     * @param provider shared activity provider
     * @param port local port, or zero to select an available port
     */
    public ActivityRestServer(ProvideDataActivity provider, int port) throws IOException {
        this.provider = Objects.requireNonNull(provider, "provider");
        this.mapper = new ObjectMapper();
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext(PATH, this::handleActivity);
    }

    /**
     * Starts accepting REST requests.
     */
    public void start() {
        server.start();
    }

    /**
     * Returns the bound server port, including an automatically selected port.
     *
     * @return local HTTP port
     */
    public int getPort() {
        return server.getAddress().getPort();
    }

    private void handleActivity(HttpExchange exchange) throws IOException {
        try {
            if (!PATH.equals(exchange.getRequestURI().getPath())) {
                sendJson(exchange, 404, "{\"error\":\"Not found\"}");
                return;
            }
            if (!"GET".equals(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().set("Allow", "GET");
                sendJson(exchange, 405, "{\"error\":\"Method not allowed\"}");
                return;
            }

            sendJson(exchange, 200, mapper.writeValueAsString(provider.getSnapshot()));
        } catch (Exception exception) {
            sendJson(exchange, 500,
                    "{\"error\":\"Unable to obtain activity snapshot\"}");
        }
    }

    private static void sendJson(HttpExchange exchange, int status, String body)
            throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set(
                "Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }

    /**
     * Stops the REST server immediately.
     */
    @Override
    public void close() {
        server.stop(0);
    }
}
