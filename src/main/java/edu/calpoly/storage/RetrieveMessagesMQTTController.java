package edu.calpoly.storage;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class RetrieveMessagesMQTTController {
    private static final String broker = "tcp://test.messages.org:5873";
    private static final String topic = "messages";
    private final MqttClient client;

    public RetrieveMessagesMQTTController(String clientId) throws MqttException {
        this.client = new MqttClient(broker, clientId);
    }

    public void publishMessage(String message) {
        try {
            client.connect();
            var mqttMessage = new MqttMessage(message.getBytes());

            mqttMessage.setQos(2);
            client.publish(topic, mqttMessage);
        } catch (MqttException e) {
            System.err.println("Could not connect to client.");
        }
    }

}
