package edu.calpoly.eye;

import com.sun.net.httpserver.HttpServer;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;
import java.net.InetSocketAddress;

public class TestGazeRestClient {
    private static final String URL = "http://localhost:8080/gaze";

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
