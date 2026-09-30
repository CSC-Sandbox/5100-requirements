package edu.calpoly.monitor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.EnumMap;
import java.util.Map;

/**
 * REST API Client for MonitorAvailability.
 * This class retrieves status information from the REST API server through {@code GET /availability}
 *
 * @author Howard Jiang (hwrd22)
 * @version 1.0 (September 29, 2026)
 */
public class AvailabilityRestClient {
    private static final String URL = "http://localhost:8080/availability";

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * This method sends a GET request to the REST API server
     * @return The parsed response from the REST API server
     * @throws Exception when the REST API server is not working.
     */
    public Map<DataSource, Boolean> getAvailability() throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(URL)).GET().build();
        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
        if (response.statusCode() != 200) {
            throw new IllegalStateException(
                    "GET /availability returned HTTP "
                    + response.statusCode()
            );
        }

        return parseAvailability(response.body());
    }

    /**
     *
     * @param json The JSON retrieved from the REST API server
     * @return A map of each Data Source and their availability (as a boolean)
     * @throws Exception If the response doesn't have anything
     */
    public Map<DataSource, Boolean> parseAvailability(String json) throws Exception {
        Map<String, String> values = mapper.readValue(
                json,
                new TypeReference<Map<String, String>>() {
                }
        );

        Map<DataSource, Boolean> availability =  new EnumMap<>(DataSource.class);

        availability.put(
                DataSource.ROBOT,
                "AVAILABILITY".equals(values.get("robot"))
        );

        availability.put(
                DataSource.GAZE,
                "AVAILABILITY".equals(values.get("gaze"))
        );

        availability.put(
                DataSource.AFFECT,
                "AVAILABILITY".equals(values.get("affect"))
        );

        availability.put(
                DataSource.LIDAR,
                "AVAILABILITY".equals(values.get("lidar"))
        );

        return availability;
    }

    public static void main(String[] args) throws Exception {
        AvailabilityRestClient client = new AvailabilityRestClient();

        // Small loop to periodically retrieve from the API server (Demonstration purposes)
        while (true) {
            try {
                Map<DataSource, Boolean> availability =
                        client.getAvailability();

                System.out.println("Availability:");
                for (DataSource source : DataSource.values()) {
                    System.out.println(
                            source.displayName() + ": "
                                    + (availability.get(source)
                            ? "AVAILABLE" : "UNAVAILABLE"));
                }

                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }
}
