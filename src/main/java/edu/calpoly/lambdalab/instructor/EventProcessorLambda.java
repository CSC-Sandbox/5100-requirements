package edu.calpoly.lambdalab.instructor;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** A04 SQS consumer: validates, logs and forwards JSON to AWS IoT Core. */
public class EventProcessorLambda implements RequestHandler<Map<String, Object>, Void> {
  private final ObjectMapper mapper = new ObjectMapper();
  private final java.util.function.Consumer<String> publisher;

  public EventProcessorLambda() {
    this(System.getenv("A04_IOT_ENDPOINT") == null
        ? null : new AwsIotEventPublisher()::publish);
  }

  EventProcessorLambda(java.util.function.Consumer<String> publisher) {
    this.publisher = publisher;
  }

  @Override
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
        if (publisher != null) {
          publisher.accept(body);
          String sent = "A04 IoT published id=" + id + " topic=" + AwsIotEventPublisher.TOPIC;
          if (context != null) context.getLogger().log(sent);
          else System.out.println(sent);
        }
      } catch (Exception e) {
        throw new IllegalArgumentException("SQS event processing failed; retry expected", e);
      }
    }
    return null;
  }
}
