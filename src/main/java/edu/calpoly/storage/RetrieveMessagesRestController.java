package edu.calpoly.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

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

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final FileMessageReader fileReader;

    public RetrieveMessagesRestController(String fileName) throws IOException {
        this.fileReader = new FileMessageReader(fileName);
    }

    private void retrieveMessages(HttpExchange exchange) throws IOException {
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
            });

            byte[] responseBody = OBJECT_MAPPER.writeValueAsBytes(records);
            exchange.sendResponseHeaders(200, responseBody.length);
            try (var os = exchange.getResponseBody()) {
                os.write(responseBody);
            }

        }
    }
}
