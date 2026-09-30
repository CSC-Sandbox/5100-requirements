package edu.calpoly.robot;

import java.util.Locale;
import java.util.Random;

/**
 * Publishes sample JSON robot data to test RobotMQTTAdapter.
 * Test Instructions:
 * 1. Start RobotServer.java
 * 2. Start TestRobotMQTTAdapter.java
 * 3. Check the printouts and the GUI from the RobotServer.java. There should be waves of valid and ivnalid messages.
 * @author Paul Motter (PaulMotter)
 * @author Jess A (0x10jalencas)
 * @version 1.1.0 (9/28/2026)
 */
public class TestRobotMQTTAdapter {

    private static final String INVALID_MESSAGE =
            "{\"jointAngles\":[1.0,2.0],\"position\":[0.0]}";

    private static final Random RANDOM = new Random();
    private static final long DELAY_MS = 500;

    /**
     * Publishes valid and invalid robot messages.
     *
     * @param args command-line arguments; unused
     * @throws Exception if MQTT communication fails
     */
    public static void main(String[] args) throws Exception {
        RobotMQTTProvider provider = new RobotMQTTProvider(
                RobotServer.MQTT_HOST,
                RobotServer.MQTT_PORT, 
                RobotServer.ROBOT_ID
        );

        boolean publishValid = false;
        // Switches between publishing valid and invalid messages every 10 iterations.
        for(int iteration=0; iteration<10_000; ++iteration){
                if (iteration%10 == 0){
                        publishValid = publishValid ? false : true;
                }

                if (publishValid) provider.publish(simulatedPose());
                else provider.publish(INVALID_MESSAGE);
                Thread.sleep(DELAY_MS);
        }
    }

   private static String simulatedPose() {
        double[] v = new double[9];
        for (int i = 0; i < 6; i++) v[i] = -Math.PI + RANDOM.nextDouble() * 2 * Math.PI;
        for (int i = 6; i < 9; i++) v[i] = -1.0 + RANDOM.nextDouble() * 2.0;
        return String.format(
                Locale.US, 
                "{\"jointAngles\":[%.3f,%.3f,%.3f,%.3f,%.3f,%.3f],\"position\":[%.3f,%.3f,%.3f]}",
                v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8]);
    }
}