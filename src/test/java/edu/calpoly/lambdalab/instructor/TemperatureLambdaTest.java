package edu.calpoly.lambdalab.instructor;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the TemperatureLambda class.
 *
 * @author Javier Gonzalez-Sanchez
 * @version 1.0
 */
class TemperatureLambdaTest {

  @Test
  void convertsFahrenheitToCelsius() {
    TemperatureLambda lambda = new TemperatureLambda();
    Map<String, Object> event = new HashMap<>();
    event.put("body", "{\"value\":32,\"from\":\"F\",\"to\":\"C\"}");
    Map<String, Object> response = lambda.handleRequest(event, null);
    assertEquals(200, response.get("statusCode"));
    assertTrue(response.get("body").toString().contains("\"result\":0.0"));
  }

  @Test
  void convertsCelsiusToFahrenheit() {
    TemperatureLambda lambda = new TemperatureLambda();
    Map<String, Object> event = new HashMap<>();
    event.put("body", "{\"value\":100,\"from\":\"C\",\"to\":\"F\"}");
    Map<String, Object> response = lambda.handleRequest(event, null);
    assertEquals(200, response.get("statusCode"));
    assertTrue(response.get("body").toString().contains("\"result\":212.0"));
  }

  @Test
  void rejectsUnsupportedConversion() {
    TemperatureLambda lambda = new TemperatureLambda();
    Map<String, Object> event = new HashMap<>();
    event.put("body", "{\"value\":100,\"from\":\"C\",\"to\":\"K\"}");
    Map<String, Object> response = lambda.handleRequest(event, null);
    assertEquals(400, response.get("statusCode"));
  }

}