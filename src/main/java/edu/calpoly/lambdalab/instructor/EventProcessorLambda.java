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
  private final java.util.function.Consumer<String> hiveMqPublisher;

  public EventProcessorLambda() {
    this(System.getenv("A04_IOT_ENDPOINT") == null
        ? null : new AwsIotEventPublisher()::publish,
        "true".equalsIgnoreCase(System.getenv("A04_HIVEMQ_ENABLED"))
            ? new HiveMqEventPublisher()::publish : null);
  }

  EventProcessorLambda(java.util.function.Consumer<String> publisher) {
    this(publisher, null);
  }

  EventProcessorLambda(java.util.function.Consumer<String> publisher,
      java.util.function.Consumer<String> hiveMqPublisher) {
    this.publisher = publisher;
    this.hiveMqPublisher = hiveMqPublisher;
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
        RuntimeException publishError = null;
        if (publisher != null) {
          try {
            publisher.accept(body);
            log(context, "A04 IoT published id=" + id + " topic=" + AwsIotEventPublisher.TOPIC);
          } catch (RuntimeException e) {
            log(context, "A04 IoT publish FAILED id=" + id + ": " + e);
            publishError = e;
          }
        }
        if (hiveMqPublisher != null) {
          try {
            hiveMqPublisher.accept(body);
            log(context, "A04 HiveMQ published id=" + id + " topic=" + HiveMqEventPublisher.TOPIC);
          } catch (RuntimeException e) {
            log(context, "A04 HiveMQ publish FAILED id=" + id + ": " + e);
            if (publishError == null) publishError = e;
            else publishError.addSuppressed(e);
          }
        }
        if (publishError != null) throw publishError;
      } catch (Exception e) {
        throw new IllegalArgumentException("SQS event processing failed; retry expected", e);
      }
    }
    return null;
  }

  private static void log(Context context, String message) {
    if (context != null) context.getLogger().log(message);
    else System.out.println(message);
  }
}
