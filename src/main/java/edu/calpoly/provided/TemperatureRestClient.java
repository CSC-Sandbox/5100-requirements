package edu.calpoly.provided;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * REST consumer for the temperature example.
 *
 * Run TemperatureRestServer first, then run this class.
 *
 * @author Javier Gonzalez-Sanchez
 * @version 1.0 (2026-09-25)
 */
public class TemperatureRestClient {

    private static final String URL = "http://localhost:8080/temperature";

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public Temperature getTemperature() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(URL)).GET().build();
        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("REST request failed: HTTP " + response.statusCode());
        }
        return mapper.readValue(response.body(), Temperature.class);
    }

    public Temperature setTemperature(Temperature temperature)
            throws IOException, InterruptedException {
        String json = mapper.writeValueAsString(temperature);
        HttpRequest request = HttpRequest.newBuilder(URI.create(URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("REST request failed: HTTP " + response.statusCode());
        }
        return mapper.readValue(response.body(), Temperature.class);
    }

    public static void main(String[] args) throws Exception {
        TemperatureRestClient client = new TemperatureRestClient();

        System.out.println("Current: " + client.getTemperature());
        System.out.println("Updated: " + client.setTemperature(new Temperature(21.0, "C")));
        System.out.println("Current: " + client.getTemperature());
    }
}
