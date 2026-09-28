package edu.calpoly.eye;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GazeRESTClient {
    private final String URL;

    private final HttpClient client = HttpClient.newHttpClient();

    // Default Constructor (default to localhost)
    GazeRESTClient() {
        this("https://localhost:8080/gaze");
    }

    // If there's a URL to listen on, use it instead
    GazeRESTClient(String URL) {
        this.URL = URL;
    }

    public GazePoint getGaze() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder(URI.create(URL)).GET().build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

        if (res.statusCode() != 200) {
            throw new IOException("REST request failed: HTTP " + res.statusCode());
        }
        return GazePoint.fromJSON(res.body());
    }
}
