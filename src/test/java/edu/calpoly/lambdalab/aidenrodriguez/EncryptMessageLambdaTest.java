package edu.calpoly.lambdalab.aidenrodriguez;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the EncryptMessageLambda class.
 *
 * @author Aiden Rodriguez
 * @version 1.0
 */
class EncryptMessageLambdaTest {

  @Test
  void encryptsHelloWithShiftThree() {
    EncryptMessageLambda lambda = new EncryptMessageLambda();
    Map<String, Object> queryParameters = new HashMap<>();
    queryParameters.put("message", "Hello");
    queryParameters.put("shift", "3");
    Map<String, Object> event = new HashMap<>();
    event.put("queryStringParameters", queryParameters);
    Map<String, Object> response = lambda.handleRequest(event, null);
    assertEquals(200, response.get("statusCode"));
    assertTrue(response.get("body").toString().contains("\"original\":\"Hello\""));
    assertTrue(response.get("body").toString().contains("\"encrypted\":\"Khoor\""));
  }

  @Test
  void wrapsXyzAroundToAbc() {
    EncryptMessageLambda lambda = new EncryptMessageLambda();
    Map<String, Object> queryParameters = new HashMap<>();
    queryParameters.put("message", "XYZ");
    queryParameters.put("shift", "3");
    Map<String, Object> event = new HashMap<>();
    event.put("queryStringParameters", queryParameters);
    Map<String, Object> response = lambda.handleRequest(event, null);
    assertEquals(200, response.get("statusCode"));
    assertTrue(response.get("body").toString().contains("\"encrypted\":\"ABC\""));
  }

  @Test
  void rejectsMissingMessage() {
    EncryptMessageLambda lambda = new EncryptMessageLambda();
    Map<String, Object> queryParameters = new HashMap<>();
    queryParameters.put("shift", "3");
    Map<String, Object> event = new HashMap<>();
    event.put("queryStringParameters", queryParameters);
    Map<String, Object> response = lambda.handleRequest(event, null);
    assertEquals(400, response.get("statusCode"));
  }

  @Test
  void rejectsNonNumericShift() {
    EncryptMessageLambda lambda = new EncryptMessageLambda();
    Map<String, Object> queryParameters = new HashMap<>();
    queryParameters.put("message", "Hello");
    queryParameters.put("shift", "abc");
    Map<String, Object> event = new HashMap<>();
    event.put("queryStringParameters", queryParameters);
    Map<String, Object> response = lambda.handleRequest(event, null);
    assertEquals(400, response.get("statusCode"));
  }
}