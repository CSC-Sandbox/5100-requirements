package edu.calpoly.lambdalab.daome;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Type;
import java.util.*;

public class MovingAverageLambda implements RequestHandler<Map<String, Object>, Map<String, Object>> {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> event, Context context) {
        try {
            Map<String, Object> input;

            String body = event.get("body").toString();
            input = mapper.readValue(body, new TypeReference<Map<String, Object>>() {
            });

            List<Integer> samples = mapper.convertValue(input.get("samples"), new TypeReference<List<Integer>>() {
            });
            Integer windowSize = mapper.convertValue(input.get("window"), Integer.class);
            var movingAverages = movingAverage(samples, windowSize);
            Map<String, Object> output = new HashMap<>();
            output.put("movingAverage", movingAverages);
            return httpResponse(200, mapper.writeValueAsString(output));
        } catch (Exception e) {
            return httpResponse(400, "{\"error\":\"Invalid request\"}");
        }

    }

    private List<Double> movingAverage(List<Integer> numbers, Integer windowSize) {
        if (windowSize > numbers.size() || windowSize < 0) {
            throw new IllegalArgumentException("Window Size Bigger than List");
        }

        List<Double> result = new ArrayList<>(windowSize);
        for (int i = 0; i <= numbers.size() - windowSize; i++) {
            var sum = 0;
            for (int j = 0; j < windowSize; j++) {
                sum += numbers.get(i + j);
            }

            result.add(1.0 * sum / windowSize);
        }
        return result;
    }

    private Map<String, Object> httpResponse(int statusCode, String body) {
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", statusCode);
        response.put("headers", Map.of("Content-Type", "application/json"));
        response.put("body", body);
        return response;
    }

}

