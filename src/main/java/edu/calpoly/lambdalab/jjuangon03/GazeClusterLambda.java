package edu.calpoly.lambdalab.jjuangon03;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

        try {
            // Read and deserialize the POST body.
            String body = event.get("body").toString();
            Map<String, Object> input = mapper.readValue(body, Map.class);

            // Validate points, epsilon, and minPoints.
            List<Map<String, Object>> rawPoints =
                    (List<Map<String, Object>>) input.get("points");
            double epsilon = Double.parseDouble(input.get("epsilon").toString());
            int minPoints = Integer.parseInt(input.get("minPoints").toString());
            if (rawPoints == null || rawPoints.isEmpty()
                    || epsilon <= 0 || minPoints <= 0) {
                throw new IllegalArgumentException("Invalid input");
            }

            // Convert request points to DBSCAN.GazePoint objects.
            List<DBSCAN.GazePoint> points = new ArrayList<>();
            for (Map<String, Object> p : rawPoints) {
                double x = Double.parseDouble(p.get("x").toString());
                double y = Double.parseDouble(p.get("y").toString());
                points.add(new DBSCAN.GazePoint(x, y));
            }

            // Call DBSCAN.findClusters(...).
            List<DBSCAN.Cluster> clusters =
                    DBSCAN.findClusters(points, epsilon, minPoints);

            // Build the JSON HTTP response.
            List<Map<String, Object>> clusterList = new ArrayList<>();
            for (DBSCAN.Cluster c : clusters) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", c.id());
                item.put("centerX", c.centerX());
                item.put("centerY", c.centerY());
                item.put("points", c.points());
                clusterList.add(item);
            }
            Map<String, Object> output = new HashMap<>();
            output.put("numberOfClusters", clusterList.size());
            output.put("clusters", clusterList);
            return httpResponse(200, mapper.writeValueAsString(output));

        } catch (Exception e) {
            // Return HTTP 400 for invalid input.
            return httpResponse(400, "{\"error\":\"Invalid request\"}");
        }
    }

    private Map<String, Object> httpResponse(int statusCode, String body) {
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", statusCode);
        response.put("headers", Map.of("Content-Type", "application/json"));
        response.put("body", body);
        return response;
    }
}