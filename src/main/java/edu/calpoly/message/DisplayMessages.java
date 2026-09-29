package edu.calpoly.message;

import edu.calpoly.provided.Broker;

import java.awt.*;
import java.time.LocalTime;
import java.util.function.Function;
import javax.swing.*;

/**
 * A GUI component that that displays a scrollable log of Messages.
 *
 * @apiNote This component is Thread-safe. External Threads can safely push Messages to the UI via {@link #appendLog(Formatter, String)},
 * which Will delegate UI updates to Swing Event Dispatch Thead.
 * @implSpec This UI component does not actively fetch Network Messages on its own. Instead, this implementation relies on external
 * controller (I.E Listener Classes)  to fetch incoming data and push it to the UI.
 *
 * <h3>Code Example</h3>
 * <pre>{@code
 * var messageBroker = new Broker("localhost", 8080);
 * var messageDisplay new MessageDisplay(messageBroker);
 *
 * var frame = new JFrame("Message Viewer");
 * frame.add(messageDisplay);
 * }</pre>
 * @see Broker
 * @author David Montiel
 * @version 2.0.0
 */
public class DisplayMessages extends JComponent {

    private final JTextArea logArea;

    public DisplayMessages() {
        setLayout(new BorderLayout());
        logArea = new JTextArea(15, 40);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(logArea);
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
            display.appendLog(Formatter::formatWithTimeStamp, msg);
        }
    }

    // A default formatter only to be used temporarily.

    /**
     * Appends a message to the text area.
     * <p>
     * This method is designed to be called by an external worker thread or polling loop. It will
     * apply a given Formater to a Message and delegate updating UI to Swing Event Dispatch Thread.
     *
     * @param formatter a function that transforms the raw message into its final
     *                  display format (e.g., adding timestamps or prefixes)
     * @param msg       A Given message from a Broker class's receive method.
     */
    public void appendLog(Formatter formatter, String msg) {
        SwingUtilities.invokeLater(() ->
                logArea.append(formatter.format(msg))
        );

    }

}
