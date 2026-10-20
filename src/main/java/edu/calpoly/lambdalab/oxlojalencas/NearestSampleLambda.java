package edu.calpoly.lambdalab.oxlojalencas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Returns the sensor sample closest to an event timestamp.
 *
 * @author Jess A
 * @version October 4, 2026
 */
public class NearestSampleLambda
        implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Reads an API Gateway request + returns a JSON response.
     *
     * @param event API Gateway request data
     * @param context Lambda runtime context
     * @return HTTP response
     */
    @Override
    public Map<String, Object> handleRequest(
            Map<String, Object> event,
            Context context) {
        try {
            String body = event.get("body").toString();
            RequestData request = readRequest(body);

            NearestResult result = findNearestSample(
                    request.eventTime(),
                    request.samples());

            Map<String, Object> output = new HashMap<>();
            output.put("nearestSample", result.sample());
            output.put("timeDifference", result.timeDifference());

            return response(200, mapper.writeValueAsString(output));
        } catch (Exception exception) {
            return response(400, "{\"error\":\"Invalid request\"}");
        }
    }

    /**
     * Finds the closest sample + its absolute time difference.
     *
     * @param eventTime timestamp to compare
     * @param samples sensor samples
     * @return closest sample + time difference
     */
    public NearestResult findNearestSample(
            double eventTime,
            List<Sample> samples) {
        if (!Double.isFinite(eventTime)
                || samples == null
                || samples.isEmpty()) {
            throw new IllegalArgumentException("Invalid samples");
        }

        Sample closest = null;
        double closestDifference = Double.POSITIVE_INFINITY;

        for (Sample sample : samples) {
            if (sample == null
                    || !Double.isFinite(sample.timestamp())
                    || !Double.isFinite(sample.value())) {
                throw new IllegalArgumentException("Invalid sample");
            }

            double difference = Math.abs(sample.timestamp() - eventTime);
            if (difference < closestDifference) {
                closest = sample;
                closestDifference = difference;
            }
        }

        return new NearestResult(closest, closestDifference);
    }

    private RequestData readRequest(String body) throws Exception {
        Map<?, ?> data = mapper.readValue(body, Map.class);

        double eventTime = readNumber(data.get("eventTimestamp"));
        Object rawSamples = data.get("samples");

        if (!(rawSamples instanceof List<?> sampleList)
                || sampleList.isEmpty()) {
            throw new IllegalArgumentException("Samples are required");
        }

        List<Sample> samples = new ArrayList<>();

        for (Object rawSample : sampleList) {
            if (!(rawSample instanceof Map<?, ?> sampleData)) {
                throw new IllegalArgumentException("Invalid sample");
            }

            double timestamp = readNumber(sampleData.get("timestamp"));
            double value = readNumber(sampleData.get("value"));
            samples.add(new Sample(timestamp, value));
        }

        return new RequestData(eventTime, samples);
    }

    private double readNumber(Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Number is required");
        }

        double number = Double.parseDouble(value.toString());

        if (!Double.isFinite(number)) {
            throw new IllegalArgumentException("Number must be finite");
        }

        return number;
    }

    private Map<String, Object> response(int statusCode, String body) {
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", statusCode);
        response.put("headers", Map.of("Content-Type", "application/json"));
        response.put("body", body);
        return response;
    }

    /** One timestamped sensor sample. */
    public record Sample(double timestamp, double value) {
    }

    /** Closest sample + its time difference. */
    public record NearestResult(Sample sample, double timeDifference) {
    }

    private record RequestData(double eventTime, List<Sample> samples) {
    }
}