package edu.calpoly.eye;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Class to test the REST client
 * Run this file directly to start the test server
 *
 * @author James Yaguma
 * @version 1.0 (2026-09-30)
 */
public class TestGazeRestClient {
    private static final String URL = "http://localhost:8080/gaze";

    /**
     * Main method
     * Run to start sending REST server
     *
     * @param args
     */
    public static void main(String[] args) {
        TestGazeMessages messages = new TestGazeMessages();
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/gaze", (exchange) -> {
                byte[] payload = "{\"error\":\"Method not allowed\"}".getBytes();
                int status = 405;
                if (exchange.getRequestMethod() != "GET") {
                    payload = messages.getNextBytes();
                    status = 200;
                }
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(status, payload.length);
                exchange.getResponseBody().write(payload);
                exchange.close();
            });

            server.start();
        } catch (IOException e) {
            System.out.println("Server failed ot be created: " + e.getMessage());
        }
    }
}
