package edu.calpoly.lambdalab.instructor;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
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

  /**
   * AWS Lambda entry point.
   *
   * @param input   JSON-compatible input containing value, from, and to
   * @param context AWS Lambda execution context
   * @return conversion result
   */
  @Override
  public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {

    double value = ((Number) input.get("value")).doubleValue();
    String from = input.get("from").toString();
    String to = input.get("to").toString();
    double result = convertTemperature(value, from, to);
    Map<String, Object> response = new HashMap<>();
    response.put("value", value);
    response.put("from", from.toUpperCase());
    response.put("to", to.toUpperCase());
    response.put("result", result);
    return response;
  }

  /**
   * Converts a temperature between Fahrenheit and Celsius.
   *
   * @param value temperature value
   * @param from  source unit
   * @param to    destination unit
   * @return converted temperature
   * @throws IllegalArgumentException if the conversion is unsupported
   */
  private double convertTemperature(double value, String from, String to) {
    from = from.toUpperCase();
    to = to.toUpperCase();
    if (from.equals("F") && to.equals("C")) {
      return (value - 32) * 5.0 / 9.0;
    }
    if (from.equals("C") && to.equals("F")) {
      return value * 9.0 / 5.0 + 32;
    }
    throw new IllegalArgumentException("Supported conversions are F to C and C to F.");
  }

}