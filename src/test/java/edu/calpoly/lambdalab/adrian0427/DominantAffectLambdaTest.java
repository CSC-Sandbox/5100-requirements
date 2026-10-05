package edu.calpoly.lambdalab.adrian0427;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DominantAffectLambdaTest {

    @Test
    void returnsClearMaximum() {
        DominantAffectLambda lambda = new DominantAffectLambda();

        Map<String, Object> event = new HashMap<>();
        event.put(
                "body",
                "{\"stress\":0.2,\"engagement\":0.8,"
                        + "\"focus\":0.6,\"interest\":0.5}");

        Map<String, Object> response =
                lambda.handleRequest(event, null);

        assertEquals(200, response.get("statusCode"));
        assertTrue(
                response.get("body").toString()
                        .contains("\"metric\":\"engagement\""));
        assertTrue(
                response.get("body").toString()
                        .contains("\"value\":0.8"));
    }

    @Test
    void resolvesTieAlphabetically() {
        DominantAffectLambda lambda = new DominantAffectLambda();

        Map<String, Object> event = new HashMap<>();
        event.put(
                "body",
                "{\"stress\":0.8,\"engagement\":0.8,"
                        + "\"focus\":0.6,\"interest\":0.5}");

        Map<String, Object> response =
                lambda.handleRequest(event, null);

        assertEquals(200, response.get("statusCode"));

        // engagement and stress tie at 0.8.
        // Alphabetical tie rule selects engagement.
        assertTrue(
                response.get("body").toString()
                        .contains("\"metric\":\"engagement\""));
        assertTrue(
                response.get("body").toString()
                        .contains("\"value\":0.8"));
    }

    @Test
    void rejectsInvalidInput() {
        DominantAffectLambda lambda = new DominantAffectLambda();

        Map<String, Object> event = new HashMap<>();
        event.put(
                "body",
                "{\"stress\":\"high\",\"engagement\":0.8}");

        Map<String, Object> response =
                lambda.handleRequest(event, null);

        assertEquals(400, response.get("statusCode"));
    }
}