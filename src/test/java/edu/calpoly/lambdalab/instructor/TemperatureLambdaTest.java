package edu.calpoly.lambdalab.instructor;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    Map<String, Object> input = new HashMap<>();
    input.put("value", 32.0);
    input.put("from", "F");
    input.put("to", "C");
    Map<String, Object> response = lambda.handleRequest(input, null);
    assertEquals(0.0, ((Number) response.get("result")).doubleValue(), 0.01);
  }

  @Test
  void convertsCelsiusToFahrenheit() {
    TemperatureLambda lambda = new TemperatureLambda();
    Map<String, Object> input = new HashMap<>();
    input.put("value", 100.0);
    input.put("from", "C");
    input.put("to", "F");
    Map<String, Object> response = lambda.handleRequest(input, null);
    assertEquals(212.0, ((Number) response.get("result")).doubleValue(), 0.01);
  }

  @Test
  void rejectsUnsupportedConversion() {
    TemperatureLambda lambda = new TemperatureLambda();
    Map<String, Object> input = new HashMap<>();
    input.put("value", 100.0);
    input.put("from", "C");
    input.put("to", "K");
    assertThrows(IllegalArgumentException.class, () -> lambda.handleRequest(input, null));
  }

}