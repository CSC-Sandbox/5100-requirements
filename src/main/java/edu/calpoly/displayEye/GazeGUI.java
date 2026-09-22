package edu.calpoly.displayEye;

import javax.swing.*;
import java.awt.*;

public class GazeGUI {
    private final GazeArea gazeArea;
    private final GazeInfo gazeInfo;
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

        // Actually add it to the panel
        panel.add(gazeArea, BorderLayout.WEST);

        // Create and add the info sidebar taking up the rest of the space
        gazeInfo = new GazeInfo();
        gazeInfo.setOpaque(false);

        panel.add(gazeInfo, BorderLayout.CENTER);
    }

    public void addToFrame(JFrame frame) {
        frame.add(panel);
    }

    public void update(GazePoint gazePoint) {
        // Update stored XY and queue a repaint
        gazeArea.setGazePoint(gazePoint);
        gazeArea.repaint();

        gazeInfo.setGazePoint(gazePoint);
        gazeInfo.conStatus = true;
        gazeInfo.repaint();
    }
}
