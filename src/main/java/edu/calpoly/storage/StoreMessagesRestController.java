package edu.calpoly.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;


public class StoreMessagesRestController {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final FileMessageStore fileStore;

    public StoreMessagesRestController(String fileName)   {
        fileStore = new FileMessageStore(fileName);
    }

    public static void main(String[] args) throws IOException {
        var server = HttpServer.create(new InetSocketAddress("localhost", 5637), 50);
        var storeMessagesRestController = new StoreMessagesRestController("data/store-input.txt");
        server.createContext("/messages", storeMessagesRestController::storeMessage);
        server.start();
    }

    public void storeMessage(HttpExchange exchange) throws IOException {
        try (exchange) {

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            MessageRecord messageRecord;

            try (var inputStream = exchange.getRequestBody()) {
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

            try (var os = exchange.getResponseBody()) {
                os.write(response);
            }
        }
    }


}
