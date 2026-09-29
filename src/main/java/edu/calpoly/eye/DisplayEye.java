package edu.calpoly.eye;

import javax.swing.*;
import java.util.Locale;

/**
 * Main file to run for the visualization
 * Also initializes the GUI frame
 *
 * @author James Yaguma
 * @version 1.1 (2026-09-29)
 */
public class DisplayEye {
    private final int width = 800;
    private final int height = 600;
    private final GazeGUI gui;

    /**
     * DisplayEye constructor
     * Initializes the GazeBroker and GUI objects
     */
    DisplayEye() {
        gui = new GazeGUI(width, height);
    }

    /**
     * Main method
     * Starts the application
     *
     * @param args Command line args (unused)
     */
    public static void main(String[] args) throws Exception{
        // Determine what interface to use based on args
        // b = GazeBroker, m = MQTTConsumer, r = RESTClient
        char interfaceMode = 'r';
        if (args.length > 1) {
            switch(args[0].toLowerCase()) {
                case "b":
                case "broker":
                case "gazebroker":
                    break;
                case "m":
                case "mqtt":
                case "mqttconsumer":
                case "gazemqttconsumer":
                    interfaceMode = 'm';
                    break;
                case "r":
                case "rest":
                case "restclient":
                case "gazerestclient":
                    interfaceMode = 'r';
                    break;
                default:
                    System.out.println("\"" + args[0] + "\" was not recognized as a valid interface mode. Defaulting to GazeBroker...");
            }
        }

        DisplayEye displayEye = new DisplayEye();

        // Initial frame setup
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(displayEye.width, displayEye.height);
        frame.setResizable(false);

        // Add the GUI to the frame before turning the frame to visible
        displayEye.gui.addToFrame(frame);
        frame.setVisible(true);

        // Add update callback to the GazeBlackboard
        GazeBlackboard blackboard = GazeBlackboard.getInstance();
        blackboard.addCallback(displayEye.gui::update);

        // Based on which interface to use, start consuming data
        switch(interfaceMode) {
            case 'm':
                GazeMqttConsumer consumer = new GazeMqttConsumer();
                if (args.length > 2) {
                    consumer.start(blackboard, args[1]);
                } else {
                    consumer.start();
                }
                break;
            case 'r':
                GazeRestClient client;
                if (args.length > 2) {
                    client = new GazeRestClient(blackboard, args[1]);
                } else {
                    client = new GazeRestClient();
                }

                Thread clientThread = new Thread(() -> {
                    try {
                        client.loopForever();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                clientThread.start();
                break;
            default:
                GazeBroker broker = new GazeBroker("localhost", 5000);
                broker.loopForever(blackboard);
        }
    }
}
