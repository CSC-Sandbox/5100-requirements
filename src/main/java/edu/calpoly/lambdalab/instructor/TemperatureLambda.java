package edu.calpoly.lambdalab.instructor;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Temperature conversion API. Legacy GET conversions remain read-only.
 * A04 POST persists when both storage environment variables are configured.
 */
public class TemperatureLambda implements RequestHandler<Map<String, Object>, Map<String, Object>> {
  interface Storage {
    void save(String id, String json);
    String get(String id);
  }

  private final ObjectMapper mapper = new ObjectMapper();
  private final Storage storage;

  public TemperatureLambda() {
    this(System.getenv("A04_DYNAMODB_TABLE"), System.getenv("A04_S3_BUCKET"));
  }

  private TemperatureLambda(String table, String bucket) {
    this(table != null && !table.isBlank() && bucket != null && !bucket.isBlank()
        ? new TemperatureStorage(table, bucket) : null);
  }

  TemperatureLambda(Storage storage) {
    this.storage = storage;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Map<String, Object> handleRequest(Map<String, Object> event, Context context) {
    try {
      if (event == null) {
        return httpResponse(400, "{\"error\":\"Invalid request\"}");
      }
      Map<String, Object> requestContext = event.get("requestContext") instanceof Map
          ? (Map<String, Object>) event.get("requestContext") : Map.of();
      Map<String, Object> http = requestContext.get("http") instanceof Map
          ? (Map<String, Object>) requestContext.get("http") : Map.of();
      String method = String.valueOf(event.getOrDefault("httpMethod",
          http.getOrDefault("method",
              event.get("queryStringParameters") != null ? "GET" : "POST"))).toUpperCase();

      Map<String, Object> query = event.get("queryStringParameters") instanceof Map
          ? (Map<String, Object>) event.get("queryStringParameters") : Map.of();
      if ("GET".equals(method) && query.containsKey("id")) {
        String id = String.valueOf(query.get("id"));
        if (!isUuid(id)) return httpResponse(400, "{\"error\":\"Invalid id\"}");
        if (storage == null) return httpResponse(503, "{\"error\":\"Storage not configured\"}");
        String json = storage.get(id);
        return json == null ? httpResponse(404, "{\"error\":\"Not found\"}")
            : httpResponse(200, json);
      }

      boolean legacyGet = "GET".equals(method) || (event.get("httpMethod") == null
          && !query.isEmpty());
      Map<String, Object> input = legacyGet ? query
          : mapper.readValue(String.valueOf(event.get("body")), Map.class);

      double value = Double.parseDouble(String.valueOf(input.get("value")));
      if (!Double.isFinite(value)) throw new IllegalArgumentException("Non-finite temperature");
      String from = String.valueOf(input.get("from")).toUpperCase();
      String to = String.valueOf(input.get("to")).toUpperCase();
      double result = convertTemperature(value, from, to);

      Map<String, Object> output = new LinkedHashMap<>();
      output.put("value", value);
      output.put("from", from);
      output.put("to", to);
      output.put("result", result);
      if (!legacyGet) {
        String id = input.containsKey("id") ? String.valueOf(input.get("id"))
            : UUID.randomUUID().toString();
        if (!isUuid(id)) return httpResponse(400, "{\"error\":\"Invalid id\"}");
        output.put("id", id);
        if (storage != null) {
          storage.save(id, mapper.writeValueAsString(output));
          output.put("storage", Map.of("dynamodb", "stored", "s3", "stored"));
        } else {
          output.put("storage", Map.of("dynamodb", "not_configured", "s3", "not_configured"));
        }
      }
      return httpResponse(200, mapper.writeValueAsString(output));
    } catch (IllegalArgumentException e) {
      return httpResponse(400, "{\"error\":\"Invalid request\"}");
    } catch (Exception e) {
      if (context != null) context.getLogger().log("TemperatureLambda failure: " + e.getClass().getSimpleName());
      return httpResponse(500, "{\"error\":\"Operation failed\"}");
    }
  }

  private boolean isUuid(String id) {
    try { return UUID.fromString(id).toString().equals(id); }
    catch (Exception e) { return false; }
  }

  private double convertTemperature(double value, String from, String to) {
    if (from.equals("F") && to.equals("C")) return (value - 32) * 5.0 / 9.0;
    if (from.equals("C") && to.equals("F")) return value * 9.0 / 5.0 + 32;
    throw new IllegalArgumentException("Supported conversions are F to C and C to F.");
  }

  private Map<String, Object> httpResponse(int status, String body) {
    return Map.of("statusCode", status,
        "headers", Map.of("Content-Type", "application/json"), "body", body);
  }
}
