package edu.calpoly.robot;

import java.util.function.Consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

/**
 * Subscribes to robot data through MQTT and updates the shared blackboard.
 *
 * @author Paul Motter (PaulMotter)
 * @author Jess A (0x10jalencas)
 * @version 1.1.0 (9/28/2026)
 */
public class RobotMQTTAdapter extends Thread {
    public static final String TOPIC = "robot/1";

    private final String brokerURI;
    private final Consumer<RobotMessage> onSub;
    private final ObjectMapper mapper;
    private MqttClient client;

    /**
     * Creates the MQTT subscriber for robot data.
     *
     * @param brokerHost MQTT broker host
     * @param brokerPort MQTT broker port
     * @param rbb shared robot blackboard
     */
    RobotMQTTAdapter(
            String brokerHost,
            int brokerPort,
            RobotBlackBoard rbb) {
        brokerURI = "tcp://" + brokerHost + ":" + brokerPort;
        onSub = rbb::post;
        mapper = new ObjectMapper();
    }

    /**
     * Connects to the MQTT broker and
     * then subscribes to the robot topic.
     */
    @Override
    public void run() {
        try {
            client = new MqttClient(
                    brokerURI,
                    MqttClient.generateClientId());

            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setAutomaticReconnect(true);

            client.connect(options);
            client.subscribe(TOPIC, (topic, message) -> {
                try {
                    RobotMessage robotMessage = mapper.readValue(
                            message.getPayload(),
                            RobotMessage.class);
                    onSub.accept(robotMessage);
                } catch (Exception e) {
                    System.err.println(
                            "Invalid robot message: " + e.getMessage());
                }
            });

            System.out.println("Subscribed to " + TOPIC);
        } catch (Exception e) {
            System.err.println(
                    "Unable to start MQTT subscriber: " + e.getMessage());
        }
    }
}