package edu.calpoly.eye;

import javax.swing.*;
import java.awt.*;

public class GazeInfo extends JPanel{

    public boolean conStatus;
    public final GazePoint gazePoint;
    public final JLabel xLabel;
    public final JLabel yLabel;
    public final JLabel statusText;

    GazeInfo() {
        super();

        // Initialize variables
        gazePoint = new GazePoint(0.5, 0.5);
        Font font = UIManager.getFont("Label.font").deriveFont(16f);
        conStatus = true;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        // Current Position Title
        JLabel posTitle = new JLabel("Current Gaze Position");
        posTitle.setFont(font.deriveFont(Font.BOLD));
        add(posTitle, gbc);
        gbc.gridy++;

        // Initialize coordinate labels
        xLabel = new JLabel("X:   0.5");
        xLabel.setFont(font);
        add(xLabel, gbc);
        gbc.gridy++;

        yLabel = new JLabel("Y:   0.5");
        yLabel.setFont(font);
        add(yLabel, gbc);
        gbc.gridy++;

        // Separator for new section
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(12, 0, 12, 0);
        JPanel line = new JPanel();
        line.setBackground(Color.BLACK);
        line.setPreferredSize(new Dimension(1, 1));
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        add(line, gbc);
        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 0, 0, 0);

        // Connection status title
        JLabel conTitle = new JLabel("Connection Status");
        conTitle.setFont(font.deriveFont(Font.BOLD));
        add(conTitle, gbc);
        gbc.gridy++;

        // Connection status text
        statusText = new JLabel("Not Connected");
        statusText.setFont(font);
        statusText.setBackground(Color.RED);
        statusText.setOpaque(true);
        add(statusText, gbc);
        gbc.gridy++;

        // Separator for new section
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(12, 0, 12, 0);
        JPanel line2 = new JPanel();
        line2.setBackground(Color.BLACK);
        line2.setPreferredSize(new Dimension(1, 1));
        line2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        add(line2, gbc);
        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 0, 0, 0);

        // Instruction Title
        JLabel helpTitle = new JLabel("First Time Instructions");
        helpTitle.setFont(font.deriveFont(Font.BOLD));
        add(helpTitle, gbc);
        gbc.gridy++;

        // List of instructions
        JLabel step1 = new JLabel("Run TestDisplayEye.java");
        step1.setFont(font);
        add(step1, gbc);
        gbc.gridy++;

        JLabel step2 = new JLabel("Close and restart this");
        step2.setFont(font);
        add(step2, gbc);
        gbc.gridy++;

        JLabel step3 = new JLabel("(DisplayEye.java)");
        step3.setFont(font);
        add(step3, gbc);
        gbc.gridy++;

        JLabel step4 = new JLabel("Observe the gaze point");
        step4.setFont(font);
        add(step4, gbc);
        gbc.gridy++;

        JLabel step5 = new JLabel("moving across the screen");
        step5.setFont(font);
        add(step5, gbc);
        gbc.gridy++;

        JLabel step6 = new JLabel( "in a back-and-forth");
        step6.setFont(font);
        add(step6, gbc);
        gbc.gridy++;

        JLabel step7 = new JLabel( "pattern.");
        step7.setFont(font);
        add(step7, gbc);
        gbc.gridy++;
    }

    public void setGazePoint(GazePoint gazePoint) {
        this.gazePoint.setXY(gazePoint.x, gazePoint.y);

        // Update position labels
        xLabel.setText("X:   " + this.gazePoint.x);
        yLabel.setText("Y:   " + this.gazePoint.y);

        // Update connection status label
        if (conStatus) {
            statusText.setText("Connected to Broker");
            statusText.setBackground(Color.GREEN);
        } else {
            statusText.setText("Not Connected");
            statusText.setBackground(Color.RED);
        }
    }
}
