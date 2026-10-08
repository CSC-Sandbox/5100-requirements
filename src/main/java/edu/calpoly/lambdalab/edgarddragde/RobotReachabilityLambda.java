package edu.calpoly.lambdalab.edgarddragde;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;


/**
 * AWS Lambda handler that determines whether a 3D point is within
 * a robot workspace centered at the origin.
 *
 * <p>Accepts query parameters {@code x}, {@code y}, {@code z}, and
 * {@code radius}. Returns JSON containing {@code distance} and
 * {@code reachable}. Points on the boundary are reachable.
 *
 * @author Edgard Aviles
 * @version 1.0.0 October 6, 2026
 */
public class RobotReachabilityLambda implements RequestHandler<Map<String, Object>, Map<String, Object>> {

  private final ObjectMapper mapper = new ObjectMapper();

  /**
  * Parses query parameters, calls the workspace calculations, and
  * creates an HTTP response.
  *
  * @param event API Gateway event containing query string parameters
  * @param context Lambda execution context; unused
  * @return HTTP 200 with distance and reachability, 
  * or HTTP 400 if input is missing, nonnumeric, non-finite, or has a negative radius
  */
  @Override
  public Map<String, Object> handleRequest(Map<String, Object> event, Context context) {
    try {
      Map<String, Object> input;

      if (event.get("queryStringParameters") != null) {
        input = (Map<String, Object>)
            event.get("queryStringParameters");
      } else {
        throw new IllegalArgumentException("not valid input\n");
      }
      double x, y, z, radius;
      x = Double.parseDouble(input.get("x").toString());
      y = Double.parseDouble(input.get("y").toString());
      z = Double.parseDouble(input.get("z").toString());
      radius = Double.parseDouble(input.get("radius").toString());
      if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z) || !Double.isFinite(radius)) {
        throw new IllegalArgumentException("not valid double\n");
      }
      double dist = distance(x, y, z);
      Map<String, Object> output = new HashMap<>();
      output.put("distance", dist);
      output.put("reachable", reachable(dist, radius));
      return httpResponse(200, mapper.writeValueAsString(output));
    } catch (Exception e) {
      return httpResponse(400, "{\"error\":\"Invalid request\"}");
    }
  }

  /**
  * Calculates the Euclidean distance from the origin to a 3D point.
  *
  * @param x the x coordinate
  * @param y the y coordinate
  * @param z the z coordinate
  * @return the distance from the origin
  */
  private double distance(double x, double y, double z) {
    return Math.sqrt(x*x + y*y + z*z);
  }

  /**
  * Determines whether a distance is within the workspace radius,
  * including the boundary.
  *
  * @param dist the distance from the origin
  * @param radius the workspace radius
  * @return true if the distance is less than or equal to the radius
  * @throws IllegalArgumentException if radius is negative or NaN
  */
  private boolean reachable(double dist, double radius) {
    if (radius >= 0) {
      return dist <= radius;
    }
    throw new IllegalArgumentException("invalid input");
  }
  /**
  * Builds an API Gateway response with a JSON content type.
  *
  * @param statusCode the HTTP status code
  * @param body the JSON response body
  * @return a map containing the status code, headers, and body
  */ 
  private Map<String, Object> httpResponse(int statusCode, String body) {
    Map<String, Object> response = new HashMap<>();
    response.put("statusCode", statusCode);
    response.put("headers", Map.of("Content-Type", "application/json"));
    response.put("body", body);
    return response;
  }
}
