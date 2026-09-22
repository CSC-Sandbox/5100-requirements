package edu.calpoly.provided;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class DisplayAffect extends JFrame {
    private static final int MAX_SAMPLES = 350;

    private final XYSeries focusSeries = new XYSeries("Focus");
    private final XYSeries excitementSeries = new XYSeries("Excitement");
    private final XYSeries engagementSeries = new XYSeries("Engagement");
    private final XYSeries interestSeries = new XYSeries("Interest");
    private final XYSeries stressSeries = new XYSeries("Stress");

    private final NumberAxis sampleAxis = new NumberAxis("Samples (Time)");

    private final JLabel statusLabel = new JLabel("● Receiving data...");
    private final JLabel focusValue = new JLabel("0.00");
    private final JLabel excitementValue = new JLabel("0.00");
    private final JLabel engagementValue = new JLabel("0.00");
    private final JLabel interestValue = new JLabel("0.00");
    private final JLabel stressValue = new JLabel("0.00");
    private final JLabel sampleCountLabel = new JLabel("Samples Received: 0");
    private final JLabel elapsedTimeLabel = new JLabel("Elapsed Time: 00:00:00");

    private final long startTime = System.currentTimeMillis();
    private int sampleNumber = 0;

    public DisplayAffect() {
        super("Affective State Monitor");

        setLayout(new BorderLayout(10, 10));

        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(12, 15, 0, 15));

        JLabel titleLabel = new JLabel("Real-Time Affective State");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        statusLabel.setForeground(new Color(40, 150, 40));
        header.add(titleLabel, BorderLayout.WEST);
        header.add(statusLabel, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
        add(createChartPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);

        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private ChartPanel createChartPanel() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(focusSeries);
        dataset.addSeries(excitementSeries);
        dataset.addSeries(engagementSeries);
        dataset.addSeries(interestSeries);
        dataset.addSeries(stressSeries);

        focusSeries.setMaximumItemCount(MAX_SAMPLES);
        excitementSeries.setMaximumItemCount(MAX_SAMPLES);
        engagementSeries.setMaximumItemCount(MAX_SAMPLES);
        interestSeries.setMaximumItemCount(MAX_SAMPLES);
        stressSeries.setMaximumItemCount(MAX_SAMPLES);

        JFreeChart chart = ChartFactory.createXYLineChart(
                null,
                "Samples (Time)",
                "Affective Value",
                dataset
        );

        // Removes the Focus/Excitement/etc. legend under the chart.
        chart.removeLegend();

        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(new Color(225, 225, 225));
        plot.setRangeGridlinePaint(new Color(225, 225, 225));

        NumberAxis valueAxis = (NumberAxis) plot.getRangeAxis();
        valueAxis.setRange(0.0, 1.0);

        sampleAxis.setRange(0, MAX_SAMPLES);
        plot.setDomainAxis(sampleAxis);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setSeriesPaint(0, new Color(45, 110, 210));
        renderer.setSeriesPaint(1, new Color(255, 125, 20));
        renderer.setSeriesPaint(2, new Color(55, 150, 45));
        renderer.setSeriesPaint(3, new Color(220, 45, 55));
        renderer.setSeriesPaint(4, new Color(135, 85, 215));
        plot.setRenderer(renderer);

        return new ChartPanel(chart);
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 15, 10));

        JLabel currentValuesLabel = new JLabel("Current Values");
        currentValuesLabel.setFont(new Font("SansSerif", Font.BOLD, 11));

        JPanel valuesPanel = new JPanel(new GridLayout(1, 5, 10, 0));
        valuesPanel.add(createValueCard("Focus", focusValue, new Color(45, 110, 210)));
        valuesPanel.add(createValueCard("Excitement", excitementValue, new Color(255, 125, 20)));
        valuesPanel.add(createValueCard("Engagement", engagementValue, new Color(55, 150, 45)));
        valuesPanel.add(createValueCard("Interest", interestValue, new Color(220, 45, 55)));
        valuesPanel.add(createValueCard("Stress", stressValue, new Color(135, 85, 215)));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.add(currentValuesLabel);
        leftPanel.add(valuesPanel);

        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.add(sampleCountLabel);
        infoPanel.add(elapsedTimeLabel);

        bottomPanel.add(leftPanel, BorderLayout.CENTER);
        bottomPanel.add(infoPanel, BorderLayout.EAST);

        return bottomPanel;
    }

    private JPanel createValueCard(String name, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new GridLayout(2, 1));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 225, 225)),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));

        JLabel nameLabel = new JLabel(name, SwingConstants.CENTER);
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));

        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        valueLabel.setForeground(color);

        card.add(nameLabel);
        card.add(valueLabel);
        return card;
    }

    private void addAffectValues(
            double focus,
            double excitement,
            double engagement,
            double interest,
            double stress
    ) {
        focusSeries.add(sampleNumber, focus);
        excitementSeries.add(sampleNumber, excitement);
        engagementSeries.add(sampleNumber, engagement);
        interestSeries.add(sampleNumber, interest);
        stressSeries.add(sampleNumber, stress);

        focusValue.setText(String.format("%.2f", focus));
        excitementValue.setText(String.format("%.2f", excitement));
        engagementValue.setText(String.format("%.2f", engagement));
        interestValue.setText(String.format("%.2f", interest));
        stressValue.setText(String.format("%.2f", stress));

        sampleNumber++;

        if (sampleNumber > MAX_SAMPLES) {
            sampleAxis.setRange(sampleNumber - MAX_SAMPLES, sampleNumber);
        }

        sampleCountLabel.setText("Samples Received: " + sampleNumber);
        elapsedTimeLabel.setText("Elapsed Time: " + getElapsedTime());
    }

    private String getElapsedTime() {
        long seconds = (System.currentTimeMillis() - startTime) / 1000;

        return String.format(
                "%02d:%02d:%02d",
                seconds / 3600,
                (seconds % 3600) / 60,
                seconds % 60
        );
    }

    private void receiveAffectData() {
        Broker broker = new Broker("localhost", 5000);

        while (true) {
            try {
                String message = broker.receive();
                String[] parts = message.split(",");

                if (parts.length != 6 || !parts[0].equals("AFFECT")) {
                    continue;
                }

                double focus = Double.parseDouble(parts[1]);
                double excitement = Double.parseDouble(parts[2]);
                double engagement = Double.parseDouble(parts[3]);
                double interest = Double.parseDouble(parts[4]);
                double stress = Double.parseDouble(parts[5]);

                SwingUtilities.invokeLater(() ->
                        addAffectValues(focus, excitement, engagement, interest, stress)
                );

            } catch (IllegalStateException | NumberFormatException e) {
                statusLabel.setText("● Connection stopped");
                statusLabel.setForeground(Color.RED);
                break;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DisplayAffect app = new DisplayAffect();
            app.setVisible(true);

            Thread receiverThread = new Thread(app::receiveAffectData);
            receiverThread.setDaemon(true);
            receiverThread.start();
        });
    }
}