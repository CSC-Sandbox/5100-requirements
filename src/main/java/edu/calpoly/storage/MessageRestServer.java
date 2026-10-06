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
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * Minimal REST interface over MessageService:
 * POST /messages  - store a message. Body: {"message": "..."}
 * GET  /messages  - retrieve all stored messages as a JSON array.
 * <p>
 * Delegates all domain logic to MessageService (the same class the MQTT
 * interface uses) - mirrors the structure of the provided TemperatureRestServer.
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

    private static String escapeJson(String s) {
        return s == null ? "" : s.replace("\"", "'");
    }

    public static void main(String[] args) throws IOException {
        FileMessageStore store = new FileMessageStore("data/stored-messages.csv");
        FileMessageReader reader = new FileMessageReader("data/stored-messages.csv");
        new MessageRestServer(new MessageService(store, reader), 8080).start();
    }

    public void start() {
        server.start();
        System.out.println("REST provider: http://localhost:" + server.getAddress().getPort() + "/messages");
    }

    public void stop() {
        server.stop(0);
    }

    private void handleMessages(HttpExchange exchange) throws IOException {
        try (exchange) {
            switch (exchange.getRequestMethod()) {
                case "GET" -> {
                    RetrieveMessagesRestController.retrieveMessages(exchange, mapper, service.getReader());
                }
                case "POST" -> {
                    StoreMessagesRestController.storeMessage(exchange, mapper, service.getStore());
                }
                default -> {
                    exchange.getResponseHeaders().set("Allow", "GET, POST");
                    exchange.sendResponseHeaders(405, -1);
                }
            }
        } catch (IllegalArgumentException e) {
            var errorResponse = ("{\"error\":\"" + escapeJson(e.getMessage()) + "\"}").getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(400, errorResponse.length);
            exchange.getResponseBody().write(errorResponse);
        } catch (Exception e) {
            var errorResponse = "{\"error\":\"Invalid JSON\"}".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(400, errorResponse.length);

            var os = exchange.getResponseBody();
            os.write(errorResponse);
        }
    }

}
