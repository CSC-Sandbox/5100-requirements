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
  void rejectsInvalidMessagesToAllowRetry() {
    assertThrows(IllegalArgumentException.class, () ->
        consumer.handleRequest(Map.of("Records", List.of(Map.of("body", "{\"id\":\"bad\"}"))), null));
  }
}
