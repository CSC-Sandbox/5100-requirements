package edu.calpoly.monitor;

import edu.calpoly.provided.Broker;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class MonitorAvailability {

    private static final long TIMEOUT_MS = 1000;

    private final Map<String, Long> lastReceived =
            new ConcurrentHashMap<>();

    private final Map<String, Boolean> previousAvailability =
            new ConcurrentHashMap<>();

    private final List<CommunicationFailure> communicationFailures =
            new CopyOnWriteArrayList<>();

    private JLabel robotStatus;
    private JLabel gazeStatus;
    private JLabel affectStatus;
    private JLabel lidarStatus;

    public record CommunicationFailure(
            String component,
            LocalDateTime timestamp) {
    }

    public MonitorAvailability() {
        lastReceived.put("ROBOT", 0L);
        lastReceived.put("GAZE", 0L);
        lastReceived.put("AFFECT", 0L);
        lastReceived.put("LIDAR", 0L);

        previousAvailability.put("ROBOT", false);
        previousAvailability.put("GAZE", false);
        previousAvailability.put("AFFECT", false);
        previousAvailability.put("LIDAR", false);

        createGUI();
        startStatusTimer();
    }

    public void recordMessage(String source) {
        if (lastReceived.containsKey(source)) {
            lastReceived.put(source, System.currentTimeMillis());
        }
    }

    public boolean isAvailable(String source) {
        Long lastTime = lastReceived.get(source);

        if (lastTime == null || lastTime == 0) {
            return false;
        }

        return System.currentTimeMillis() - lastTime <= TIMEOUT_MS;
    }

    public List<CommunicationFailure> getCommunicationFailures() {
        return List.copyOf(communicationFailures);
    }

    private void processMessage(String message) {
        if (message == null || message.isBlank()) {
            return;
        }

        String[] parts = message.split(",");

        if (parts.length == 0) {
            return;
        }

        String source = parts[0];

        recordMessage(source);
    }

    private void recordCommunicationFailure(String source) {
        CommunicationFailure failure =
                new CommunicationFailure(source, LocalDateTime.now());

        communicationFailures.add(failure);

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        System.out.println(
                "Communication failure: "
                        + failure.component()
                        + " at "
                        + failure.timestamp().format(formatter)
        );
    }

    private void createGUI() {
        JFrame frame = new JFrame("Availability Monitor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(
                new GridLayout(4, 2, 10, 10)
        );

        panel.add(new JLabel("Robot"));
        robotStatus = new JLabel("UNAVAILABLE");
        panel.add(robotStatus);

        panel.add(new JLabel("Gaze"));
        gazeStatus = new JLabel("UNAVAILABLE");
        panel.add(gazeStatus);

        panel.add(new JLabel("Affect"));
        affectStatus = new JLabel("UNAVAILABLE");
        panel.add(affectStatus);

        panel.add(new JLabel("LiDAR"));
        lidarStatus = new JLabel("UNAVAILABLE");
        panel.add(lidarStatus);

        frame.add(panel);

        frame.setSize(350, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void updateGUI() {
        updateStatus("ROBOT", robotStatus);
        updateStatus("GAZE", gazeStatus);
        updateStatus("AFFECT", affectStatus);
        updateStatus("LIDAR", lidarStatus);
    }

    private void updateStatus(
            String source,
            JLabel statusLabel) {

        boolean available = isAvailable(source);
        boolean wasAvailable =
                previousAvailability.get(source);

        if (wasAvailable && !available) {
            recordCommunicationFailure(source);
        }

        statusLabel.setText(
                available ? "AVAILABLE" : "UNAVAILABLE"
        );

        previousAvailability.put(source, available);
    }

    private void startStatusTimer() {
        Timer timer = new Timer(
                100,
                e -> updateGUI()
        );

        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MonitorAvailability monitor =
                    new MonitorAvailability();

            Thread receiverThread = new Thread(() -> {
                Broker broker =
                        new Broker("localhost", 5000);

                while (true) {
                    String message =
                            broker.receive();

                    monitor.processMessage(message);
                }
            });

            receiverThread.start();
        });
    }
}