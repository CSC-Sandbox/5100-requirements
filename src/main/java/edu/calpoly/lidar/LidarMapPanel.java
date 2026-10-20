package edu.calpoly.lidar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

/**
 * Draws received LiDAR points as a top-down 2D map.
 *
 * @author Jess A
 * @version September 30, 2026
 */
public final class LidarMapPanel extends JPanel {
    private static final double WORLD_RANGE = 5.0;
    private static final int MARGIN = 30;

    private final List<LidarPoint> points = new ArrayList<>();

    /**
     * Creates our empty LiDAR map.
     */
    public LidarMapPanel() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.WHITE);
    }

    /**
     * Adds a LiDAR point and redraws the map.
     *
     * @param point received LiDAR measurement
     */
    public void addPoint(LidarPoint point) {
        points.add(point);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int plotWidth = getWidth() - 2 * MARGIN;
        int plotHeight = getHeight() - 2 * MARGIN;

        if (plotWidth <= 0 || plotHeight <= 0) {
            g2.dispose();
            return;
        }

        g2.setColor(Color.LIGHT_GRAY);
        g2.drawRect(MARGIN, MARGIN, plotWidth, plotHeight);

        g2.setColor(Color.GRAY);
        g2.drawLine(mapX(0, plotWidth), MARGIN,
                mapX(0, plotWidth), MARGIN + plotHeight);
        g2.drawLine(MARGIN, mapY(0, plotHeight),
                MARGIN + plotWidth, mapY(0, plotHeight));

        g2.setColor(new Color(30, 100, 220));
        for (LidarPoint point : points) {
            int x = mapX(point.x(), plotWidth);
            int y = mapY(point.y(), plotHeight);
            g2.fillOval(x - 2, y - 2, 4, 4);
        }

        g2.dispose();
    }

    private int mapX(double x, int plotWidth) {
        return MARGIN + (int) Math.round(
                (x + WORLD_RANGE) * plotWidth / (2 * WORLD_RANGE));
    }

    private int mapY(double y, int plotHeight) {
        return MARGIN + plotHeight - (int) Math.round(
                (y + WORLD_RANGE) * plotHeight / (2 * WORLD_RANGE));
    }
}