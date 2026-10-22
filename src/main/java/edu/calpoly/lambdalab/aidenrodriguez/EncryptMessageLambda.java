package edu.calpoly.lambdalab.aidenrodriguez;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * AWS Lambda function that encrypts a message using a Caesar shift cipher.
 *
 * @author Aiden Rodriguez
 * @version 1.0
 */
public class EncryptMessageLambda implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private static final String DEFAULT_DYNAMODB_TABLE = "csc5100-results";
    private static final String DEFAULT_S3_BUCKET = "csc5100-results";
    private static final String OPERATION = "EncryptMessageLambda";
    private final ObjectMapper mapper = new ObjectMapper();
    private final Storage storage;
    private final Messaging messaging;

    /** Storage seam; production uses DynamoDB and tests may supply a fake. */
    interface Storage {
        void save(String id, String json);
        String get(String id);
    }

    /** SNS is deliberately a separate branch from the later SQS integration. */
    interface Messaging {
        void publishSns(String json);
    }

    /**
     * Uses the configured A04 DynamoDB and S3 destinations in Lambda. Local
     * runs without that configuration retain in-memory lookup support.
     */
    public EncryptMessageLambda() {
        this(defaultStorage(), defaultMessaging());
    }

    EncryptMessageLambda(Storage storage) {
        this(storage, null);
    }

    EncryptMessageLambda(Storage storage, Messaging messaging) {
        this.storage = storage;
        this.messaging = messaging;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> handleRequest(Map<String, Object> event, Context context) {
        try {
            if (event == null) {
                return invalidRequest();
            }

            Map<String, Object> query = event.get("queryStringParameters") instanceof Map
                    ? (Map<String, Object>) event.get("queryStringParameters") : Map.of();
            String method = requestMethod(event, query);
            if ("GET".equals(method) && query.containsKey("id")) {
                return lookup(String.valueOf(query.get("id")));
            }

            boolean legacyGet = "GET".equals(method);
            Map<String, Object> input = legacyGet ? query : requestBody(event);

            String message = requireText(input.get("message"), "message");
            int shift = Integer.parseInt(requireText(input.get("shift"), "shift"));
            String encrypted = caesarShift(message, shift);
            String id = UUID.randomUUID().toString();

            Map<String, Object> output = new LinkedHashMap<>();
            output.put("original", message);
            output.put("encrypted", encrypted);
            output.put("shift", shift);
            output.put("id", id);
            output.put("messaging", Map.of("sns", messaging == null ? "not_configured" : "published"));
            String json = mapper.writeValueAsString(output);
            try {
                storage.save(id, json);
            } catch (RuntimeException e) {
                log(context, "Unable to persist encryption result", e);
                return httpResponse(500, "{\"error\":\"Unable to persist result\"}");
            }
            if (messaging != null) {
                String eventJson = mapper.writeValueAsString(Map.of(
                        "id", id,
                        "operation", OPERATION,
                        "result", output));
                try {
                    messaging.publishSns(eventJson);
                    output.put("messaging", Map.of("sns", "published"));
                } catch (RuntimeException e) {
                    log(context, "Unable to publish SNS event", e);
                    return httpResponse(502, "{\"error\":\"Unable to publish SNS event\"}");
                }
            }
            return httpResponse(200, json);
        } catch (IllegalArgumentException | JsonProcessingException e) {
            return invalidRequest();
        } catch (Exception e) {
            log(context, "Unable to process encryption request", e);
            return httpResponse(500, "{\"error\":\"Operation failed\"}");
        }
    }

    private Map<String, Object> lookup(String id) {
        if (!isUuid(id)) {
            return httpResponse(400, "{\"error\":\"Invalid id\"}");
        }
        try {
            String json = storage.get(id);
            return json == null ? httpResponse(404, "{\"error\":\"Not found\"}")
                    : httpResponse(200, json);
        } catch (RuntimeException e) {
            return httpResponse(500, "{\"error\":\"Unable to retrieve result\"}");
        }
    }

    @SuppressWarnings("unchecked")
    private String requestMethod(Map<String, Object> event, Map<String, Object> query) {
        Object httpMethod = event.get("httpMethod");
        if (httpMethod != null) {
            return String.valueOf(httpMethod).toUpperCase();
        }
        Map<String, Object> requestContext = event.get("requestContext") instanceof Map
                ? (Map<String, Object>) event.get("requestContext") : Map.of();
        Map<String, Object> http = requestContext.get("http") instanceof Map
                ? (Map<String, Object>) requestContext.get("http") : Map.of();
        if (http.get("method") != null) {
            return String.valueOf(http.get("method")).toUpperCase();
        }
        return query.isEmpty() ? "POST" : "GET";
    }

    private Map<String, Object> requestBody(Map<String, Object> event) throws Exception {
        Object body = event.get("body");
        if (body == null) {
            throw new IllegalArgumentException("body is required");
        }
        return mapper.readValue(String.valueOf(body), Map.class);
    }

    private static boolean isUuid(String id) {
        try {
            return UUID.fromString(id).toString().equals(id);
        } catch (Exception e) {
            return false;
        }
    }

    private Map<String, Object> invalidRequest() {
        return httpResponse(400, "{\"error\":\"Invalid request\"}");
    }

    private static Storage defaultStorage() {
        String table = System.getenv("A04_DYNAMODB_TABLE");
        String bucket = System.getenv("A04_S3_BUCKET");
        if ((table != null && !table.isBlank()) || (bucket != null && !bucket.isBlank())) {
            return new EncryptMessageStorage(nonBlankOrDefault(table, DEFAULT_DYNAMODB_TABLE),
                    nonBlankOrDefault(bucket, DEFAULT_S3_BUCKET));
        }
        // AWS_LAMBDA_FUNCTION_NAME is supplied by Lambda, but not local JUnit.
        return System.getenv("AWS_LAMBDA_FUNCTION_NAME") != null
                ? new EncryptMessageStorage(DEFAULT_DYNAMODB_TABLE, DEFAULT_S3_BUCKET)
                : new InMemoryStorage();
    }

    private static String nonBlankOrDefault(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private static Messaging defaultMessaging() {
        String topicArn = System.getenv("A04_SNS_TOPIC_ARN");
        return topicArn == null || topicArn.isBlank() ? null : new EncryptMessageMessaging(topicArn);
    }

    private static void log(Context context, String message, Exception error) {
        if (context != null) {
            context.getLogger().log(message + ": " + error.getClass().getSimpleName());
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
        return Map.of("statusCode", statusCode,
                "headers", Map.of("Content-Type", "application/json"), "body", body);
    }

    private static final class InMemoryStorage implements Storage {
        private final Map<String, String> records = new ConcurrentHashMap<>();

        @Override
        public void save(String id, String json) {
            records.put(id, json);
        }

        @Override
        public String get(String id) {
            return records.get(id);
        }
    }
}
