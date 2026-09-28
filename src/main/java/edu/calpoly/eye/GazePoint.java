package edu.calpoly.eye;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

// Literally just a class to hold an (x,y) coordinate
public class GazePoint {

    public double x;
    public double y;
    public static final ObjectMapper mapper = new ObjectMapper();

    // Default Constructor (0,0)
    GazePoint() {
        x = 0.0;
        y = 0.0;
    }

    // Constructor with predefined (x,y)
    GazePoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Copy constructor
    GazePoint(GazePoint gazePoint) {
        x = gazePoint.x;
        y = gazePoint.y;
    }

    // Set both simultaneously
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
