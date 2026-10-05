package edu.calpoly.storage;

import org.eclipse.paho.client.mqttv3.*;

import java.time.LocalDateTime;
import java.util.Arrays;

public class RetrieveMessagesMQTTSubscriber implements MqttCallback {
    private static final String broker = "tcp://test.messages.org:5873";
    private static final String topic = "csc5100/messages";
    private static final String clientId = "message-retriever";

    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("Connection Lost");
    }

    public static void main(String[] args) {
        try {
            var client = new MqttClient(broker, clientId);
            client.setCallback(new RetrieveMessagesMQTTSubscriber());
            client.connect();

            System.out.println("Client connected");
            client.subscribe(topic);

            System.out.println("Subscribed to topic" + topic);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        var payload = Arrays.toString(message.getPayload());
        var timestamp = LocalDateTime.now().toString();

        System.out.println(timestamp + "," + payload);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        System.out.println("Received a Message");
    }
}
