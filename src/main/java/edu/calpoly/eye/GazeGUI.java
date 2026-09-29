package edu.calpoly.eye;

import javax.swing.*;
import java.awt.*;

/**
 * Manages the different UI components that are shown to the user
 * Also handles updating those components upon receiving an updated gaze position
 * 
 * @author James Yaguma
 * @version 1.0 (2026-09-25)
*/
public class GazeGUI {

    private final GazeArea gazeArea;
    private final GazeInfo gazeInfo;
    private final JPanel panel;

    /**
     * Constructor for GazeGUI
     *
     * @param width width of the space given in the frame
     * @param height height of the space given in the frame
     */
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

    /**
     * Used to add the GUI elements managed by GazeGUI to the specified JFrame
     * 
     * @param frame The JFrame to add the components to
     */
    public void addToFrame(JFrame frame) {
        frame.add(panel);
    }

    /**
     * Used when new gaze position data is received and components need to be updated
     * Updates the component's stored gaze position and queues a repaint
     *
     * @param gazePoint The new gaze position data to use
     */
    public void update(GazePoint gazePoint) {
        gazeArea.setGazePoint(gazePoint);
        gazeArea.repaint();

        gazeInfo.setGazePoint(gazePoint);
        gazeInfo.conStatus = true;
        gazeInfo.repaint();
    }
}
