package edu.calpoly.monitor;

import edu.calpoly.provided.Broker;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.EnumMap;
import java.util.Map;

/**
 * Provides the Swing user interface for monitoring recent broker message
 * activity. The panel receives messages in the background and presents rolling
 * counts for each supported data source without blocking the event-dispatch
 * thread.
 *
 * @author Dylan Gururajan
 * @version September 25, 2026
 */
public final class DisplayDataActivity extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final String BROKER_HOST = "localhost";
    private static final int BROKER_PORT = 5000;
    private static final int REFRESH_INTERVAL_MILLIS = 250;
    private static final long RETRY_DELAY_MILLIS = 1_000;
    private static final Color BACKGROUND = new Color(245, 247, 250);
    private static final Color BAR_COLOR = new Color(52, 120, 246);

    private final ActivityTracker tracker;
    private final EnumMap<DataSource, JLabel> countLabels = new EnumMap<>(DataSource.class);

    private final EnumMap<DataSource, JProgressBar> activityBars =
            new EnumMap<>(DataSource.class);

    private final JLabel connectionStatus = new JLabel("Starting data receiver...");

    private DisplayDataActivity(ActivityTracker tracker) {
        this.tracker = tracker;
        setLayout(new BorderLayout(0, 22));
        setBackground(BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(26, 34, 22, 34));

        add(createHeading(), BorderLayout.NORTH);
        add(createActivityGrid(), BorderLayout.CENTER);

        connectionStatus.setForeground(new Color(88, 96, 105));
        connectionStatus.getAccessibleContext().setAccessibleName("Broker connection status");
        add(connectionStatus, BorderLayout.SOUTH);

        Timer refreshTimer = new Timer(REFRESH_INTERVAL_MILLIS, event -> refreshCounts());
        refreshTimer.setInitialDelay(0);
        refreshTimer.start();
    }

    /**
     * Launches the recent-data activity monitor on the Swing event-dispatch
     * thread.
     *
     * @param args command-line arguments; currently unused
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(DisplayDataActivity::createAndShowApplication);
    }

    private static void createAndShowApplication() {
        DisplayDataActivity application = new DisplayDataActivity(new ActivityTracker());

        JFrame frame = new JFrame("Recent Data Activity");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(application);
        frame.setMinimumSize(new Dimension(620, 350));
        frame.pack();
        frame.setSize(720, 410);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        application.startReceiver();
    }

    private JPanel createHeading() {
        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Recent Data Activity");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 26f));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Messages received in the last 60 seconds");
        subtitle.setForeground(new Color(88, 96, 105));
        subtitle.setFont(subtitle.getFont().deriveFont(14f));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        heading.add(title);
        heading.add(Box.createVerticalStrut(4));
        heading.add(subtitle);
        return heading;
    }

    private JPanel createActivityGrid() {
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = 0;
        constraints.insets = new Insets(8, 0, 8, 14);
        constraints.fill = GridBagConstraints.HORIZONTAL;

        for (DataSource source : DataSource.values()) {
            JLabel sourceLabel = new JLabel(source.displayName());
            sourceLabel.setFont(sourceLabel.getFont().deriveFont(Font.BOLD, 15f));
            sourceLabel.setPreferredSize(new Dimension(74, 28));

            constraints.gridx = 0;
            constraints.weightx = 0;
            grid.add(sourceLabel, constraints);

            JProgressBar activityBar = new JProgressBar(0, 1);
            activityBar.setForeground(BAR_COLOR);
            activityBar.setBackground(new Color(220, 225, 232));
            activityBar.setBorderPainted(false);
            activityBar.setPreferredSize(new Dimension(320, 20));
            activityBars.put(source, activityBar);

            constraints.gridx = 1;
            constraints.weightx = 1;
            grid.add(activityBar, constraints);

            JLabel countLabel = new JLabel("0 messages", SwingConstants.RIGHT);
            countLabel.setFont(countLabel.getFont().deriveFont(Font.BOLD, 14f));
            countLabel.setPreferredSize(new Dimension(130, 28));
            countLabels.put(source, countLabel);

            constraints.gridx = 2;
            constraints.weightx = 0;
            constraints.insets = new Insets(8, 0, 8, 0);
            grid.add(countLabel, constraints);

            constraints.gridy++;
            constraints.insets = new Insets(8, 0, 8, 14);
        }

        return grid;
    }

    private void refreshCounts() {
        Map<DataSource, Integer> counts = tracker.snapshot();
        int maximum = Math.max(1, counts.values().stream().mapToInt(Integer::intValue).max().orElse(1));

        for (DataSource source : DataSource.values()) {
            int count = counts.get(source);
            countLabels.get(source).setText(count + (count == 1 ? " message" : " messages"));
            JProgressBar activityBar = activityBars.get(source);
            activityBar.setMaximum(maximum);
            activityBar.setValue(count);
        }
    }

    private void startReceiver() {
        Thread receiverThread = new Thread(
                () -> receiveMessages(new Broker(BROKER_HOST, BROKER_PORT)),
                "broker-message-receiver");
        receiverThread.setDaemon(true);
        receiverThread.start();
    }

    private void receiveMessages(Broker broker) {
        boolean connected = false;
        setConnectionStatus("Connecting to localhost:5000...");

        while (!Thread.currentThread().isInterrupted()) {
            try {
                String message = broker.receive();
                if (!connected) {
                    setConnectionStatus("Receiving data from localhost:5000");
                    connected = true;
                }
                DataSource.fromMessage(message).ifPresent(tracker::record);
            } catch (IllegalStateException exception) {
                connected = false;
                setConnectionStatus("Waiting for the data broker on localhost:5000...");
                if (!pauseBeforeRetry()) {
                    return;
                }
            }
        }
    }

    private void setConnectionStatus(String status) {
        SwingUtilities.invokeLater(() -> connectionStatus.setText(status));
    }

    private static boolean pauseBeforeRetry() {
        try {
            Thread.sleep(RETRY_DELAY_MILLIS);
            return true;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
