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
    private final GazeBlackboard blackboard;

    /**
     * Default constructor
     * Use localhost:8080 and GazeBlackboard by default
     */
    GazeRestClient() {
        this(GazeBlackboard.getInstance() ,"http://localhost:8080/gaze");
    }

    /**
     * Constructor with blackboard and URL specified
     * This needs the full path (not just host and port)
     *
     * @param blackboard The blackboard to put new gaze data into
     * @param URL The url to connect to for http requests
     */
    GazeRestClient(GazeBlackboard blackboard, String URL) {
        this.blackboard = blackboard;
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

    public void loopForever() throws IOException, InterruptedException {
        loopForever(150);
    }

    public void loopForever(int ms) throws IOException, InterruptedException{
        while(!Thread.currentThread().isInterrupted()) {
            blackboard.updateGazePoint(getGaze());
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    // Main method for testing purposes
    public static void main(String[] args) {
        GazeRestClient client = new GazeRestClient();
        try {
            GazePoint gp = client.getGaze();
            System.out.println("New GazePoint received: (" + gp.x + ", " + gp.y + ")");
        } catch (Exception e) {
            System.out.println("GetGaze got error: " + e.getMessage());
        }
    }
}
