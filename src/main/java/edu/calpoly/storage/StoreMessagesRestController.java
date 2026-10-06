package edu.calpoly.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;


public class StoreMessagesRestController {


    public static void storeMessage(HttpExchange exchange, ObjectMapper mapper, FileMessageStore fileStore) throws IOException {
        try (exchange) {

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            StoreMessageRequest messageRecord;

            try (var inputStream = exchange.getRequestBody()) {
                messageRecord = mapper.readValue(inputStream, StoreMessageRequest.class);
            } catch (JsonProcessingException e) {
                byte[] errorBytes = "Invalid JSON payload".getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(400, errorBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(errorBytes);
                }
                return;
            }

            fileStore.store(new MessageRecord(LocalDateTime.now().toString(), messageRecord.message()));

            var response = "Content Created".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, response.length);

            try (var os = exchange.getResponseBody()) {
                os.write(response);
            }
        }
    }


}
