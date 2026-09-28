package edu.calpoly.monitor;

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
import java.util.Optional;

/**
 * Displays recent message activity obtained from the paired activity
 *
 * @author Aiden Rodriguez
 * @version September 27, 2026
 */
public final class DisplayDataActivity extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final int REFRESH_INTERVAL_MILLIS = 250;
    private static final Color BACKGROUND = new Color(245, 247, 250);
    private static final Color BAR_COLOR = new Color(52, 120, 246);
    private static final String DEFAULT_REST_BASE_URI = "http://localhost:8080";

    private final ActivitySnapshotSource activitySource;
    private final EnumMap<DataSource, JLabel> countLabels = new EnumMap<>(DataSource.class);
    private final EnumMap<DataSource, JProgressBar> activityBars = new EnumMap<>(DataSource.class);
    private final JLabel connectionStatus = new JLabel("Starting activity consumer...");
    private final JLabel subtitle = new JLabel("Messages received in the last 60 seconds");

    private DisplayDataActivity(ActivitySnapshotSource activitySource) {
        this.activitySource = activitySource;
        setLayout(new BorderLayout(0, 22));
        setBackground(BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(26, 34, 22, 34));

        add(createHeading(), BorderLayout.NORTH);
        add(createActivityGrid(), BorderLayout.CENTER);

        connectionStatus.setForeground(new Color(88, 96, 105));
        connectionStatus.getAccessibleContext().setAccessibleName("Activity source connection status");
        add(connectionStatus, BorderLayout.SOUTH);

        Timer refreshTimer = new Timer(REFRESH_INTERVAL_MILLIS, event -> refreshDisplay());
        refreshTimer.setInitialDelay(0);
        refreshTimer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DisplayDataActivity::createAndShowApplication);
    }

    private static void createAndShowApplication() {
        ActivitySnapshotSource activitySource = createConfiguredSource();
        DisplayDataActivity application = new DisplayDataActivity(activitySource);

        JFrame frame = new JFrame("Recent Data Activity");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(application);
        frame.setMinimumSize(new Dimension(620, 350));
        frame.pack();
        frame.setSize(720, 410);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        activitySource.start();
        Runtime.getRuntime().addShutdownHook(
                new Thread(activitySource::close, "activity-source-shutdown"));
    }

    /**
     * Chooses which outward interface to consume. REST is the default,
     * matching the architecture note that REST is the preferred interface;
     * passing {@code -Dactivity.source=mqtt} switches to the experimental
     * MQTT path without any code change, so both can be demonstrated from
     * the same GUI.
     *
     * @return the configured, not-yet-started activity source
     */
    private static ActivitySnapshotSource createConfiguredSource() {
        String sourceType = System.getProperty("activity.source", "rest");
        if ("mqtt".equalsIgnoreCase(sourceType)) {
            String broker = System.getProperty("activity.mqtt.broker", ActivityMqttConsumer.DEFAULT_BROKER);
            String topic = System.getProperty("activity.mqtt.topic", ActivityMqttConsumer.DEFAULT_TOPIC);
            return new ActivityMqttConsumer(broker, topic);
        }
        String baseUri = System.getProperty("activity.rest.baseUri", DEFAULT_REST_BASE_URI);
        return new ActivityRestClient(baseUri);
    }

    private JPanel createHeading() {
        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Recent Data Activity");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 26f));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

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

    /**
     * Runs on the Swing event thread every {@link #REFRESH_INTERVAL_MILLIS}.
     * Reads whatever the source last obtained; it does not itself perform
     * any network I/O, so it never blocks the UI.
     */
    private void refreshDisplay() {
        connectionStatus.setText(activitySource.status());

        Optional<ActivitySnapshot> snapshotOpt = activitySource.latestSnapshot();
        if (snapshotOpt.isEmpty()) {
            return; // Keep the initial "0 messages" display until data arrives.
        }

        ActivitySnapshot snapshot = snapshotOpt.get();
        subtitle.setText("Messages received in the last " + snapshot.windowSeconds() + " seconds");

        Map<DataSource, Integer> counts = snapshot.counts();
        int maximum = Math.max(1, counts.values().stream().mapToInt(Integer::intValue).max().orElse(1));

        for (DataSource source : DataSource.values()) {
            int count = counts.get(source);
            countLabels.get(source).setText(count + (count == 1 ? " message" : " messages"));
            JProgressBar activityBar = activityBars.get(source);
            activityBar.setMaximum(maximum);
            activityBar.setValue(count);
        }
    }
}