package edu.calpoly.lambdalab.hwrd22;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * AWS Lambda function that checks if the age of a sensor's data
 * is still fresh according to a user-provided threshold.
 *
 * @author Howard Jiang (hwrd22)
 * @version 1.0 (October 2, 2026)
 */
public class DataFreshnessLambda implements RequestHandler<Map<String, Object>, Map<String, Object>> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> event, Context context) {
        try {
            Map<String, Object> input;
            // GET
            if (event.get("queryStringParameters") != null) {
                input = (Map<String, Object>)
                        event.get("queryStringParameters");
            }
            // POST
            else {
                String body = event.get("body").toString();
                input = mapper.readValue(body, Map.class);
            }

            long current = Long.parseLong(input.get("current").toString());
            long sample = Long.parseLong(input.get("sample").toString());
            long threshold = Long.parseLong(input.get("threshold").toString());

            Map<String, Object> output = determineStatus(current, sample, threshold);
            return httpResponse(200, mapper.writeValueAsString(output));
        } catch (Exception e) {
            return httpResponse(400, "{\"error\":\"Invalid request\"}");
        }
    }

    private Map<String, Object> determineStatus(long current, long sample, long threshold) {
        if (current < 0 || sample < 0 || threshold < 0) {
            throw new IllegalArgumentException("Times and threshold must be non-negative.");
        }

        if (sample > current) {
            throw new IllegalArgumentException("Sample time cannot later than current.");
        }

        long age = current - sample;
        String status = age <= threshold ? "FRESH" : "STALE";  // If age has not exceeded threshold, then it is fresh.

        Map<String, Object> output = new HashMap<>();
        output.put("status", status);
        output.put("age", age);
        return output;
    }

    private Map<String, Object> httpResponse(int statusCode, String body) {
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", statusCode);
        response.put("headers", Map.of("Content-Type", "application/json"));
        response.put("body", body);
        return response;
    }
}
