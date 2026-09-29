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

    /**
     * Default constructor
     * (x,y) set to (0,0) by default
     */
    GazePoint() {
        x = 0.0;
        y = 0.0;
    }

    /**
     * Constructor allowing preset (x,y) values
     *
     * @param x the x value to store
     * @param y the y value to store
     */
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

    /**
     * Create a new GazePoint from JSON byte array
     * Also performs a data validity check
     *
     * @param jsonBytes A JSON representation of GazePoint as a byte array
     * @return A new GazePoint from the inputted JSON
     * @throws IOException Thrown if unable to read byte array as JSON
     */
    public static GazePoint fromJSON (byte[] jsonBytes) throws IOException {
        GazePoint ret = mapper.readValue(jsonBytes, GazePoint.class);
        ret.validCheck();
        return ret;
    }

    /**
     * String version of fromJSON
     *
     * @param jsonString A JSON representation of GazePoint as a string
     * @return A new GazePoint from the inputted JSON
     * @throws IOException Thrown if unable to read string as JSON
     * @see #fromJSON(byte[])
     */
    public static GazePoint fromJSON (String jsonString) throws IOException {
        GazePoint ret = mapper.readValue(jsonString, GazePoint.class);
        ret.validCheck();
        return ret;
    }

    /**
     * Perform a validity check on itself
     * Throws an IllegalArgumentException if a value is out of range
     */
    private void validCheck() {
        if (x < 0 || x > 1) {
            throw new IllegalArgumentException("GazePoint x out of range: " + x + " is not within range of 0.0 and 1.0");
        }
        if (y < 0 || y > 1) {
            throw new IllegalArgumentException("GazePoint y out of range: " + y + " is not within range of 0.0 and 1.0");
        }
    }
}
