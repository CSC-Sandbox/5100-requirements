package edu.calpoly.monitor;

import edu.calpoly.provided.Broker;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Monitors the communication availability of Robot, Gaze, Affect, and LiDAR
 * data sources.
 *
 * <p>The monitor tracks when data was most recently received from each source
 * and considers a source unavailable when it has not communicated within the
 * configured timeout period. Communication failures are recorded with the
 * affected component and the time the failure was detected.</p>
 *
 * <p>A Swing interface displays the current availability of all monitored
 * sources while messages are received through the course-provided
 * {@link Broker}.</p>
 *
 * @author Adrian Valenzuela (adrian0427)
 * @version September 29, 2026
 */
public class MonitorAvailability {

    private static final long TIMEOUT_MS = 1000;

    private final Map<DataSource, Long> lastReceived =
            new ConcurrentHashMap<>();

    private final Map<DataSource, Boolean> previousAvailability =
            new ConcurrentHashMap<>();

    private final List<CommunicationFailure> communicationFailures =
            new CopyOnWriteArrayList<>();

    private JLabel robotStatus;
    private JLabel gazeStatus;
    private JLabel affectStatus;
    private JLabel lidarStatus;

    /**
     * Represents a detected loss of communication from a monitored component.
     *
     * @param component the component that stopped communicating
     * @param timestamp the time at which the communication failure was detected
     */
    public record CommunicationFailure(
            DataSource component,
            LocalDateTime timestamp) {
    }

    /**
     * Creates an availability monitor for the supported data sources and
     * initializes the graphical status display.
     *
     * <p>Sources begin as unavailable until their first message is received.</p>
     */
    public MonitorAvailability() {
        for (DataSource source : DataSource.values()) {
            lastReceived.put(source, 0L);
            previousAvailability.put(source, false);
        }

        createGUI();
        startStatusTimer();
    }

    /**
     * Records that a message was received from a monitored source.
     *
     * @param source the source that produced the message
     */
    public void recordMessage(DataSource source) {
        lastReceived.put(source, System.currentTimeMillis());
    }

    /**
     * Determines whether a monitored source has communicated within the
     * configured timeout period.
     *
     * <p>A source that has never sent a message is considered unavailable.</p>
     *
     * @param source the monitored source
     * @return {@code true} if the source has communicated within the timeout
     *         period; {@code false} otherwise
     */
    public boolean isAvailable(DataSource source) {
        Long lastTime = lastReceived.get(source);

        if (lastTime == null || lastTime == 0) {
            return false;
        }

        return System.currentTimeMillis() - lastTime <= TIMEOUT_MS;
    }

    /**
     * Returns the current availability state of every monitored source.
     *
     * <p>This snapshot is intended to be shared by communication adapters such
     * as REST and MQTT so they expose the same underlying monitor state.</p>
     *
     * @return a snapshot mapping each source to its current availability
     */
    public Map<DataSource, Boolean> getAvailabilitySnapshot() {
        Map<DataSource, Boolean> snapshot =
                new EnumMap<>(DataSource.class);

        for (DataSource source : DataSource.values()) {
            snapshot.put(source, isAvailable(source));
        }

        return snapshot;
    }

    /**
     * Returns a snapshot of the communication failures detected by the monitor.
     *
     * @return an unmodifiable copy of the recorded communication failures
     */
    public List<CommunicationFailure> getCommunicationFailures() {
        return List.copyOf(communicationFailures);
    }

    private void processMessage(String message) {
        DataSource.fromMessage(message)
                .ifPresent(this::recordMessage);
    }

    private void recordCommunicationFailure(DataSource source) {
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

        panel.add(new JLabel(DataSource.ROBOT.displayName()));
        robotStatus = new JLabel("UNAVAILABLE");
        panel.add(robotStatus);

        panel.add(new JLabel(DataSource.GAZE.displayName()));
        gazeStatus = new JLabel("UNAVAILABLE");
        panel.add(gazeStatus);

        panel.add(new JLabel(DataSource.AFFECT.displayName()));
        affectStatus = new JLabel("UNAVAILABLE");
        panel.add(affectStatus);

        panel.add(new JLabel(DataSource.LIDAR.displayName()));
        lidarStatus = new JLabel("UNAVAILABLE");
        panel.add(lidarStatus);

        frame.add(panel);

        frame.setSize(350, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void updateGUI() {
        updateStatus(DataSource.ROBOT, robotStatus);
        updateStatus(DataSource.GAZE, gazeStatus);
        updateStatus(DataSource.AFFECT, affectStatus);
        updateStatus(DataSource.LIDAR, lidarStatus);
    }

    private void updateStatus(
            DataSource source,
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

    /**
     * Starts the availability monitor application.
     *
     * <p>The Swing interface is created on the event dispatch thread while a
     * separate receiver thread listens for messages from the provided
     * {@link Broker}.</p>
     *
     * @param args command-line arguments; not used by this application
     */
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