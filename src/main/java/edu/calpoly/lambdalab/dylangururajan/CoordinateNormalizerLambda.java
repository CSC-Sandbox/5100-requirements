package edu.calpoly.lambdalab.dylangururajan;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.Map;

// AWS Lambda function that converts pixel coordinates to normalized values
public class CoordinateNormalizerLambda
    implements RequestHandler<Map<String, Object>, Map<String, Object>> {

  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  public Map<String, Object> handleRequest(Map<String, Object> event, Context context) {
    try {
      Map<String, Object> queryParameters = queryParameters(event);
      double x = parseFiniteNumber(queryParameters, "x");
      double y = parseFiniteNumber(queryParameters, "y");
      double width = parseFiniteNumber(queryParameters, "width");
      double height = parseFiniteNumber(queryParameters, "height");

      Map<String, Double> normalized = normalizeCoordinates(x, y, width, height);
      return httpResponse(200, mapper.writeValueAsString(normalized));
    } catch (Exception exception) {
      return httpResponse(400, "{\"error\":\"Invalid request\"}");
    }
  }

  // Normalizes a point
  public Map<String, Double> normalizeCoordinates(
      double x, double y, double width, double height) {
    if (!Double.isFinite(x)
        || !Double.isFinite(y)
        || !Double.isFinite(width)
        || !Double.isFinite(height)
        || width <= 0
        || height <= 0
        || x < 0
        || x > width
        || y < 0
        || y > height) {
      throw new IllegalArgumentException("Coordinates and dimensions must describe a valid point");
    }

    Map<String, Double> result = new LinkedHashMap<>();
    result.put("normalizedX", x / width);
    result.put("normalizedY", y / height);
    return result;
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> queryParameters(Map<String, Object> event) {
    if (event == null || !(event.get("queryStringParameters") instanceof Map<?, ?>)) {
      throw new IllegalArgumentException("Query parameters are required");
    }
    return (Map<String, Object>) event.get("queryStringParameters");
  }

  private double parseFiniteNumber(Map<String, Object> input, String name) {
    Object value = input.get(name);
    if (value == null) {
      throw new IllegalArgumentException("Missing query parameter: " + name);
    }
    double number = Double.parseDouble(value.toString());
    if (!Double.isFinite(number)) {
      throw new IllegalArgumentException("Query parameter must be finite: " + name);
    }
    return number;
  }

  private Map<String, Object> httpResponse(int statusCode, String body) {
    Map<String, Object> response = new LinkedHashMap<>();
    response.put("statusCode", statusCode);
    response.put("headers", Map.of("Content-Type", "application/json"));
    response.put("body", body);
    return response;
  }
}
