package edu.calpoly.robot;

import java.nio.charset.StandardCharsets;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Publishes sample JSON robot data to test RobotMQTTAdapter.
 *
 * @author Paul Motter (PaulMotter)
 * @author Jess A (0x10jalencas)
 * @version 1.1.0 (9/28/2026)
 */
public class TestRobotMQTTAdapter {
    private static final String BROKER =
            "tcp://broker.hivemq.com:1883";

    private static final String VALID_MESSAGE =
            "{\"jointAngles\":[2.0,1.75,1.0,0.5,0.25,0.0],"
            + "\"position\":[0.5,-10.0,5.25]}";

    private static final String INVALID_MESSAGE =
            "{\"jointAngles\":[1.0,2.0],\"position\":[0.0]}";

    /**
     * Publishes valid and invalid robot messages.
     *
     * @param args command-line arguments; unused
     * @throws Exception if MQTT communication fails
     */
    public static void main(String[] args) throws Exception {
        MqttClient client = new MqttClient(
                BROKER,
                MqttClient.generateClientId());

        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        client.connect(options);    

        publish(client, VALID_MESSAGE);
        Thread.sleep(1000);
        publish(client, INVALID_MESSAGE);

        client.disconnect();
        client.close();
    }

    private static void publish(
            MqttClient client,
            String payload) throws Exception {
        MqttMessage message = new MqttMessage(
                payload.getBytes(StandardCharsets.UTF_8));
        message.setQos(0);

        client.publish(RobotMQTTAdapter.TOPIC, message);
        System.out.println(
                "Published to "
                + RobotMQTTAdapter.TOPIC
                + ": "
                + payload);
    }
}