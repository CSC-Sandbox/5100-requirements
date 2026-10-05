package edu.calpoly.lambdalab.adrian0427;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class DominantAffectLambda
        implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Map<String, Object> handleRequest(
            Map<String, Object> event, Context context) {

        try {
            if (event == null || event.get("body") == null) {
                throw new IllegalArgumentException("Missing request body");
            }

            String body = event.get("body").toString();

            Map<String, Object> input = mapper.readValue(body, Map.class);

            Map.Entry<String, Double> dominant = findDominantAffect(input);

            Map<String, Object> output = new HashMap<>();
            output.put("metric", dominant.getKey());
            output.put("value", dominant.getValue());

            return httpResponse(200, mapper.writeValueAsString(output));

        } catch (Exception e) {
            return httpResponse(400, "{\"error\":\"Invalid request\"}");
        }
    }

    private Map.Entry<String, Double> findDominantAffect(
            Map<String, Object> affectValues) {

        if (affectValues == null || affectValues.isEmpty()) {
            throw new IllegalArgumentException("Affect values cannot be empty");
        }

        String dominantMetric = null;
        double dominantValue = Double.NEGATIVE_INFINITY;

        for (Map.Entry<String, Object> entry : affectValues.entrySet()) {
            double value;

            try {
                value = Double.parseDouble(entry.getValue().toString());
            } catch (Exception e) {
                throw new IllegalArgumentException("All affect values must be numeric");
            }

            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException("Affect values must be finite");
            }

            if (dominantMetric == null
                    || value > dominantValue
                    || (value == dominantValue
                    && entry.getKey().compareTo(dominantMetric) < 0)) {

                dominantMetric = entry.getKey();
                dominantValue = value;
            }
        }

        return Map.entry(dominantMetric, dominantValue);
    }

    private Map<String, Object> httpResponse(int statusCode, String body) {
        Map<String, Object> response = new HashMap<>();

        response.put("statusCode", statusCode);
        response.put(
                "headers",
                Map.of("Content-Type", "application/json"));
        response.put("body", body);

        return response;
    }
}