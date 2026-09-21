package displayEye;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import edu.calpoly.provided.Broker;

public class GazeBroker {
    private boolean debug;
    private Broker broker;
    public double eyeX;
    public double eyeY;

    GazeBroker(String host, int port) {
        broker = new Broker(host, port);
        eyeX = 0.0;
        eyeY = 0.0;
        debug = false;
    }

    GazeBroker(String host, int port, boolean debug) {
        this(host, port);
        this.debug = debug;
    }

    public void loopForever () {
        Pattern msgPattern = Pattern.compile("GAZE,(-?\\d+(?:\\.\\d+)?),(-?\\d+(?:\\.\\d+)?)");
        while(true) {
            String message = broker.receive();
            Matcher msgMatcher = msgPattern.matcher(message);

            if (!msgMatcher.find()) {
                if (debug) {
                    System.out.println("Ignoring incoming message due to invalid format: " + message);
                }
                continue;
            }

            double newX = Double.parseDouble(msgMatcher.group(1));
            double newY = Double.parseDouble(msgMatcher.group(2));

            if (newX < 0.0 || newX > 1.0 || newY < 0.0 || newY > 1.0) {
                if (debug) {
                    System.out.println("Ignoring incoming message due to coords out of bounds: " + message);
                }
                continue;
            }

            eyeX = newX;
            eyeY = newY;

            if (debug) {
                System.out.println("New position: " + eyeX + ", " + eyeY);
            }
        }
    }
}
