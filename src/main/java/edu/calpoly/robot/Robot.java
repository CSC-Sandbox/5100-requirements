package edu.calpoly.robot;

import java.awt.BorderLayout;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Robot {
    public static final RobotMessage DEFAULT_DATA = new RobotMessage("ROBOT,2.00,1.57,1.0,0.5,0.25,0.0,-0.25,-0.5,-0.75");
    // Data in the class
    private RobotMessage data;
    private Date lastUpdated;
    // Rendering Robot
    private final RobotDisplay robotDisplay;
    // Rendering Data
    private final RobotDataPanel robotDataPanel;
    // Container for the data and Robot.
    private final JPanel container;


    // Default constructor.
    Robot(){
        this(DEFAULT_DATA, new RobotDisplay());
    };

    // Set imageResolution for the displayed robot.
    // Note: Performance can be an issue with higher resolutions ex: 2000x2000
    Robot( int imageResolutionWidth, int imageResolutionHeight){
        this(DEFAULT_DATA,new RobotDisplay(imageResolutionWidth, imageResolutionHeight));
    };

    // Helper private contructor 
    private Robot(RobotMessage initData, RobotDisplay display){
        data = initData;
        lastUpdated = new Date();
        robotDisplay = display;
        robotDataPanel = new RobotDataPanel(initData.jointAngles.length);
        robotDataPanel.setVisible(false); //default to no data panel.

        container = new JPanel(new BorderLayout());
        container.add(robotDataPanel, BorderLayout.WEST);
        container.add(robotDisplay, BorderLayout.CENTER);
    }

    // Updates the data within the Robot and updates the displayed information.
    public void update(RobotMessage newData){
        if (newData != null){
            // Save a deep copy to prevent unpredictable modification.
            data = newData.clone();
            lastUpdated = new Date(); 
        }
        robotDisplay.update(data);
        robotDataPanel.update(data, lastUpdated);
    }

    // Adds the robot and data to a frame.
    public void addToFrame(JFrame frame) {
        frame.add(container);
        this.update(null);
    }

    // DataPanel is off by default, but can be turned on.
    public void showData(boolean show) {
        robotDataPanel.setVisible(show);
        container.revalidate();
        container.repaint();
    }
}
