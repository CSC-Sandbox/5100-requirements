package edu.calpoly.message;

import edu.calpoly.provided.Broker;

import javax.swing.*;
import java.awt.*;

/**
 * A GUI component that prompts the user for a message to send over a given broker.
 *
 * @author David Montiel
 * @version 1.0.0
 * @implSpec Requires an active {@link Broker} connection; however, server-side listener is not required to be active.
 * In the event of a server disconnection, this component notifies the user that the server must be restarted.
 * <h3>Code Example</h3>
 * <pre> {@code
 * var broker = new Broker("localhost", 5000);
 * new EnterMessages(broker);
 * }
 * </pre>
 */
public class EnterMessages extends JDialog {

    private final JTextField textField;
    private final Broker broker;

    /**
     * Instantiates a new {@code EnterMessages} can be added to Button ActionListener.
     * @apiNote When sending a message if the message is blank (Only white space) or empty it will not send the message over
     * the given broker.
     * @param broker The broker over which messages will be sent. Must not be {@code null}.
     */
    public EnterMessages(Broker broker) {

        setLocationRelativeTo(null);
        setSize(400, 200);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
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

    public static void main(String[] args) {
        var broker = new Broker("localhost", 5000);
        new EnterMessages(broker);

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
}