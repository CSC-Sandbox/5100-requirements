package edu.calpoly.displayEye;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.*;

public class GazeGUI {
    private final GazeArea gazeArea;
    private final JPanel panel;

    GazeGUI(int width, int height) {
        panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(200, 200, 200)); // Just a gray background

        // Create area with about 75% width
        gazeArea = new GazeArea();
        gazeArea.setPreferredSize(new Dimension(width * 3 / 4, height));
        gazeArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                BorderFactory.createLineBorder(Color.BLACK, 2)
        ));
        gazeArea.setOpaque(false); // Background transparent
        gazeArea.setLayout(new BorderLayout());

        // North border layout for the (0, 0) and (1, 0) labels
        JPanel topGazeArea = new JPanel(new BorderLayout());
        topGazeArea.setOpaque(false);
        JLabel labelNW = new JLabel("(0.0, 0.0)");
        JLabel labelNE = new JLabel("(1.0, 0.0)");
        topGazeArea.add(labelNW, BorderLayout.WEST);
        topGazeArea.add(labelNE, BorderLayout.EAST);

        // South border layout for the (0, 1) and (1, 1) labels
        JPanel bottomGazeArea = new JPanel(new BorderLayout());
        bottomGazeArea.setOpaque(false);
        JLabel labelSW = new JLabel("(0.0, 1.0)");
        JLabel labelSE = new JLabel("(1.0, 1.0)");
        bottomGazeArea.add(labelSW, BorderLayout.WEST);
        bottomGazeArea.add(labelSE, BorderLayout.EAST);

        gazeArea.add(topGazeArea, BorderLayout.NORTH);
        gazeArea.add(bottomGazeArea, BorderLayout.SOUTH);

        // Actually add it to the panel
        panel.add(gazeArea, BorderLayout.WEST);
    }

    public void addToFrame(JFrame frame) {
        frame.add(panel);
    }

    public void update(GazePoint gazePoint) {
        // Update stored XY and queue a repaint
        gazeArea.gazePoint.setXY(gazePoint.x, gazePoint.y);
        gazeArea.repaint();
    }
}
