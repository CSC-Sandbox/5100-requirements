package edu.calpoly.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * REST Interface for RetrieveMessages that will get messages from server.
 *
 * @author David Montiel
 * @version 1.0.0
 *
 */
public class RetrieveMessagesRestController {

    public static void retrieveMessages(HttpExchange exchange, ObjectMapper mapper, FileMessageReader fileReader) throws IOException {
        try (exchange) {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            List<String> records;

            try {
                records = fileReader.readAll();
            } catch (IOException e) {
                var response = "Error retrieving Messages".getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(500, response.length);

                try (var os = exchange.getResponseBody()) {
                    os.write(response);
                }

                return;
            }

            var recordsStream = records.stream().map(i -> {
                String[] split = i.split(",");
                assert (split.length == 2) : "Incorrectly formatted file";
                return new MessageRecord(split[0], split[1]);
            }).toList();

            byte[] responseBody = mapper.writeValueAsBytes(recordsStream);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(200, responseBody.length);
            try (var os = exchange.getResponseBody()) {
                os.write(responseBody);
            }

        }
    }
}
