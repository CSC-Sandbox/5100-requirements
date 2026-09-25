package edu.calpoly.eye;

import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import edu.calpoly.provided.Broker;

/**
 * Simulates an eye-tracking device using a Java Swing interface.
 * Users click on the simulated screen to determine the current gaze
 * position, which is normalized and published through the communication broker.
 *
 * @author Briggs
 * @version September 25, 2026
 */
public class GatherEye {

    /**
     * Starts the simulated eye-tracking application and publishes gaze data
     * whenever the user clicks on the screen.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        Broker broker = new Broker("localhost", 5000);
        JFrame frame = new JFrame("Gather Eye");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel screen = new JPanel();
        JLabel gazeLabel = new JLabel("Gaze X: 0.00, Gaze Y: 0.00");

        screen.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                double normalizedX = (double) x / screen.getWidth();
                double normalizedY = (double) y / screen.getHeight();
                String message = "GAZE," + normalizedX + "," + normalizedY;
                broker.send(message);
                gazeLabel.setText("Gaze X: " + normalizedX + ", Gaze Y: " + normalizedY);
                System.out.println("X: " + normalizedX + ", Y: " + normalizedY);
            }
        });

        frame.add(screen);
        frame.add(gazeLabel, "South");
        frame.setVisible(true);
    }
}