package edu.calpoly.lambdalab.paulmotter;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * AWS Lambda function that calculates the distance between two 3 dimensional points.
 * 
 * @author Paul Motter (PaulMotter)
 * @version 1.0 
 */
public class RobotDistanceLambda implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> event, Context context) {
        
        //GET parameters
        Map<String, Object> input = (Map<String, Object>) event.get("queryStringParameters");
        float[] pos1 = new float[3];
        float[] pos2 = new float[3];

        try{
            // Parsing can throw errors.
            if (input == null) throw new IllegalArgumentException();
            pos1[0] = Float.parseFloat((String) input.get("x1"));
            pos1[1] = Float.parseFloat((String) input.get("y1"));
            pos1[2] = Float.parseFloat((String) input.get("z1"));
    
            pos2[0] = Float.parseFloat((String) input.get("x2"));
            pos2[1] = Float.parseFloat((String) input.get("y2"));
            pos2[2] = Float.parseFloat((String) input.get("z2"));

            // calculateDistance checks to NaN and Infinity.
            float distance = calculateDistance(pos1, pos2);

            Map<String, Object> output = new HashMap<>();
            output.put("distance", distance);
                // mapper can throw error. 
            return httpResponse(200, mapper.writeValueAsString(output));
        } catch (Exception e){
            return httpResponse(400, "{\"error\":\"Invalid request\"}");
        }
    }

    private Map<String, Object> httpResponse(int statusCode, String body) {
        Map<String, Object> response = new HashMap<>();
            response.put("statusCode", statusCode);
            response.put("headers", Map.of("Content-Type", "application/json"));
            response.put("body", body);
        return response;
    }

    /**
     * Calculates the distance between two points.
     * Makes sure the inputs are 3 dimensional positions.
     * Makes sure the output is a real value.
     */
    private float calculateDistance(float[] pos1, float[] pos2){
        if(pos1.length != 3 || pos2.length != 3){
            throw new IllegalArgumentException("pos1 and pos2 must both have 3 elements.");
        }
        float[] difference = {pos2[0]-pos1[0], pos2[1]-pos1[1], pos2[2]-pos1[2]};
        float distance = (float) Math.sqrt(difference[0]*difference[0] + difference[1]*difference[1] + difference[2]*difference[2]);
        if (!Float.isFinite(distance)){
            throw new IllegalArgumentException("distance is infinite or NaN.");
        }
        return distance;
    }
    
}
