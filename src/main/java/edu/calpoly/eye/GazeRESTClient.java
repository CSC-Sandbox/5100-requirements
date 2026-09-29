package edu.calpoly.eye;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * REST client for gaze data
 * Gaze data is expected to be a JSON object representing a GazePoint
 *
 * @author James Yaguma
 * @version 1.0 (2026-09-28)
 */
public class GazeRestClient {
    private final String URL;

    private final HttpClient client = HttpClient.newHttpClient();

    /**
     * Default constructor
     * Use localhost:8080 by default
     */
    GazeRestClient() {
        this("https://localhost:8080/gaze");
    }

    /**
     * Constructor with URL specified
     * This needs the full path (not just host and port)
     *
     * @param URL The url to connect to for http requests
     */
    GazeRestClient(String URL) {
        this.URL = URL;
    }

    /**
     * Attempt to GET the current GazePoint
     * Throws IOException if the request fails
     *
     * @return The GazePoint that was received from the REST interface
     */
    public GazePoint getGaze() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder(URI.create(URL)).GET().build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

        if (res.statusCode() != 200) {
            throw new IOException("REST request failed: HTTP " + res.statusCode());
        }
        return GazePoint.fromJSON(res.body());
    }

    // Main method for testing purposes
    public static void main(String[] args) {
        GazeRestClient client = new GazeRestClient();
        try {
            client.getGaze();
        } catch (Exception e) {
            System.out.println("GetGaze got error: " + e.getMessage());
        }
    }
}
