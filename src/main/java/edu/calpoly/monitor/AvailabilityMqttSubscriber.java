package edu.calpoly.monitor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * MQTT Subscriber for MonitorAvailability.
 * This class retrieves status information from the MQTT Provider
 *
 * @author Howard Jiang (hwrd22)
 * @version 1.0 (September 29, 2026)
 */
public class AvailabilityMqttSubscriber {
    public static final String BROKER = "tcp://broker.hivemq.com:1883";
    public static final String TOPIC = "csc5100/availability/status";

    // Lambda function that decides what to do when a message is received.
    private final Consumer<Map<DataSource, Boolean>> onUpdate;
    private final ObjectMapper mapper = new ObjectMapper();

    public AvailabilityMqttSubscriber(Consumer<Map<DataSource, Boolean>> onUpdate) {
        this.onUpdate = onUpdate;
    }

    /**
     *
     * @param json The JSON retrieved from the MQTT provider
     * @return A map of each Data Source and their availability (as a boolean)
     * @throws Exception If JSON cannot be parsed correctly
     */
    private Map<DataSource, Boolean> parseAvailability(
            String json) throws Exception {

        Map<String, String> values = mapper.readValue(
                json,
                new TypeReference<Map<String, String>>() { });

        Map<DataSource, Boolean> availability =
                new EnumMap<>(DataSource.class);

        availability.put(
                DataSource.ROBOT,
                "AVAILABLE".equals(values.get("robot")));
        availability.put(
                DataSource.GAZE,
                "AVAILABLE".equals(values.get("gaze")));
        availability.put(
                DataSource.AFFECT,
                "AVAILABLE".equals(values.get("affect")));
        availability.put(
                DataSource.LIDAR,
                "AVAILABLE".equals(values.get("lidar")));

        return availability;
    }

    /**
     * Method to subscribe to the MQTT Provider for MonitorAvailability
     * @throws Exception when a message from the provider is invalid or cannot be parsed
     */
    public void subscribe() throws Exception {
        MqttClient client = new MqttClient(BROKER, MqttClient.generateClientId());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);

        client.connect(options);

        IMqttMessageListener listener = (String topic, MqttMessage message) -> {
            try {
                String json = new String(
                        message.getPayload(),
                        StandardCharsets.UTF_8
                );

                Map<DataSource, Boolean> status = parseAvailability(json);

                onUpdate.accept(status);
            } catch (Exception e) {
                System.err.println("Invalid availability MQTT message: "
                        + e.getMessage());
            }
        };


        client.subscribe(TOPIC, listener);
        System.out.println("Listening to " + TOPIC);
    }

    public static void main(String[] args) throws Exception {
        // Small loop to output messages from the MQTT publisher (Demonstration purposes)
        AvailabilityMqttSubscriber subscriber = new AvailabilityMqttSubscriber(snapshot -> {
            System.out.println("Availability:");
            for (DataSource source: DataSource.values()) {
                System.out.println(
                        source + ": " + (snapshot.get(source) ? "AVAILABLE" : "UNAVAILABLE")
                );
            }
        });

        subscriber.subscribe();
    }
}
