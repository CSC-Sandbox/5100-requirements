package edu.calpoly.lambdalab.ejiswkrosmew;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LidarSafetyLambdaTest {
    @Test
    void warningDistance() {
        LidarSafetyLambda lambda = new LidarSafetyLambda();
        Map<String, Object> input = Map.of(
                "body", "{\"distances\": [1.7, 2.5, 0.2, 0.7], \"threshold\": 0.9}"
        );
        Map<String, Object> res = lambda.handleRequest(input, null);
        assertEquals(200, res.get("statusCode"));
        assertTrue(res.get("body").toString().contains("\"status\":\"WARNING\""));
        assertTrue(res.get("body").toString().contains("\"minDist\":0.2"));
    }

    @Test
    void safeDistance() {
        LidarSafetyLambda lambda = new LidarSafetyLambda();
        Map<String, Object> input = Map.of(
                "body", "{\"distances\": [1.5, 1.0, 2.2, 1.3], \"threshold\": 0.4}"
        );
        Map<String, Object> res = lambda.handleRequest(input, null);
        assertEquals(200, res.get("statusCode"));
        assertTrue(res.get("body").toString().contains("\"status\":\"SAFE\""));
        assertTrue(res.get("body").toString().contains("\"minDist\":1.0"));
    }

    @Test
    void emptyBody() {
        LidarSafetyLambda lambda = new LidarSafetyLambda();
        Map<String, Object> input = Map.of(
                "body", ""
        );
        Map<String, Object> res = lambda.handleRequest(input, null);
        assertEquals(400, res.get("statusCode"));
    }

    @Test
    void notJSON() {
        LidarSafetyLambda lambda = new LidarSafetyLambda();
        Map<String, Object> input = Map.of(
                "body", "foobar"
        );
        Map<String, Object> res = lambda.handleRequest(input, null);
        assertEquals(400, res.get("statusCode"));
    }

    @Test
    void missingDists() {
        LidarSafetyLambda lambda = new LidarSafetyLambda();
        Map<String, Object> input = Map.of(
                "body", "{\"threshold\": 0.4}"
        );
        Map<String, Object> res = lambda.handleRequest(input, null);
        assertEquals(400, res.get("statusCode"));
    }

    @Test
    void missingThreshold() {
        LidarSafetyLambda lambda = new LidarSafetyLambda();
        Map<String, Object> input = Map.of(
                "body", "{\"distances\": [1.5, 1.0, 2.2, 1.3]}"
        );
        Map<String, Object> res = lambda.handleRequest(input, null);
        assertEquals(400, res.get("statusCode"));
    }

    @Test
    void distsNotArray() {
        LidarSafetyLambda lambda = new LidarSafetyLambda();
        Map<String, Object> input = Map.of(
                "body", "{\"distances\": 1.5, \"threshold\": 0.4}"
        );
        Map<String, Object> res = lambda.handleRequest(input, null);
        assertEquals(400, res.get("statusCode"));
    }

    @Test
    void dists2DArray() {
        LidarSafetyLambda lambda = new LidarSafetyLambda();
        Map<String, Object> input = Map.of(
                "body", "{\"distances\": [[1.5, 1.0, 2.2, 1.3]], \"threshold\": 0.4}"
        );
        Map<String, Object> res = lambda.handleRequest(input, null);
        assertEquals(400, res.get("statusCode"));
    }

    @Test
    void distsNotAllDoubles() {
        LidarSafetyLambda lambda = new LidarSafetyLambda();
        Map<String, Object> input = Map.of(
                "body", "{\"distances\": [1.5, \"foo\", 2.2, 1.3], \"threshold\": 0.4}"
        );
        Map<String, Object> res = lambda.handleRequest(input, null);
        assertEquals(400, res.get("statusCode"));
    }

    @Test
    void thresholdNotDouble() {
        LidarSafetyLambda lambda = new LidarSafetyLambda();
        Map<String, Object> input = Map.of(
                "body", "{\"distances\": [1.5, 1.0, 2.2, 1.3], \"threshold\": \"bar\"}"
        );
        Map<String, Object> res = lambda.handleRequest(input, null);
        assertEquals(400, res.get("statusCode"));
    }
}
