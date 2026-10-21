package edu.calpoly.lambdalab.instructor;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;

/** Explicit, allowlisted routing; never accept arbitrary MQTT topics from SQS. */
final class A04MqttTopics {
  private static final String PREFIX = "csc5100/events/";
  private static final Map<String, String> ROUTES = Map.ofEntries(
      Map.entry("TemperatureLambda", "temperature"),
      Map.entry("GazeClusterLambda", "gaze-clusters"),
      Map.entry("EncryptMessageLambda", "encrypt"),
      Map.entry("DominantAffectLambda", "dominant-affect"),
      Map.entry("NearestSampleLambda", "nearest-sample"),
      Map.entry("RobotDistanceLambda", "robot-distance"),
      Map.entry("RobotReachabilityLambda", "robot-reachability"),
      Map.entry("LidarClosestLambda", "lidar-closest"),
      Map.entry("LidarSafetyLambda", "lidar-safety"),
      Map.entry("DataFreshnessLambda", "data-freshness"),
      Map.entry("MovingAverageLambda", "moving-average"),
      Map.entry("CoordinateNormalizerLambda", "coordinate-normalizer"),
      Map.entry("EventRateLambda", "event-rate"));

  private A04MqttTopics() { }

  static String forEvent(JsonNode event) {
    String operation = event.path("operation").asText("");
    if (operation.isBlank()) operation = event.path("lambda").asText("");
    // Existing instructor TemperatureLambda events omit operation.
    if (operation.isBlank() && event.hasNonNull("from") && event.hasNonNull("to")) {
      operation = "TemperatureLambda";
    }
    String suffix = ROUTES.get(operation);
    if (suffix == null) throw new IllegalArgumentException(
        "Unknown A04 operation: " + operation + "; expected a registered Lambda name");
    return PREFIX + suffix;
  }
}
