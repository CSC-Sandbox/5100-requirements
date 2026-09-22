package edu.calpoly.gui;

import edu.calpoly.provided.Broker;

import java.awt.*;
import java.time.LocalTime;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import javax.swing.*;

public class DisplayMessages extends JComponent{
    private final JScrollPane scrollPane;
    private final JTextArea logArea;

    public DisplayMessages() {
        setLayout(new BorderLayout());
        logArea = new JTextArea(15, 40);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setEditable(false);

        scrollPane = new JScrollPane(logArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        var display = new DisplayMessages();
        var broker = new Broker("localhost", 5000);
        SwingUtilities.invokeLater(() -> {
            var frame = new JFrame("Logs");

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 600);
            frame.setLocationRelativeTo(null);

            frame.add(display);
            frame.setVisible(true);
        });

        while (true) {
            var msg = broker.receive();
            display.appendLog(msg);
        }
    }

    public void appendLog(String msg) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(formatMessage(msg));
        });

    }

    private String formatMessage(String message) {
        var time = LocalTime.now();

        return String.format("[%tT] %s %n", time, message );

    }

}
