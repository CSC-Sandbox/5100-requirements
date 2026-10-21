package edu.calpoly.lambdalab.instructor;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class EventProcessorLambdaTest {
  private static final String ID = "14caa8ee-ca63-41f4-b57f-074007b2affc";
  private static final String TEMP = "{\"id\":\"" + ID + "\",\"from\":\"C\",\"to\":\"F\",\"result\":32.0}";
  private static final String GAZE = "{\"id\":\"" + ID + "\",\"operation\":\"GazeClusterLambda\",\"result\":2}";

  private static Map<String, Object> event(String body) {
    return Map.of("Records", List.of(Map.of("body", body)));
  }

  @Test
  void routesInstructorTemperatureWithoutOperationField() {
    List<String> calls = new ArrayList<>();
    new EventProcessorLambda((topic, json) -> calls.add(topic + "|" + json), null)
        .handleRequest(event(TEMP), null);
    assertEquals(List.of("csc5100/events/temperature|" + TEMP), calls);
  }

  @Test
  void routesStudentOperationToBothBrokersWithoutChangingJson() {
    List<String> iot = new ArrayList<>();
    List<String> hive = new ArrayList<>();
    new EventProcessorLambda((topic, json) -> iot.add(topic + "|" + json),
        (topic, json) -> hive.add(topic + "|" + json))
        .handleRequest(event(GAZE), null);
    assertEquals(List.of("csc5100/events/gaze-clusters|" + GAZE), iot);
    assertEquals(iot, hive);
  }

  @Test
  void supportsLambdaAliasField() {
    String body = "{\"id\":\"" + ID + "\",\"lambda\":\"RobotDistanceLambda\",\"result\":1}";
    List<String> topics = new ArrayList<>();
    new EventProcessorLambda((topic, json) -> topics.add(topic), null)
        .handleRequest(event(body), null);
    assertEquals(List.of("csc5100/events/robot-distance"), topics);
  }

  @Test
  void attemptsSecondBrokerEvenIfFirstFails() {
    List<String> hive = new ArrayList<>();
    EventProcessorLambda processor = new EventProcessorLambda(
        (topic, json) -> { throw new IllegalStateException("IoT unavailable"); },
        (topic, json) -> hive.add(json));
    assertThrows(IllegalArgumentException.class, () -> processor.handleRequest(event(GAZE), null));
    assertEquals(List.of(GAZE), hive);
  }

  @Test
  void rejectsUnknownOperationRatherThanPublishingArbitraryTopic() {
    String body = "{\"id\":\"" + ID + "\",\"operation\":\"other/+/topic\",\"result\":1}";
    assertThrows(IllegalArgumentException.class,
        () -> new EventProcessorLambda((topic, json) -> fail("Must not publish"), null)
            .handleRequest(event(body), null));
  }

  @Test
  void rejectsInvalidUuid() {
    assertThrows(IllegalArgumentException.class,
        () -> new EventProcessorLambda((topic, json) -> fail("Must not publish"), null)
            .handleRequest(event("{\"id\":\"bad\",\"operation\":\"GazeClusterLambda\",\"result\":1}"), null));
  }
}
