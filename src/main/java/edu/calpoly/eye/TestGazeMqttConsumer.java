package edu.calpoly.eye;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Class to test the MQTT Consumer
 * Run this file directly to start sending test MQTT messages
 *
 * @author James Yaguma
 * @version 1.0 (2026-09-30)
 */
public class TestGazeMqttConsumer {
    private static final String BROKER = "tcp://broker.hivemq.com:1883";
    private static final String TOPIC = "csc5100/gaze/current";

    /**
     * Main method
     * Run to start sending MQTT test messages
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            MqttClient client = new MqttClient(BROKER, MqttClient.generateClientId());

            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);

            client.connect(options);

            loopTestMessages(client);
        } catch (Exception e) {
            System.out.println("Test encountered an error: " + e.getMessage());
        }
    }

    /**
     * Start the process of sending test messages forever
     * Busy waits, so current thread can't do anything else after
     *
     * @param client The client to send MQTT messages with
     */
    public static void loopTestMessages(MqttClient client) {
        TestGazeMessages messages = new TestGazeMessages();
        while(!Thread.currentThread().isInterrupted()) {
            try {
                MqttMessage message = new MqttMessage(messages.getNextBytes());
                message.setQos(0);

                client.publish(TOPIC, message);

                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.out.println("MQTT Consumer test encountered exception: " + e.getMessage());
            }
        }
    }
}
