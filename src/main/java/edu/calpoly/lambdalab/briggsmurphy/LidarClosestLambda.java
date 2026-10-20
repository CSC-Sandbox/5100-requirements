package edu.calpoly.lambdalab.briggsmurphy;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AWS Lambda function that finds the closest LiDAR measurement
 * and its index.
 *
 * @author Briggs Murphy
 * @version 1.0
 */
public class LidarClosestLambda
    implements RequestHandler<Map<String, Object>, Map<String, Object>> {

  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  public Map<String, Object> handleRequest(
      Map<String, Object> event, Context context) {
    try {
      String body = event.get("body").toString();
      Map<String, Object> input = mapper.readValue(body, Map.class);

      List<?> distances = (List<?>) input.get("distances");

      if (distances == null || distances.isEmpty()) {
        throw new IllegalArgumentException("Distances array is required.");
      }

      double[] values = new double[distances.size()];

      for (int i = 0; i < distances.size(); i++) {
        values[i] = Double.parseDouble(distances.get(i).toString());
      }

      Map<String, Object> result = findClosest(values);

      return httpResponse(200, mapper.writeValueAsString(result));
    } catch (Exception e) {
      return httpResponse(400, "{\"error\":\"Invalid request\"}");
    }
  }

  private Map<String, Object> findClosest(double[] distances) {
    int closestIndex = 0;
    double closestDistance = distances[0];

    for (int i = 1; i < distances.length; i++) {
      if (distances[i] < closestDistance) {
        closestDistance = distances[i];
        closestIndex = i;
      }
    }

    Map<String, Object> result = new HashMap<>();
    result.put("distance", closestDistance);
    result.put("index", closestIndex);

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