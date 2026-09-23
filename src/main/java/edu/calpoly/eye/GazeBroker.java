package edu.calpoly.eye;

import java.util.function.Consumer;
import java.util.regex.*;

import edu.calpoly.provided.Broker;

public class GazeBroker {

    private boolean debug; // Debug flag for logging
    private final Broker broker;
    private final GazePoint gazePoint;

    // Debug flag is false by default and optional
    GazeBroker(String host, int port) {
        broker = new Broker(host, port);
        debug = false;
        gazePoint = new GazePoint(0, 0);
    }

    GazeBroker(String host, int port, boolean debug) {
        this(host, port);
        this.debug = debug;
    }

    // Starts the loop to receive messages
    // Halts the current thread, so preferably multithread this
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
