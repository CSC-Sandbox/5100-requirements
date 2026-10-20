package edu.calpoly.lambdalab.dylangururajan;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

// Unit tests
class CoordinateNormalizerLambdaTest {

  private final CoordinateNormalizerLambda lambda = new CoordinateNormalizerLambda();
  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void normalizesCenterPoint() throws JsonProcessingException {
    Map<String, Object> response = request("960", "540", "1920", "1080");

    assertEquals(200, response.get("statusCode"));
    assertEquals("application/json", headers(response).get("Content-Type"));
    JsonNode body = mapper.readTree(response.get("body").toString());
    assertEquals(0.5, body.get("normalizedX").asDouble(), 0.000001);
    assertEquals(0.5, body.get("normalizedY").asDouble(), 0.000001);
  }

  @Test
  void normalizesImageBoundary() throws JsonProcessingException {
    Map<String, Object> response = request("1920", "1080", "1920", "1080");

    assertEquals(200, response.get("statusCode"));
    JsonNode body = mapper.readTree(response.get("body").toString());
    assertEquals(1.0, body.get("normalizedX").asDouble(), 0.000001);
    assertEquals(1.0, body.get("normalizedY").asDouble(), 0.000001);
  }

  @Test
  void rejectsNonPositiveDimensions() {
    Map<String, Object> zeroWidth = request("0", "0", "0", "1080");
    Map<String, Object> negativeHeight = request("0", "0", "1920", "-1");

    assertEquals(400, zeroWidth.get("statusCode"));
    assertEquals(400, negativeHeight.get("statusCode"));
  }

  @Test
  void rejectsMissingAndInvalidInput() {
    Map<String, Object> missingCoordinate = request(null, "540", "1920", "1080");
    Map<String, Object> nonNumericCoordinate = request("left", "540", "1920", "1080");
    Map<String, Object> outsideImage = request("1921", "540", "1920", "1080");

    assertEquals(400, missingCoordinate.get("statusCode"));
    assertEquals(400, nonNumericCoordinate.get("statusCode"));
    assertEquals(400, outsideImage.get("statusCode"));
  }

  private Map<String, Object> request(String x, String y, String width, String height) {
    Map<String, Object> queryParameters = new HashMap<>();
    if (x != null) {
      queryParameters.put("x", x);
    }
    queryParameters.put("y", y);
    queryParameters.put("width", width);
    queryParameters.put("height", height);

    Map<String, Object> event = new HashMap<>();
    event.put("queryStringParameters", queryParameters);
    return lambda.handleRequest(event, null);
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> headers(Map<String, Object> response) {
    return (Map<String, Object>) response.get("headers");
  }
}
