package edu.calpoly.eye;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GatherEye {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Gather Eye");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel screen = new JPanel();
        frame.add(screen);

        frame.setVisible(true);
    }
}