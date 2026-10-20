package edu.calpoly.robot;

import javax.swing.JFrame;

import edu.calpoly.provided.Broker;

/**
 * DisplayRobot is a testclass for RobotGUI and it's related 
 * classes including RobotBlackBoard and RobotMessage.
 * 
 * @author Paul Motter (PaulMotter)
 * @version 1.0.0 (9/24/2026)
 */
public class DisplayRobot {
    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    public static void main(String[] args) {
        // Broker for getting data
        Broker broker = new Broker(HOST,PORT);

        // Frame to put the robot in.
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setVisible(true);

        // Create Robot.
        RobotGUI robotGUI = new RobotGUI();
        robotGUI.showData(true);
        robotGUI.showRobot(true, 1000, 1000);
        // Add Robot to Frame
        robotGUI.addToFrame(frame);
        
        // Blackboard to post updates to
        RobotBlackBoard rbb = new RobotBlackBoard();
        rbb.callbackOnPost(robotGUI::update);

        while (true){
            String message = broker.receive();
            RobotMessage rm = new RobotMessage(message);
            rbb.post(rm);
        }
    }
}
