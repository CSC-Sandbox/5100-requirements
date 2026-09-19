package edu.calpoly.provided;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.EnumMap;
import java.util.Map;

public class MessageValidator {

    public enum MessageType {
        ROBOT, GAZE, AFFECT, LIDAR
    }

    private static final Map<MessageType, Integer> EXPECTED_COUNT = new EnumMap<>(MessageType.class);
    static {
        EXPECTED_COUNT.put(MessageType.ROBOT, 9);
        EXPECTED_COUNT.put(MessageType.GAZE, 2);
        EXPECTED_COUNT.put(MessageType.AFFECT, 5);
        EXPECTED_COUNT.put(MessageType.LIDAR, 3);
    }

    private static final Map<MessageType, String[]> FIELD_NAMES = new EnumMap<>(MessageType.class);
    static {
        FIELD_NAMES.put(MessageType.ROBOT, new String[] {"J1", "J2", "J3", "J4", "J5", "J6", "X", "Y", "Z"});
        FIELD_NAMES.put(MessageType.GAZE, new String[] {"X", "Y"});
        FIELD_NAMES.put(MessageType.AFFECT, new String[] {"focus", "excitement", "engagement", "interest", "stress"});
        FIELD_NAMES.put(MessageType.LIDAR, new String[] {"X", "Y", "Z"});
    }

    private static final double RANGE_MIN = 0.0;
    private static final double RANGE_MAX = 1.0;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String GENERIC_ERROR = "Invalid message.";

    private String lastError;

    public boolean validate(String message) {
        lastError = null;

        if (message == null || message.trim().isEmpty()) {
            lastError = GENERIC_ERROR;
            return false;
        }

        String trimmed = message.trim();
        if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
            return validateJson(trimmed);
        }
        return validateCsv(trimmed);
    }

    public String getLastError() {
        return lastError;
    }

    private boolean validateCsv(String message) {
        // Use -1 limit so trailing/embedded empty fields (missing values) are preserved.
        String[] tokens = message.split(",", -1);

        if (tokens.length == 0) {
            lastError = GENERIC_ERROR;
            return false;
        }

        String typeToken = tokens[0].trim();
        MessageType type = parseType(typeToken);
        if (type == null) {
            lastError = GENERIC_ERROR;
            return false;
        }

        String[] valueTokens = new String[tokens.length - 1];
        System.arraycopy(tokens, 1, valueTokens, 0, valueTokens.length);

        return parseAndValidateValues(type, valueTokens);
    }

    private boolean validateJson(String message) {
        JsonNode root;
        try {
            root = objectMapper.readTree(message);
        } catch (Exception e) {
            lastError = GENERIC_ERROR;
            return false;
        }

        if (root == null || !root.isObject()) {
            lastError = GENERIC_ERROR;
            return false;
        }

        JsonNode typeNode = root.get("type");
        if (typeNode == null || !typeNode.isTextual()) {
            lastError = GENERIC_ERROR;
            return false;
        }

        MessageType type = parseType(typeNode.asText());
        if (type == null) {
            lastError = GENERIC_ERROR;
            return false;
        }

        JsonNode dataNode = root.get("data");
        if (dataNode == null || !dataNode.isArray()) {
            lastError = GENERIC_ERROR;
            return false;
        }

        String[] valueTokens = new String[dataNode.size()];
        for (int i = 0; i < dataNode.size(); i++) {
            JsonNode element = dataNode.get(i);
            if (element.isNumber()) {
                valueTokens[i] = element.asText();
            } else if (element.isTextual()) {
                // Preserve the raw (possibly non-numeric) text so the shared
                // numeric parser below can report a consistent error message.
                valueTokens[i] = element.asText();
            } else {
                valueTokens[i] = element.toString();
            }
        }

        return parseAndValidateValues(type, valueTokens);
    }

    private boolean parseAndValidateValues(MessageType type, String[] valueTokens) {
        int expected = EXPECTED_COUNT.get(type);

        if (valueTokens.length < expected) {
            lastError = GENERIC_ERROR;
            return false;
        }
        if (valueTokens.length > expected) {
            lastError = GENERIC_ERROR;
            return false;
        }

        double[] values = new double[expected];
        for (int i = 0; i < expected; i++) {
            String token = valueTokens[i] == null ? "" : valueTokens[i].trim();
            if (token.isEmpty()) {
                lastError = GENERIC_ERROR;
                return false;
            }
            try {
                values[i] = Double.parseDouble(token);
            } catch (NumberFormatException e) {
                lastError = GENERIC_ERROR;
                return false;
            }
        }

        if (requiresRangeCheck(type)) {
            for (int i = 0; i < expected; i++) {
                if (values[i] < RANGE_MIN || values[i] > RANGE_MAX) {
                    lastError = GENERIC_ERROR;
                    return false;
                }
            }
        }

        return true;
    }

    private boolean requiresRangeCheck(MessageType type) {
        return type == MessageType.GAZE || type == MessageType.AFFECT;
    }

    private MessageType parseType(String token) {
        if (token == null) {
            return null;
        }
        try {
            return MessageType.valueOf(token.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}