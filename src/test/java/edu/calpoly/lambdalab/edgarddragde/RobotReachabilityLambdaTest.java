package edu.calpoly.lambdalab.edgarddragde;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;


class RobotReachabilityLambdaTest {

  @Test
  void insideRadius() {
    RobotReachabilityLambda lambda = new RobotReachabilityLambda();
    Map<String, Object> event = new HashMap<>();
    Map<String, Object> param = new HashMap<>();
    param.put("x", "3"); param.put("y", "4"); param.put("z", "5"); param.put("radius", "10");
    event.put("queryStringParameters", param);
    Map<String, Object> response = lambda.handleRequest(event, null);
    assertEquals(200, response.get("statusCode"));
    assertTrue(response.get("body").toString().contains("\"reachable\":true"));
  }


  @Test
  void onRadius() {
    RobotReachabilityLambda lambda = new RobotReachabilityLambda();
    Map<String, Object> event = new HashMap<>();
    Map<String, Object> param = new HashMap<>();

    param.put("x", "0.5"); param.put("y", "1"); param.put("z", "1"); param.put("radius", "2.25");
    event.put("queryStringParameters", param);
    Map<String, Object> response = lambda.handleRequest(event, null);
    assertEquals(200, response.get("statusCode"));
    assertTrue(response.get("body").toString().contains("\"reachable\":true"));
  }

  @Test
  void invalidInput() {
    RobotReachabilityLambda lambda = new RobotReachabilityLambda();
    Map<String, Object> event = new HashMap<>();
    Map<String, Object> param = new HashMap<>();

    param.put("x", "HI"); param.put("y", "1"); param.put("z", "1"); param.put("radius", "1.5");
    event.put("queryStringParameters", param);
    Map<String, Object> response = lambda.handleRequest(event, null);
    assertEquals(400, response.get("statusCode"));
  }
}
