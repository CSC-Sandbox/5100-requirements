package edu.calpoly.lambdalab.instructor;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class EventProcessorLambdaTest {
  private final EventProcessorLambda consumer = new EventProcessorLambda();

  @Test
  void acceptsValidSqsEvent() {
    assertDoesNotThrow(() -> consumer.handleRequest(Map.of("Records", List.of(
        Map.of("body", "{\"id\":\"14caa8ee-ca63-41f4-b57f-074007b2affc\",\"result\":0.0}"))), null));
  }

  @Test
  void forwardsUnmodifiedJsonToMqtt() {
    java.util.List<String> published = new java.util.ArrayList<>();
    String body = "{\"id\":\"14caa8ee-ca63-41f4-b57f-074007b2affc\",\"result\":0.0}";
    new EventProcessorLambda(published::add).handleRequest(
        Map.of("Records", List.of(Map.of("body", body))), null);
    assertEquals(List.of(body), published);
  }

  @Test
  void mirrorsSameJsonToBothBrokers() {
    java.util.List<String> iot = new java.util.ArrayList<>();
    java.util.List<String> hive = new java.util.ArrayList<>();
    String body = "{\"id\":\"14caa8ee-ca63-41f4-b57f-074007b2affc\",\"result\":0.0}";
    new EventProcessorLambda(iot::add, hive::add).handleRequest(
        Map.of("Records", List.of(Map.of("body", body))), null);
    assertEquals(List.of(body), iot);
    assertEquals(List.of(body), hive);
  }

  @Test
  void oneBrokerFailureDoesNotPreventOtherAttempt() {
    java.util.List<String> hive = new java.util.ArrayList<>();
    String body = "{\"id\":\"14caa8ee-ca63-41f4-b57f-074007b2affc\",\"result\":0.0}";
    EventProcessorLambda processor = new EventProcessorLambda(
        json -> { throw new IllegalStateException("IoT unavailable"); }, hive::add);
    assertThrows(IllegalArgumentException.class, () ->
        processor.handleRequest(Map.of("Records", List.of(Map.of("body", body))), null));
    assertEquals(List.of(body), hive);
  }

  @Test
  void rejectsInvalidMessagesToAllowRetry() {
    assertThrows(IllegalArgumentException.class, () ->
        consumer.handleRequest(Map.of("Records", List.of(Map.of("body", "{\"id\":\"bad\"}"))), null));
  }
}
