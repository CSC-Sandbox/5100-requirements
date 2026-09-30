package edu.calpoly.lambdalab.jjuangon03;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * Lambda adapter for gaze clustering.
 *
 * The DBSCAN algorithm is provided in DBSCAN.java.
 * Complete the API Gateway/Lambda integration for this lab.
 *
 * @author YOUR_NAME
 * @version 1.0
 */
public class GazeClusterLambda
        implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Map<String, Object> handleRequest(
            Map<String, Object> event,
            Context context) {

        // TODO: Read and deserialize the POST body.
        // TODO: Validate points, epsilon, and minPoints.
        // TODO: Convert request points to DBSCAN.GazePoint objects.
        // TODO: Call DBSCAN.findClusters(...).
        // TODO: Build the JSON HTTP response.
        // TODO: Return HTTP 400 for invalid input.

        throw new UnsupportedOperationException(
                "Complete the Lambda adapter");
    }
}
