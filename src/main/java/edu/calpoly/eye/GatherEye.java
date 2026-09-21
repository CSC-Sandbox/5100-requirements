package edu.calpoly.eye;

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

        screen.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                System.out.println("X: " + x + ", Y: " + y);
            }
        });

        frame.add(screen);

        frame.setVisible(true);
    }
}