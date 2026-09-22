package edu.calpoly.displayEye;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;

public class DisplayEye {
    private final int width = 800;
    private final int height = 600;
    private final GazeBroker broker;
    private final GazeGUI gui;

    DisplayEye() {
        broker = new GazeBroker("localhost", 5000, true);
        gui = new GazeGUI(width, height);
    }

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
