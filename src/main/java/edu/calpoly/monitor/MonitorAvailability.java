package edu.calpoly.monitor;

import edu.calpoly.provided.Broker;

import java.util.Map;
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ConcurrentHashMap;

public class MonitorAvailability {

    private static final long TIMEOUT_MS = 1000;

    private final Map<String, Long> lastReceived = new ConcurrentHashMap<>();

    private JLabel robotStatus;
    private JLabel gazeStatus;
    private JLabel affectStatus;
    private JLabel lidarStatus;

    public MonitorAvailability() {
        lastReceived.put("ROBOT", 0L);
        lastReceived.put("GAZE", 0L);
        lastReceived.put("AFFECT", 0L);
        lastReceived.put("LIDAR", 0L);

        createGUI();
        startStatusTimer();
    }

    public void recordMessage(String source) {
        if (lastReceived.containsKey(source)) {
            lastReceived.put(source, System.currentTimeMillis());
        }
    }
    public boolean isAvailable(String source) {
        long lastTime = lastReceived.get(source);

        if (lastTime == 0) {
            return false;
        }

        return System.currentTimeMillis() - lastTime <= TIMEOUT_MS;
    }
    private void processMessage(String message) {
        String[] parts = message.split(",");

        if (parts.length == 0) {
            return;
        }

        String source = parts[0];

        recordMessage(source);
    }

    private void createGUI() {
        JFrame frame = new JFrame("Availability Monitor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        panel.add(new JLabel("Robot"));
        robotStatus = new JLabel("UNAVAILABLE");
        panel.add(robotStatus);

        panel.add(new JLabel("Gaze"));
        gazeStatus = new JLabel("UNAVAILABLE");
        panel.add(gazeStatus);

        panel.add(new JLabel("Affect"));
        affectStatus = new JLabel("UNAVAILABLE");
        panel.add(affectStatus);

        panel.add(new JLabel("Lidar"));
        lidarStatus = new JLabel("UNAVAILABLE");
        panel.add(lidarStatus);

        frame.add(panel);
        frame.setSize(350, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void updateGUI() {
        robotStatus.setText(
                isAvailable("ROBOT") ? "AVAILABLE" : "UNAVAILABLE"
        );

        gazeStatus.setText(
                isAvailable("GAZE") ? "AVAILABLE" : "UNAVAILABLE"
        );

        affectStatus.setText(
                isAvailable("AFFECT") ? "AVAILABLE" : "UNAVAILABLE"
        );

        lidarStatus.setText(
                isAvailable("LIDAR") ? "AVAILABLE" : "UNAVAILABLE"
        );
    }

    private void startStatusTimer() {
        Timer timer = new Timer(100, e -> updateGUI());
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MonitorAvailability monitor = new MonitorAvailability();

            Thread receiverThread = new Thread(() -> {
                Broker broker = new Broker("localhost", 5000);

                while (true) {
                    String message = broker.receive();
                    monitor.processMessage(message);
                }
            });

            receiverThread.start();
        });
    }
}
