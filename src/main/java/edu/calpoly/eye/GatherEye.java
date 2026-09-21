package edu.calpoly.eye;

import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GatherEye {
    public static void main(String[] args) {
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
                gazeLabel.setText("Gaze X: " + normalizedX + ", Gaze Y: " + normalizedY);
                System.out.println("X: " + normalizedX + ", Y: " + normalizedY);
            }
        });

        frame.add(screen);
        frame.add(gazeLabel, "South");
        frame.setVisible(true);
    }
}