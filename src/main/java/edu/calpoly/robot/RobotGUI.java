package edu.calpoly.robot;

import java.awt.BorderLayout;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * RobotGUI is a class that manages a container that can optionally 
 * display Robot data and/or the Robot Visulization and updates their GUIs. 
 * 
 * Typical usage is creating an instance, toggling showData and/or showRobot and then calling update.
 * @author Paul Motter (PaulMotter)
 * @version 1.0.0 (9/24/2026)
 */
public class RobotGUI {
    // makes sure there is at least 1/30s between rendering updates.
    private final long NANO_TIME_BETWEEN_UPDATES = 1_000_000_000/30;
    // measured in system time.
    private long lastUpdate;
    // Rendering Robot
    private RobotDisplayPanel robotDisplayPanel;
    // Rendering Data
    private RobotDataPanel robotDataPanel;
    // Container for the data and Robot.
    private final JPanel container;

    /**
     * Creates a robot with the container.
     */    
    public RobotGUI(){
        lastUpdate = System.nanoTime();
        container = new JPanel(new BorderLayout());
    }

    public void addToFrame(JFrame frame){
        frame.add(container);
    }

    /**
     * Updates the visible GUIs with newData.
     * Note that any update sooner than 1/30s since the last will not be processed. 
     * @param newData The RobotMessage to update the GUIs with.
     */
    public void update(RobotMessage newData){
        // Update time management.
        long thisUpdate = System.nanoTime();
        if (thisUpdate-lastUpdate < NANO_TIME_BETWEEN_UPDATES){
            return;
        }
        lastUpdate = thisUpdate;

        if (robotDataPanel != null){
            robotDataPanel.update(newData, new Date());
        }
        if (robotDisplayPanel != null){
            robotDisplayPanel.update(newData);
        }
    }

    /**
     * Toggles wether the RobotDataPanel is visible.
     * @param show Controls visibility.
     */
    public void showData(boolean show) {
        if (robotDataPanel == null){
            robotDataPanel = new RobotDataPanel();
        }
        robotDataPanel.setVisible(show);
        layoutPanels();
    }

    /**
     * when used on first showing the RobotDisplayPanel you can decide the resolution of the image.
     * @param show Determins visibility.
     * @param imageResolutionWidth Resolution of image width.
     * @param imageResolutionHeight Resolution of image height.
     */
    public void showRobot(boolean show, int imageResolutionWidth, int imageResolutionHeight){
        if (robotDisplayPanel == null){
            robotDisplayPanel = new RobotDisplayPanel(imageResolutionWidth, imageResolutionHeight);
        }
        robotDisplayPanel.setVisible(show);
        layoutPanels();
    }

    /**
     * Toggles wether the RobotDisplayPanel is visible.
     * @param show Controls visibility.
     */
    public void showRobot(boolean show){
        if (robotDisplayPanel == null){
            robotDisplayPanel = new RobotDisplayPanel();
        }
        robotDisplayPanel.setVisible(show);
        layoutPanels();
    }

    /**
     * Places the panels within the container based on which ones are present.
     * Makes sure whatever is present isn't restricted in size, but formats 
     * nicely when all components are present.
     */
    private void layoutPanels(){
        // Detach both panels so they can be re-placed cleanly.
        if (robotDataPanel != null){
            container.remove(robotDataPanel);
        }
        if (robotDisplayPanel != null){
            container.remove(robotDisplayPanel);
        }

        boolean robotDisplayVisible = robotDisplayPanel != null && robotDisplayPanel.isVisible();
        boolean robotDataVisible = robotDataPanel != null && robotDataPanel.isVisible();
        if (robotDataVisible){
            container.add(robotDataPanel, robotDisplayVisible ? BorderLayout.WEST : BorderLayout.CENTER);
        }
        if (robotDisplayVisible){
            container.add(robotDisplayPanel, BorderLayout.CENTER);
        }

        container.revalidate();
        container.repaint();
    }
}
