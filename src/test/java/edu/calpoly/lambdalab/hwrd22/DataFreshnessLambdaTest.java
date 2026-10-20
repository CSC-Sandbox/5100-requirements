package edu.calpoly.lambdalab.hwrd22;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DataFreshnessLambdaTest {

    @Test
    void freshStatus() {
        DataFreshnessLambda lambda = new DataFreshnessLambda();
        Map<String, Object> event = new HashMap<>();
        event.put("body", "{\"current\":100,\"sample\":95,\"threshold\":10}");
        Map<String, Object> response = lambda.handleRequest(event, null);
        assertEquals(200, response.get("statusCode"));
        assertTrue(response.get("body").toString().contains("\"status\":\"FRESH\""));
        assertTrue(response.get("body").toString().contains("\"age\":5"));
    }

    @Test
    void staleStatus() {
        DataFreshnessLambda lambda = new DataFreshnessLambda();
        Map<String, Object> event = new HashMap<>();
        event.put("body", "{\"current\":500,\"sample\":95,\"threshold\":10}");
        Map<String, Object> response = lambda.handleRequest(event, null);
        assertEquals(200, response.get("statusCode"));
        assertTrue(response.get("body").toString().contains("\"status\":\"STALE\""));
        assertTrue(response.get("body").toString().contains("\"age\":405"));
    }

    @Test
    void invalidNegativeTime() {
        DataFreshnessLambda lambda = new DataFreshnessLambda();
        Map<String, Object> event = new HashMap<>();
        event.put("body", "{\"current\":100,\"sample\":-95,\"threshold\":10}");
        Map<String, Object> response = lambda.handleRequest(event, null);
        assertEquals(400, response.get("statusCode"));
    }

    @Test
    void sampleAfterCurrent() {
        DataFreshnessLambda lambda = new DataFreshnessLambda();
        Map<String, Object> event = new HashMap<>();
        event.put("body", "{\"current\":50,\"sample\":95,\"threshold\":10}");
        Map<String, Object> response = lambda.handleRequest(event, null);
        assertEquals(400, response.get("statusCode"));
    }

    @Test
    void freshStatusUsingGet() {
        DataFreshnessLambda lambda = new DataFreshnessLambda();
        Map<String, Object> queryParameters = new HashMap<>();
        queryParameters.put("current", "105");
        queryParameters.put("sample", "95");
        queryParameters.put("threshold", "10");
        Map<String, Object> event = new HashMap<>();
        event.put("queryStringParameters", queryParameters);
        Map<String, Object> response = lambda.handleRequest(event, null);
        assertEquals(200, response.get("statusCode"));
        assertTrue(response.get("body").toString().contains("\"status\":\"FRESH\""));
        assertTrue(response.get("body").toString().contains("\"age\":10"));
    }
}
