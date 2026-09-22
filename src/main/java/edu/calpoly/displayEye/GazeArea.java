package edu.calpoly.displayEye;

import java.awt.*;
import javax.swing.*;

// The visualization of the current gaze position as a red dot
public class GazeArea extends JPanel {
    public final GazePoint gazePoint = new GazePoint(0.5, 0.5);

    GazeArea() {
        super();

        setLayout(new BorderLayout());

        // North border layout for the (0, 0) and (1, 0) labels
        JPanel topGazeArea = new JPanel(new BorderLayout());
        topGazeArea.setOpaque(false);
        topGazeArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JLabel labelNW = new JLabel("(0.0, 0.0)");
        JLabel labelNE = new JLabel("(1.0, 0.0)");
        topGazeArea.add(labelNW, BorderLayout.WEST);
        topGazeArea.add(labelNE, BorderLayout.EAST);

        // South border layout for the (0, 1) and (1, 1) labels
        JPanel bottomGazeArea = new JPanel(new BorderLayout());
        bottomGazeArea.setOpaque(false);
        bottomGazeArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JLabel labelSW = new JLabel("(0.0, 1.0)");
        JLabel labelSE = new JLabel("(1.0, 1.0)");
        bottomGazeArea.add(labelSW, BorderLayout.WEST);
        bottomGazeArea.add(labelSE, BorderLayout.EAST);

        add(topGazeArea, BorderLayout.NORTH);
        add(bottomGazeArea, BorderLayout.SOUTH);
    }

    protected void paintComponent(Graphics g) {
        // Repaint as normal first
        super.paintComponent(g);
        int radius = 20;

        // Calculate actual width and height not including borders
        Insets insets = getInsets();
        int width = getWidth() - insets.left - insets.right;
        int height = getHeight() - insets.top - insets.bottom;

        // Calculate actual X,Y in pixels from the 0-1 range GazePoint gives
        int panelX = (int) Math.round(gazePoint.x * width);
        int panelY = (int) Math.round(gazePoint.y * height);

        // Offset X and Y by the left and top to make (0,0) still inside the border
        panelX += insets.left;
        panelY += insets.top;

        // Draw the circle on another graphics to clip the circle but not the borders
        Graphics2D g2 = (Graphics2D) g.create();
        g2.clipRect(insets.left, insets.top, width, height);
        g2.setColor(Color.red);
        g2.fillOval(panelX - radius, panelY - radius, 2 * radius, 2 * radius);
    }

    public void setGazePoint(GazePoint gazePoint) {
        this.gazePoint.setXY(gazePoint.x, gazePoint.y);
    }
}
