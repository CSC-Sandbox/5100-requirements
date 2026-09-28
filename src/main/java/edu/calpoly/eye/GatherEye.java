package edu.calpoly.eye;

import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import edu.calpoly.provided.Broker;

public class GatherEye {
    public static void main(String[] args) {
        Broker broker = new Broker("localhost", 5000);
        GazeService gazeService = new GazeService();
        GazeMqttProvider mqttProvider = new GazeMqttProvider(gazeService);
        try {
            mqttProvider.connect();
        } catch (Exception e) {
            System.out.println("Could not connect to MQTT broker.");
        }
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
                GazePoint gazePoint = new GazePoint(normalizedX, normalizedY);
                gazeService.setGazePoint(gazePoint);
                try {
                    mqttProvider.publish();
                } catch (Exception ex) {
                    System.out.println("Could not publish gaze data.");
                }
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