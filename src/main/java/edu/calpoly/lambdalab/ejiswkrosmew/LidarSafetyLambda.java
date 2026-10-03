package edu.calpoly.lambdalab.ejiswkrosmew;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

/**
 * AWS Lambda function that checks the safety of Lidar distances read
 * Gives a "warning" status if any distances read is too close (relative to a threshold)
 *
 * @author James Yaguma
 * @version 1.0 (2026-09-30)
 */
public class LidarSafetyLambda implements RequestHandler<Map<String, Object>, Map<String, Object>> {
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Handle a request
     *
     * @param input The Lambda Function input
     * @param context The Lambda execution environment context object.
     * @return The HTTP response as a Map<String, Object>
     */
    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        try {

            Map<String, Object> body = mapper.readValue(input.get("body").toString(), Map.class);

            List<Double> distances = (List<Double>) body.get("distances");
            double threshold = (Double) body.get("threshold");

            Map<String, Object> response = safetyCheck(distances, threshold);
            return httpResponse(200, mapper.writeValueAsString(response));
        } catch (Exception e) {
            // System.err.println("Invalid Request Received due to: " + e.getMessage());
            return httpResponse(400, "{\"error\":\"Invalid Request\"");
        }
    }

    /**
     * Perform a safety check to ensure no distance measured was too close
     * Given threshold determines what is too close
     *
     * @param distances A list of distances measured to safety check
     * @param threshold The threshold that notes what distance becomes too close
     * @return A map of String to Object containing the safety status and minimum distance recorded
     */
    private Map<String, Object> safetyCheck(List<Double> distances, double threshold) {
        double minDist = Integer.MAX_VALUE;
        for (double dist : distances) {
            if (dist < 0) {
                throw new IllegalArgumentException("Invalid distance value: A distance of " + dist + " was found which is not greater than 0.");
            }
            if (minDist > dist) {
                minDist = dist;
            }
        }

        String status = "SAFE";
        if (minDist < threshold) {
            status = "WARNING";
        }
        return Map.of(
                "status", status,
                "minDist", minDist
        );
    }

    /**
     * Generate an HTTP response in the form of a Map<String, Object>
     *
     * @param statusCode The status code to use
     * @param body The body of the HTTP response
     * @return A Map<String, Object> representation of an HTTP response
     */
    private Map<String, Object> httpResponse(int statusCode, String body) {
        return Map.of(
                "statusCode", statusCode,
                "headers", Map.of("Content-Type", "application/json"),
                "body", body
        );
    }
}
