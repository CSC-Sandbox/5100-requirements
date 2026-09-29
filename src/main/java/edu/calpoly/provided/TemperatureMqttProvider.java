package edu.calpoly.provided;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * MQTT provider/publisher for the temperature example.
 *
 * This class publishes the same Temperature domain object used by REST.
 *
 * @author Javier Gonzalez-Sanchez
 * @version 1.0 (2026-09-25)
 */
public class TemperatureMqttProvider {

    public static final String BROKER = "tcp://broker.hivemq.com:1883";
    public static final String TOPIC = "csc5100/example/temperature";

    private final TemperatureService service;
    private final ObjectMapper mapper = new ObjectMapper();

    public TemperatureMqttProvider(TemperatureService service) {
        this.service = service;
    }

    public void publish() throws Exception {
        MqttClient client = new MqttClient(BROKER, MqttClient.generateClientId());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);

        client.connect(options);

        String json = mapper.writeValueAsString(service.getTemperature());
        MqttMessage message = new MqttMessage(json.getBytes());
        message.setQos(0);

        client.publish(TOPIC, message);
        System.out.println("Published to " + TOPIC + ": " + json);

        client.disconnect();
        client.close();
    }

    public static void main(String[] args) throws Exception {
        TemperatureService service = new TemperatureService();
        new TemperatureMqttProvider(service).publish();
    }
}
