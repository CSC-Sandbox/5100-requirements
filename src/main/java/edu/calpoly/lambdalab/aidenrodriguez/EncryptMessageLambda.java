package edu.calpoly.lambdalab.aidenrodriguez;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * AWS Lambda function that encrypts a message using a Caesar shift cipher.
 *
 * @author Aiden Rodriguez
 * @version 1.0
 */
public class EncryptMessageLambda implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> event, Context context) {
        try {
            Map<String, Object> input = (Map<String, Object>) event.get("queryStringParameters");
            if (input == null) {
                throw new IllegalArgumentException("message and shift are required");
            }

            String message = requireText(input.get("message"), "message");
            int shift = Integer.parseInt(requireText(input.get("shift"), "shift"));
            String encrypted = caesarShift(message, shift);

            Map<String, Object> output = new HashMap<>();
            output.put("original", message);
            output.put("encrypted", encrypted);
            output.put("shift", shift);
            return httpResponse(200, mapper.writeValueAsString(output));
        } catch (Exception e) {
            return httpResponse(400, "{\"error\":\"Invalid request\"}");
        }
    }

    /**
     * Shifts each letter of forward in the alphabet by shift number of
     * positions, wrapping from Z back to A (and z back to a).
     *
     * @param message text to encrypt; must not be null
     * @param shift number of alphabet positions to shift by
     * @return the shifted message
     */
    static String caesarShift(String message, int shift) {
        if (message == null) {
            throw new IllegalArgumentException("message is required");
        }

        int normalizedShift = ((shift % 26) + 26) % 26;
        StringBuilder result = new StringBuilder(message.length());

        for (char c : message.toCharArray()) {
            if (Character.isUpperCase(c)) {
                result.append((char) ('A' + (c - 'A' + normalizedShift) % 26));
            } else if (Character.isLowerCase(c)) {
                result.append((char) ('a' + (c - 'a' + normalizedShift) % 26));
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    private static String requireText(Object value, String name) {
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException(name + " is required");
        }
        return value.toString();
    }

    private Map<String, Object> httpResponse(int statusCode, String body) {
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", statusCode);
        response.put("headers", Map.of("Content-Type", "application/json"));
        response.put("body", body);
        return response;
    }
}