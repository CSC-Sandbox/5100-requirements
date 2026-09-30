package edu.calpoly.monitor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * MQTT provider for the current data-source availability state.
 *
 * <p>The provider publishes availability information from the existing
 * {@link MonitorAvailability} domain logic. It does not calculate availability
 * independently.</p>
 *
 * @author Adrian Valenzuela (adrian0427)
 * @version September 29, 2026
 */
public class AvailabilityMqttProvider {

    public static final String BROKER =
            "tcp://broker.hivemq.com:1883";

    public static final String TOPIC =
            "csc5100/availability/status";

    private static final int QOS = 0;
    private static final long CHECK_INTERVAL_MS = 100;

    private final MonitorAvailability monitor;
    private final ObjectMapper mapper = new ObjectMapper();

    private MqttClient client;
    private Map<DataSource, Boolean> lastPublishedState;

    /**
     * Creates an MQTT provider backed by the shared availability monitor.
     *
     * @param monitor the monitor that provides current availability state
     */
    public AvailabilityMqttProvider(
            MonitorAvailability monitor) {

        this.monitor = monitor;
    }

    /**
     * Connects to the MQTT broker and begins publishing availability updates.
     *
     * <p>The complete availability state is published once when the provider
     * starts and again whenever the monitored availability state changes.</p>
     *
     * @throws Exception if the MQTT connection or publication fails
     */
    public void start() throws Exception {
        client = new MqttClient(
                BROKER,
                MqttClient.generateClientId()
        );

        MqttConnectOptions options =
                new MqttConnectOptions();

        options.setCleanSession(true);

        client.connect(options);

        System.out.println(
                "MQTT provider connected to " + BROKER
        );

        publishCurrentState();

        while (client.isConnected()) {
            Map<DataSource, Boolean> currentState =
                    monitor.getAvailabilitySnapshot();

            if (!currentState.equals(lastPublishedState)) {
                publishCurrentState();
            }

            Thread.sleep(CHECK_INTERVAL_MS);
        }
    }

    private void publishCurrentState() throws Exception {
        Map<DataSource, Boolean> snapshot =
                monitor.getAvailabilitySnapshot();

        Map<String, String> response =
                createAvailabilityResponse(snapshot);

        String json =
                mapper.writeValueAsString(response);

        MqttMessage message =
                new MqttMessage(
                        json.getBytes(StandardCharsets.UTF_8)
                );

        message.setQos(QOS);

        client.publish(TOPIC, message);

        lastPublishedState =
                Map.copyOf(snapshot);

        System.out.println(
                "Published to " + TOPIC + ": " + json
        );
    }

    private Map<String, String> createAvailabilityResponse(
            Map<DataSource, Boolean> snapshot) {

        Map<String, String> response =
                new LinkedHashMap<>();

        for (DataSource source : DataSource.values()) {
            boolean available =
                    snapshot.getOrDefault(source, false);

            response.put(
                    source.name().toLowerCase(),
                    available
                            ? "AVAILABLE"
                            : "UNAVAILABLE"
            );
        }

        return response;
    }

    /**
     * Disconnects the MQTT provider from the broker.
     *
     * @throws Exception if the MQTT client cannot disconnect cleanly
     */
    public void stop() throws Exception {
        if (client != null && client.isConnected()) {
            client.disconnect();
        }

        if (client != null) {
            client.close();
        }
    }

    /**
     * Starts the existing availability monitor and exposes its state through
     * MQTT.
     *
     * @param args command-line arguments; not used
     * @throws Exception if the MQTT provider cannot start
     */
    public static void main(String[] args)
            throws Exception {

        MonitorAvailability monitor =
                new MonitorAvailability();

        monitor.startReceiving();

        AvailabilityMqttProvider provider =
                new AvailabilityMqttProvider(monitor);

        provider.start();
    }
}