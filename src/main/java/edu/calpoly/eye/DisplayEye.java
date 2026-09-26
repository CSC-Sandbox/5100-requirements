package edu.calpoly.eye;

import javax.swing.*;

/**
 * Main file to run for the visualization
 * Creates and starts the broker (GazeBroker) used to receive messages
 * Also initializes the GUI frame
 *
 * @author James Yaguma
 * @version 1.0 (2026-09-25)
 */
public class DisplayEye {
    private final int width = 800;
    private final int height = 600;
    private final GazeBroker broker;
    private final GazeGUI gui;

    /**
     * DisplayEye constructor
     * Initializes the GazeBroker and GUI objects
     */
    DisplayEye() {
        broker = new GazeBroker("localhost", 5000, true);
        gui = new GazeGUI(width, height);
    }

    /**
     * Main method
     * Starts the application
     *
     * @param args Command line args (unused)
     */
    public static void main(String[] args) {
        DisplayEye displayEye = new DisplayEye();

        // Initial frame setup
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(displayEye.width, displayEye.height);
        frame.setResizable(false);

        // Add the GUI to the frame before turning the frame to visible
        displayEye.gui.addToFrame(frame);
        frame.setVisible(true);

        // Start receiving messages
        displayEye.broker.loopForever(displayEye.gui::update);
    }
}
