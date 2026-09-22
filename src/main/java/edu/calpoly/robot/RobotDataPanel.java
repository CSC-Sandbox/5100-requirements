package edu.calpoly.robot;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Date;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class RobotDataPanel extends JPanel {
    private final JLabel[] jointLabels;
    private final JLabel posLabel = new JLabel();
    private final JLabel lastUpdatedLabel = new JLabel();
    private final static Color TITLE_COLOR = Color.white;
    private final static Color TEXT_COLOR = Color.lightGray;
  
    public RobotDataPanel(int jointCount) {
        // Setup data panel.
        jointLabels = new JLabel[jointCount];
        setBackground(new Color(20, 20, 20));
        setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(400, 0)); // width hint for BorderLayout.WEST
        Font mono = new Font(Font.MONOSPACED, Font.PLAIN, 30);

        // Joint Angle Header.
        add(makeLabel("Joint Angles:", TITLE_COLOR, mono));
        add(makeLabel("(Radians)", TEXT_COLOR, mono));
        // Individual Angle Values.
        for (int i = 0; i < jointLabels.length; i++) {
            jointLabels[i] = makeLabel(String.format("J%d:   ---", i + 1), TEXT_COLOR, mono);
            add(jointLabels[i]);
        }
        // Position Header
        add(Box.createVerticalStrut(10));
        add(makeLabel("Position:", TITLE_COLOR, mono));
        add(makeLabel("(Not Displayed)", TEXT_COLOR, mono));
        // Position values
        posLabel.setForeground(TEXT_COLOR);
        posLabel.setFont(mono);
        posLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(posLabel);
        // Last Updated Header
        add(Box.createVerticalStrut(10));
        add(makeLabel("Last Updated:", TITLE_COLOR, mono));
        // Last Updated values
        lastUpdatedLabel.setForeground(TEXT_COLOR);
        lastUpdatedLabel.setFont(mono);
        lastUpdatedLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(lastUpdatedLabel);
    }

    // helper function.
    private JLabel makeLabel(String text, Color color, Font font) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(font);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    // Updates the displayed information.
    public void update(RobotMessage data, Date lastUpdated) {
        for (int i = 0; i < jointLabels.length && i < data.jointAngles.length; i++) {
            jointLabels[i].setText(String.format("J%d: % .3f", i + 1, data.jointAngles[i]));
        }
        posLabel.setText(String.format(
            "<html>x=% .3f<br>y=% .3f<br>z=% .3f</html>",
            data.position[0], data.position[1], data.position[2]));
        lastUpdatedLabel.setText(new SimpleDateFormat("hh:mm:ss").format(lastUpdated));
    }
    
}
