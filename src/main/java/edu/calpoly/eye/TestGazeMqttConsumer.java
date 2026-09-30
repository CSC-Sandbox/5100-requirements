package edu.calpoly.eye;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class TestGazeMqttConsumer {
    private static final String BROKER = "tcp://broker.hivemq.com:1883";
    private static final String TOPIC = "csc5100/gaze/current";

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
