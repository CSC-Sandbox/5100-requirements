package edu.calpoly.lambdalab.paulmotter;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the RobotDistanceLamnbda class.
 *
 * @author Paul Motter (PaulMotter)
 * @version 1.0
 */
class RobotDistanceLambdaTest {

    static private Map<String, Object>MakeEvent(
        double x1, double y1, double z1, double x2, double y2, double z2)
        {
            Map<String, Object> queryParams =
                Map.of(
                    "x1", Double.toString(x1), "y1", Double.toString(y1), "z1", Double.toString(z1),
                "x2", Double.toString(x2), "y2", Double.toString(y2), "z2", Double.toString(z2)
                );
            
            return Map.of("queryStringParameters", queryParams);
        }

    /**
     * Tests a known and valid distance.
     */
    @Test
    void calculateDistance(){
        // Setup
        RobotDistanceLambda lambda = new RobotDistanceLambda();
        Map<String, Object> event = MakeEvent(2, -2, 5, 4, -5, -1); 
        // Action
        Map<String, Object> response = lambda.handleRequest(event, null);
        // Check
        assertEquals(200, response.get("statusCode"));
        assertTrue(response.get("body").toString().contains("\"distance\":7.0"));
    }

    /**
     * Tests a distance of zero.
     */
    @Test 
    void calculateZeroDistance(){
        RobotDistanceLambda lambda = new RobotDistanceLambda();
        Map<String, Object> event = MakeEvent(2, 2, 2, 2, 2, 2); 
        // Action
        Map<String, Object> response = lambda.handleRequest(event, null);
        // Check
        assertEquals(200, response.get("statusCode"));
        assertTrue(response.get("body").toString().contains("\"distance\":0.0"));
    }

    /**
     * Tests catching an invalid input with one vector only having 2 dimensions.
     */
    @Test 
    void invalidInputTooFewDimensions(){
        // Setup
        RobotDistanceLambda lambda = new RobotDistanceLambda();
        Map<String, Object> queryParams = Map.of(
            "x1", "2", "y1", "3",
            "x2", "-1", "y2", "5", "z2", "8" 
        );
        Map<String, Object> event = Map.of("queryStringParameters", queryParams);
        // Action
        Map<String, Object> response = lambda.handleRequest(event, null);
        // Check
        assertEquals(400, response.get("statusCode"));
        assertTrue(response.get("body").toString().contains("{\"error\":\"Invalid request\"}"));
    }

    /**
     * Tets the parsing of query params to fail correctly.
     */
    @Test 
    void invalidInputNoQueryParams(){
        // Setup
        RobotDistanceLambda lambda = new RobotDistanceLambda();
        Map<String, Object> event = new HashMap<String, Object>();
        // Action
        Map<String, Object> response = lambda.handleRequest(event, null);
        // Check
        assertEquals(400, response.get("statusCode"));
        assertTrue(response.get("body").toString().contains("{\"error\":\"Invalid request\"}"));
    }

    /**
     * Test that NaN is caught since Float.parseFloat("NaN") doesn't error.
     */
    @Test 
    void invalidInputNaNPosition(){
        // Setup
        RobotDistanceLambda lambda = new RobotDistanceLambda();
        Map<String, Object> queryParams = Map.of(
            "x1", "2", "y1", "3", "z1", "NaN",
            "x2", "-1", "y2", "5", "z2", "8" 
        );
        Map<String, Object> event = Map.of("queryStringParameters", queryParams);
        // Action
        Map<String, Object> response = lambda.handleRequest(event, null);
        // Check
        assertEquals(400, response.get("statusCode"));
        assertTrue(response.get("body").toString().contains("{\"error\":\"Invalid request\"}"));
    }

    /**
     * Test that Infinity is caught since Float.parseFloat("Infinity") doesn't error.
     */
    @Test 
    void invalidInputInfinitePosition(){
        // Setup
        RobotDistanceLambda lambda = new RobotDistanceLambda();
        Map<String, Object> queryParams = Map.of(
            "x1", "2", "y1", "3", "z1", "Infinity",
            "x2", "-1", "y2", "5", "z2", "8" 
        );
        Map<String, Object> event = Map.of("queryStringParameters", queryParams);
        // Action
        Map<String, Object> response = lambda.handleRequest(event, null);
        // Check
        assertEquals(400, response.get("statusCode"));
        assertTrue(response.get("body").toString().contains("{\"error\":\"Invalid request\"}"));
    }

}