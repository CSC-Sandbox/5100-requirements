package edu.calpoly.lidar;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import edu.calpoly.provided.Broker;

/**
 * Receives LiDAR measurements and displays them as a 2D map.
 *
 * @author Jess A
 * @version September 30, 2026
 */
public final class DisplayLidar {
    private static final String BROKER_HOST = "localhost";
    private static final int BROKER_PORT = 5000;

    private DisplayLidar() {
    }

    /**
     * Starts LiDAR map app.
     *
     * @param args command-line arguments; currently unused
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(DisplayLidar::start);
    }

    private static void start() {
        LidarMapPanel mapPanel = new LidarMapPanel();

        JFrame frame = new JFrame("LiDAR Map");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(mapPanel);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);

        Thread receiver = new Thread(
                () -> receivePoints(mapPanel),
                "lidar-receiver");
        receiver.setDaemon(true);
        receiver.start();
    }

    private static void receivePoints(LidarMapPanel mapPanel) {
        Broker broker = new Broker(BROKER_HOST, BROKER_PORT);

        while (true) {
            try {
                String message = broker.receive();
                LidarPoint point = parsePoint(message);
                SwingUtilities.invokeLater(() -> mapPanel.addPoint(point));
            } catch (IllegalArgumentException exception) {
                System.err.println(
                        "Ignoring invalid LiDAR message: " + exception.getMessage());
            } catch (IllegalStateException exception) {
                System.err.println(
                        "Unable to receive LiDAR data: " + exception.getMessage());
                return;
            }
        }
    }

    private static LidarPoint parsePoint(String message) {
        String[] values = message.split(",");

        if (values.length != 4 || !values[0].trim().equals("LIDAR")) {
            throw new IllegalArgumentException("expected LIDAR,x,y,z");
        }

        try {
            double x = Double.parseDouble(values[1].trim());
            double y = Double.parseDouble(values[2].trim());
            double z = Double.parseDouble(values[3].trim());
            return new LidarPoint(x, y, z);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("coordinates must be numbers");
        }
    }
}