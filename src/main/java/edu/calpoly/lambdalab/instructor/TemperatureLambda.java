package edu.calpoly.lambdalab.instructor;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

/**
 * AWS Lambda function that converts temperatures
 * between Fahrenheit and Celsius.
 *
 * @author Javier Gonzalez-Sanchez
 * @version 1.0
 */
public class TemperatureLambda implements RequestHandler<Map<String, Object>, Map<String, Object>> {

  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  public Map<String, Object> handleRequest(
      Map<String, Object> event,
      Context context) {
    try {
      String body = event.get("body").toString();
      Map<String, Object> input = mapper.readValue(body, Map.class);
      double value = ((Number) input.get("value")).doubleValue();
      String from = input.get("from").toString();
      String to = input.get("to").toString();
      double result = convertTemperature(value, from, to);
      Map<String, Object> output = new HashMap<>();
      output.put("value", value);
      output.put("from", from.toUpperCase());
      output.put("to", to.toUpperCase());
      output.put("result", result);
      return httpResponse(200, mapper.writeValueAsString(output));
    } catch (Exception e) {
      return httpResponse(400, "{\"error\":\"Invalid request\"}");
    }
  }

  private double convertTemperature(double value, String from, String to) {
    from = from.toUpperCase(); to = to.toUpperCase();
    if (from.equals("F") && to.equals("C")) {
      return (value - 32) * 5.0 / 9.0;
    }
    if (from.equals("C") && to.equals("F")) {
      return value * 9.0 / 5.0 + 32;
    }
    throw new IllegalArgumentException("Supported conversions are F to C and C to F.");
  }

  private Map<String, Object> httpResponse(int statusCode, String body) {
    Map<String, Object> response = new HashMap<>();
    response.put("statusCode", statusCode);
    response.put("headers", Map.of("Content-Type", "application/json"));
    response.put("body", body);
    return response;
  }

}