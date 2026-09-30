package edu.calpoly.eye;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * MQTT provider for the current gaze position.
 */
public class GazeMqttProvider {

    public static final String BROKER = "tcp://broker.hivemq.com:1883";
    public static final String TOPIC = "csc5100/gaze/current";

    private final GazeService service;
    private final ObjectMapper mapper = new ObjectMapper();
    private MqttClient client;

    public GazeMqttProvider(GazeService service) {
        this.service = service;
    }

    public void connect() throws Exception {
        client = new MqttClient(BROKER, MqttClient.generateClientId());

        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);

        client.connect(options);
    }

    public void publish() throws Exception {
        String json = mapper.writeValueAsString(service.getGazePoint());

        MqttMessage message = new MqttMessage(json.getBytes());
        message.setQos(0);

        client.publish(TOPIC, message);

        System.out.println("Published to " + TOPIC + ": " + json);
    }

    public void disconnect() throws Exception {
        if (client != null && client.isConnected()) {
            client.disconnect();
        }

        if (client != null) {
            client.close();
        }
    }
}