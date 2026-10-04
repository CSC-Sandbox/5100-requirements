package edu.calpoly.lambdalab.oxlojalencas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Tests for NearestSampleLambda.
 *
 * @author Jess A
 * @version October 4, 2026
 */
class NearestSampleLambdaTest {

    @Test
    void returnsExactMatch() {
        NearestSampleLambda lambda = new NearestSampleLambda();

        String body = """
                {
                  "eventTimestamp": 10.0,
                  "samples": [
                    {"timestamp": 9.5, "value": 3.0},
                    {"timestamp": 10.0, "value": 4.2}
                  ]
                }
                """;

        Map<String, Object> response = lambda.handleRequest(event(body), null);

        assertEquals(200, response.get("statusCode"));
        assertTrue(response.get("body").toString()
                .contains("\"timestamp\":10.0"));
        assertTrue(response.get("body").toString()
                .contains("\"timeDifference\":0.0"));
    }

    @Test
    void returnsNearestNeighbor() {
        NearestSampleLambda lambda = new NearestSampleLambda();

        NearestSampleLambda.NearestResult result =
                lambda.findNearestSample(
                        10.25,
                        List.of(
                                new NearestSampleLambda.Sample(10.0, 4.2),
                                new NearestSampleLambda.Sample(10.4, 5.1)));

        assertEquals(10.4, result.sample().timestamp(), 0.0001);
        assertEquals(5.1, result.sample().value(), 0.0001);
        assertEquals(0.15, result.timeDifference(), 0.0001);
    }

    @Test
    void rejectsEmptySamples() {
        NearestSampleLambda lambda = new NearestSampleLambda();

        String body = """
                {
                  "eventTimestamp": 10.0,
                  "samples": []
                }
                """;

        Map<String, Object> response = lambda.handleRequest(event(body), null);

        assertEquals(400, response.get("statusCode"));
    }

    @Test
    void rejectsInvalidSample() {
        NearestSampleLambda lambda = new NearestSampleLambda();

        String body = """
                {
                  "eventTimestamp": 10.0,
                  "samples": [
                    {"timestamp": "not a number", "value": 4.2}
                  ]
                }
                """;

        Map<String, Object> response = lambda.handleRequest(event(body), null);

        assertEquals(400, response.get("statusCode"));
    }

    private Map<String, Object> event(String body) {
        Map<String, Object> event = new HashMap<>();
        event.put("body", body);
        return event;
    }
}