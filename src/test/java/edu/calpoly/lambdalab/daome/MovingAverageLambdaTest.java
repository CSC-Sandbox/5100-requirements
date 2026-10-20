package edu.calpoly.lambdalab.daome;


import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class MovingAverageLambdaTest {
    @Test
    void calculateMovingAverage() {
        var lambda = new MovingAverageLambda();
        Map<String, Object> event = new HashMap<>();
        event.put("body", "{\"samples\": [1, 2, 3, 4, 5],\"window\": 3}");
        Map<String, Object> response = lambda.handleRequest(event, null);
        assertEquals(200, response.get("statusCode"));
        assertTrue(response.get("body").toString().contains("\"movingAverage\":[2.0,3.0,4.0]"));
    }

    @Test
    void calculateMovingAverageWindowBoundary() {
        var lambda = new MovingAverageLambda();
        Map<String, Object> event = new HashMap<>();
        event.put("body", "{\"samples\": [1, 2, 3, 4, 5],\"window\": 5}");
        Map<String, Object> response = lambda.handleRequest(event, null);
        assertEquals(200, response.get("statusCode"));
        assertTrue(response.get("body").toString().contains("\"movingAverage\":[3.0]"));
    }


    @Test
    void calculateMovingAverageInvalidWindowInput() {
        var lambda = new MovingAverageLambda();
        Map<String, Object> event = new HashMap<>();
        event.put("body", "{\"samples\": [1, 2, 3, 4, 5],\"window\": 10}");
        Map<String, Object> response = lambda.handleRequest(event, null);
        assertEquals(400, response.get("statusCode"));
    }
}
