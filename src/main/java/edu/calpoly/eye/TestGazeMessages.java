package edu.calpoly.eye;

/**
 * Class to hold all gaze JSON test messages in one place
 *
 * @author James Yaguma
 * @version 1.0 (2026-09-30)
 */
public class TestGazeMessages {
    public static final String[] messages = {
            "{\"x\": 0.867,\"y\": 0.5309}",      // OK: Normal
            "foobar",                            // Fail: Not even JSON
            "{\"foo\": \"bar\"",                 // Fail: Straight up wrong
            "{\"x\": 0.867}",                    // OK: Missing y (default y to 0)
            "{\"y\": 0.5309}",                   // OK: Missing x (default x to 0)
            "{\"x\": -0.867,\"y\": 0.5309}",     // Fail: x < 0
            "{\"x\": 0.867,\"y\": 1.5309}",      // Fail: y > 1
            "{\"z\": 0.867,\"y\": 0.5309}",      // Fail: No x (replaced with z)
            "{\"x\": \"0.86b\",\"y\": 0.5309}",  // Fail: x value is an invalid string
            "{\"x\": 0.214,\"y\": 0.623}",       // OK: Should work
    };
    public int index = 0;

    /**
     * Get the next message to test as a string
     * Starts at index 0
     *
     * @return The next message in the list
     */
    public String getNext() {
        String ret = messages[index];
        index = (index + 1) % messages.length;
        return ret;
    }

    /**
     * Same as getNext but returns a byte array instead
     *
     * @return The next message in the list
     * @see #getNext()
     */
    public byte[] getNextBytes() {
        byte[] ret = messages[index].getBytes();
        index = (index + 1) % messages.length;
        return ret;
    }
}
