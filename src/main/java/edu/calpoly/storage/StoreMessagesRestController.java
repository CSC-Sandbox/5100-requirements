package edu.calpoly.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import javax.print.DocFlavor;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;


public class StoreMessagesRestController {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final FileMessageStore fileStore;
    private final HttpServer server;

    public StoreMessagesRestController(String fileName) throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", 5637), 50);
        server.createContext("/messages", this::storeMessage);
        fileStore = new FileMessageStore(fileName);
    }

    public static void main(String[] args) throws IOException {
        var messageController = new StoreMessagesRestController("data/store-input.txt");

        messageController.start();

    }

    private void storeMessage(HttpExchange exchange) throws IOException {
        try (exchange) {

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            MessageRecord messageRecord;

            try (var inputStream = exchange.getRequestBody();) {
                messageRecord = OBJECT_MAPPER.readValue(inputStream, MessageRecord.class);
            } catch (JsonProcessingException e) {
                byte[] errorBytes = "Invalid JSON payload".getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(400, errorBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(errorBytes);
                }
                return;
            }

            fileStore.store(messageRecord);

            var response = "Content Created".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, response.length);

            try (var os = exchange.getResponseBody();) {
                os.write(response);
            }
        }
    }

    public void start() {
        System.out.println("Start server on:" + server.getAddress());
        server.start();
    }
}
