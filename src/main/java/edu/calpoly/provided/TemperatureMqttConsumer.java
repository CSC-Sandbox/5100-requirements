package edu.calpoly.provided;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

/**
 * MQTT consumer/subscriber for the temperature example.
 *
 * Run this class first, then run TemperatureMqttProvider.
 *
 * @author Javier Gonzalez-Sanchez
 * @version 1.0 (2026-09-25)
 */
public class TemperatureMqttConsumer {

    private final ObjectMapper mapper = new ObjectMapper();

    public void start() throws Exception {
        MqttClient client =
                new MqttClient(TemperatureMqttProvider.BROKER, MqttClient.generateClientId());

        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        client.connect(options);

        client.subscribe(TemperatureMqttProvider.TOPIC, (topic, message) -> {
            try {
                Temperature temperature =
                        mapper.readValue(message.getPayload(), Temperature.class);
                System.out.println("Received from " + topic + ": " + temperature);
            } catch (Exception e) {
                System.err.println("Invalid temperature message: " + e.getMessage());
            }
        });

        System.out.println("Subscribed to " + TemperatureMqttProvider.TOPIC);
        System.out.println("Waiting for temperature data. Press Ctrl+C to stop.");
    }

    public static void main(String[] args) throws Exception {
        new TemperatureMqttConsumer().start();
    }
}
