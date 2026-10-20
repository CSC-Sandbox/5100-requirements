package edu.calpoly.lambdalab.instructor;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * A04 SQS consumer. Validates and logs a correlated event.
 * A later iteration forwards validated events to the MQTT broker.
 */
public class EventProcessorLambda implements RequestHandler<Map<String, Object>, Void> {
  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  @SuppressWarnings("unchecked")
  public Void handleRequest(Map<String, Object> event, Context context) {
    if (event == null || !(event.get("Records") instanceof List<?> records)) {
      throw new IllegalArgumentException("Expected SQS Records");
    }
    for (Object record : records) {
      if (!(record instanceof Map<?, ?> entry) || !(entry.get("body") instanceof String body)) {
        throw new IllegalArgumentException("Invalid SQS record");
      }
      try {
        JsonNode json = mapper.readTree(body);
        String id = json.path("id").asText();
        if (!UUID.fromString(id).toString().equals(id)) {
          throw new IllegalArgumentException("Invalid correlation id");
        }
        String result = json.path("result").asText(null);
        if (result == null) throw new IllegalArgumentException("Missing result");
        String log = "A04 SQS received id=" + id + " result=" + result;
        if (context != null) context.getLogger().log(log);
        else System.out.println(log);
      } catch (Exception e) {
        // Fail the batch so Lambda/SQS retries instead of silently losing a message.
        throw new IllegalArgumentException("Invalid SQS event", e);
      }
    }
    return null;
  }
}
