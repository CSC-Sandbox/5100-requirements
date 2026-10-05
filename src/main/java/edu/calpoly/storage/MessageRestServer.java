package edu.calpoly.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import edu.calpoly.message.MessageValidator;
import edu.calpoly.storage.FileMessageReader;
import edu.calpoly.storage.FileMessageStore;
import edu.calpoly.storage.MessageService;
import edu.calpoly.storage.StoreMessageRequest;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * Minimal REST interface over MessageService:
 *   POST /messages  - store a message. Body: {"message": "..."}
 *   GET  /messages  - retrieve all stored messages as a JSON array.
 *
 * Delegates all domain logic to MessageService (the same class the MQTT
 * interface uses) - mirrors the structure of the course-provided
 * TemperatureRestServer.
 *
 * NOTE: MessageValidator currently comes from edu.calpoly.message. There is
 * an apparently-duplicate edu.calpoly.provided.MessageValidator with
 * identical logic - confirm with the team which one is canonical before
 * this ships.
 *
 * @author Edgard Aviles
 */
public class MessageRestServer {

    private final MessageService service;
    private final MessageValidator validator = new MessageValidator();
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpServer server;

    public MessageRestServer(MessageService service, int port) throws IOException {
        this.service = service;
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/messages", this::handleMessages);
    }

    public void start() {
        server.start();
        System.out.println("REST provider: http://localhost:" + server.getAddress().getPort() + "/messages");
    }

    public void stop() {
        server.stop(0);
    }

    private void handleMessages(HttpExchange exchange) throws IOException {
        try {
            switch (exchange.getRequestMethod()) {
                case "GET" -> sendJson(exchange, 200, mapper.writeValueAsString(service.retrieveMessages()));
                case "POST" -> handlePost(exchange);
                default -> {
                    exchange.getResponseHeaders().set("Allow", "GET, POST");
                    sendJson(exchange, 405, "{\"error\":\"Method not allowed\"}");
                }
            }
        } catch (IllegalArgumentException e) {
            sendJson(exchange, 400, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
        } catch (Exception e) {
            sendJson(exchange, 400, "{\"error\":\"Invalid JSON\"}");
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        StoreMessageRequest request = mapper.readValue(exchange.getRequestBody(), StoreMessageRequest.class);

        if (request.message() == null) {
            sendJson(exchange, 400, "{\"error\":\"'message' field is required\"}");
            return;
        }
        if (!validator.validate(request.message())) {
            sendJson(exchange, 400, "{\"error\":\"" + escapeJson(validator.getLastError()) + "\"}");
            return;
        }

        service.storeMessage(request.message());
        sendJson(exchange, 201, "{\"status\":\"stored\"}");
    }

    private void sendJson(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }

    private static String escapeJson(String s) {
        return s == null ? "" : s.replace("\"", "'");
    }

    public static void main(String[] args) throws IOException {
        FileMessageStore store = new FileMessageStore("data/stored-messages.csv");
        FileMessageReader reader = new FileMessageReader("data/stored-messages.csv");
        new MessageRestServer(new MessageService(store, reader), 8080).start();
    }
}
