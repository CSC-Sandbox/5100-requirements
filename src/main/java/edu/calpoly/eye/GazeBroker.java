package edu.calpoly.eye;

import java.util.function.Consumer;
import java.util.regex.*;

import edu.calpoly.provided.Broker;

/**
 * The broker class for receiving and validating GAZE messages
 * Utilizes Broker.java which was provided to send/receive messages over TCP sockets
 *
 * @author James Yaguma
 * @version 1.0 (2026-09-25)
 */
public class GazeBroker {

    private boolean debug; // Debug flag for logging
    private final Broker broker;
    private final GazePoint gazePoint;

    /**
     * GazeBroker constructor (with debugging false by default)
     *
     * @param host IP address of the host
     * @param port port to use
     */
    GazeBroker(String host, int port) {
        broker = new Broker(host, port);
        debug = false;
        gazePoint = new GazePoint(0, 0);
    }

    /**
     * GazeBroker constructor with explicit debug flag
     *
     * @param host IP address of the host
     * @param port port to use
     * @param debug debug flag
     */
    GazeBroker(String host, int port, boolean debug) {
        this(host, port);
        this.debug = debug;
    }

    /**
     * Starts the loop to constantly receive messages
     * Blocks the current thread, so multi-threading is recommended
     *
     * @param onMessage Method to call upon receiving a valid GAZE message
     */
    public void loopForever(Consumer<GazePoint> onMessage) {
        Pattern msgPattern = Pattern.compile("\\AGAZE,(-?\\d+(?:\\.\\d+)?),(-?\\d+(?:\\.\\d+)?)\\Z");
        try {
            while (true) {
                String message = broker.receive();
                Matcher msgMatcher = msgPattern.matcher(message);

                // Message did not match the desired format
                // Includes message not being "GAZE", X/Y not being numbers, and extra arguments
                if (!msgMatcher.find()) {
                    if (debug) {
                        System.out.println("Ignoring incoming message due to invalid format: " + message);
                    }
                    continue;
                }

                // Since they are numbers, this is safe
                double newX = Double.parseDouble(msgMatcher.group(1));
                double newY = Double.parseDouble(msgMatcher.group(2));

                // If out of range, ignore
                if (newX < 0.0 || newX > 1.0 || newY < 0.0 || newY > 1.0) {
                    if (debug) {
                        System.out.println("Ignoring incoming message due to coords out of bounds: " + message);
                    }
                    continue;
                }

                // Message seems to be fine so act on the message
                gazePoint.setXY(newX, newY);

                if (debug) {
                    System.out.println("New position: " + newX + ", " + newY);
                }

                onMessage.accept(gazePoint);
            }
        } catch (IllegalStateException e) {
            // TODO: Disconnected
        }
    }
}
