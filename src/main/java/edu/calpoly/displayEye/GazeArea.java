package edu.calpoly.displayEye;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Insets;
import javax.swing.JPanel;

// The visualization of the current gaze position as a red dot
public class GazeArea extends JPanel {
    public final GazePoint gazePoint = new GazePoint(0.5, 0.5);

    protected void paintComponent(Graphics g) {
        // Repaint as normal first
        super.paintComponent(g);
        int radius = 15;

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
}
