package edu.calpoly.robot;

import javax.swing.JFrame;

import edu.calpoly.provided.Broker;

public class DisplayRobot {
    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    public static void main(String[] args) {
        // Broker for getting data
        Broker broker = new Broker(HOST,PORT);

        // frame to put the robot in.
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setVisible(true);

        // Create Robot.
        Robot robot = new Robot(1000, 1000);
        // Add Robot to Frame
        robot.addToFrame(frame);
        // Toggle showing data for the robot.
        robot.showData(true);
        
        while (true){
            String message = broker.receive();
            RobotMessage rm = new RobotMessage(message);
            // Update Robot with message.
            robot.update(rm);
        }
    }
}
