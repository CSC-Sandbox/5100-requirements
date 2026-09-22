package edu.calpoly.EnterMessage;

import edu.calpoly.provided.Broker;

import javax.swing.*;
import java.awt.*;

public class EnterMessages extends JFrame {
    private final JTextField textField;
    private final Broker broker;

    EnterMessages(Broker broker) {

        super("Send Messages");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        this.broker = broker;

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(Box.createVerticalGlue());

        textField = new JTextField(60);
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        textField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton button = createSendButton();

        panel.add(textField);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(button);

        panel.add(Box.createVerticalGlue());

        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }

    private JButton createSendButton() {
        JButton button = new JButton("Send");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        button.addActionListener(e -> {
            String text = textField.getText();

            if (text.isBlank()) {
                return;
            }

            try {
                this.broker.send(text);
                textField.setText("");
            } catch (IllegalStateException stateException) {
                System.out.println("Not connected to Server, please start server.");
            }
        });
        return button;
    }

    public static void main(String[] args) {
        var broker = new Broker("localhost", 5000);
        var messageButton = new EnterMessages(broker);

    }


}
