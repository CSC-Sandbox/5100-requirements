package edu.calpoly.eye;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * GazePoint class to store an (x,y) coordinate as a single object
 *
 * @author James Yaguma
 * @version 1.0 (2026-09-25)
 */
public class GazePoint {

    public double x;
    public double y;
    public static final ObjectMapper mapper = new ObjectMapper();

    GazePoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Copy constructor for GazePoint
     *
     * @param gazePoint The GazePoint to copy
     */
    GazePoint(GazePoint gazePoint) {
        x = gazePoint.x;
        y = gazePoint.y;
    }

    /**
     * Setter for both x and y simultaneously
     *
     * @param x New x value
     * @param y New y value
     */
    public void setXY(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // New GazePoint from JSON
    public static GazePoint fromJSON (byte[] jsonBytes) throws IOException {
        GazePoint ret = mapper.readValue(jsonBytes, GazePoint.class);
        ret.validCheck();
        return ret;
    }

    public static GazePoint fromJSON (String jsonString) throws IOException {
        GazePoint ret = mapper.readValue(jsonString, GazePoint.class);
        ret.validCheck();
        return ret;
    }

    // Throw exception if x or y not within valid range
    private void validCheck() {
        if (x < 0 || x > 1) {
            throw new IllegalArgumentException("GazePoint x out of range: " + x + " is not within range of 0.0 and 1.0");
        }
        if (y < 0 || y > 1) {
            throw new IllegalArgumentException("GazePoint y out of range: " + y + " is not within range of 0.0 and 1.0");
        }
    }
}
