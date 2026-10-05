package edu.calpoly.lambdalab.briggsmurphy;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the LidarClosestLambda class.
 *
 * @author Briggs Murphy
 * @version 1.0
 */
class LidarClosestLambdaTest {

  @Test
  void findsClosestDistance() {
    LidarClosestLambda lambda = new LidarClosestLambda();

    Map<String, Object> event = new HashMap<>();
    event.put(
        "body",
        "{\"distances\":[1.4,0.8,2.1,0.5,1.7]}"
    );

    Map<String, Object> response = lambda.handleRequest(event, null);

    assertEquals(200, response.get("statusCode"));
    assertTrue(response.get("body").toString().contains("\"distance\":0.5"));
    assertTrue(response.get("body").toString().contains("\"index\":3"));
  }

  @Test
  void handlesSingleValue() {
    LidarClosestLambda lambda = new LidarClosestLambda();

    Map<String, Object> event = new HashMap<>();
    event.put("body", "{\"distances\":[4.2]}");

    Map<String, Object> response = lambda.handleRequest(event, null);

    assertEquals(200, response.get("statusCode"));
    assertTrue(response.get("body").toString().contains("\"distance\":4.2"));
    assertTrue(response.get("body").toString().contains("\"index\":0"));
  }

  @Test
  void rejectsEmptyArray() {
    LidarClosestLambda lambda = new LidarClosestLambda();

    Map<String, Object> event = new HashMap<>();
    event.put("body", "{\"distances\":[]}");

    Map<String, Object> response = lambda.handleRequest(event, null);

    assertEquals(400, response.get("statusCode"));
  }

  @Test
  void rejectsInvalidDistance() {
    LidarClosestLambda lambda = new LidarClosestLambda();

    Map<String, Object> event = new HashMap<>();
    event.put("body", "{\"distances\":[1.0,\"invalid\",2.0]}");

    Map<String, Object> response = lambda.handleRequest(event, null);

    assertEquals(400, response.get("statusCode"));
  }
}