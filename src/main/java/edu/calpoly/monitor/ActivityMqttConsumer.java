package edu.calpoly.monitor;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Obtains current data-activity information by subscribing to the paired
 * provider's MQTT activity topic. The provider publishes retained messages,
 * so a snapshot is normally available immediately after subscribing rather
 * than only after the next scheduled publish.
 *
 *
 * @author Aiden Rodriguez
 * @version September 27, 2026
 */
public final class ActivityMqttConsumer implements ActivitySnapshotSource {
    public static final String DEFAULT_BROKER = ActivityMqttProvider.DEFAULT_BROKER;
    public static final String DEFAULT_TOPIC = ActivityMqttProvider.DEFAULT_TOPIC;
    private static final int QOS = ActivityMqttProvider.QOS;

    private final String brokerUri;
    private final String topic;
    private final ObjectMapper mapper = new ObjectMapper();

    private final AtomicReference<ActivitySnapshot> lastSnapshot = new AtomicReference<>();
    private volatile String status = "Not yet connected";
    private MqttClient client;

    /**
     * Creates an MQTT consumer for the given broker and topic.
     *
     * @param brokerUri MQTT broker URI, e.g. {@code tcp://broker.hivemq.com:1883}
     * @param topic activity topic to subscribe to, agreed with the provider
     */
    public ActivityMqttConsumer(String brokerUri, String topic) {
        this.brokerUri = requireText(brokerUri, "brokerUri");
        this.topic = requireText(topic, "topic");
    }

    /**
     * Connects to the broker and subscribes to the activity topic.
     */
    @Override
    public synchronized void start() {
        if (client != null) {
            return;
        }

        try {
            MqttClient newClient = new MqttClient(brokerUri, MqttClient.generateClientId());
            newClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    status = "Disconnected from " + brokerUri + ": " + cause.getMessage();
                }

                @Override
                public void messageArrived(String receivedTopic, MqttMessage message) {
                    handleMessage(message);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                }
            });

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);

            status = "Connecting to " + brokerUri + "...";
            newClient.connect(options);
            newClient.subscribe(topic, QOS);
            client = newClient;
            status = "Subscribed to " + topic;
        } catch (MqttException exception) {
            status = "Unable to connect to " + brokerUri + ": " + exception.getMessage();
        }
    }

    private void handleMessage(MqttMessage message) {
        try {
            String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
            ActivitySnapshot snapshot = mapper.readValue(payload, ActivitySnapshot.class);
            lastSnapshot.set(snapshot);
            status = "Receiving data from " + topic;
        } catch (JsonProcessingException | IllegalArgumentException exception) {
            // Keep the last good snapshot on screen; surface the problem rather than crashing.
            status = "Received malformed activity message: " + exception.getMessage();
        }
    }

    @Override
    public Optional<ActivitySnapshot> latestSnapshot() {
        return Optional.ofNullable(lastSnapshot.get());
    }

    @Override
    public String status() {
        return status;
    }

    private static String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " is required");
        }
        return value;
    }

    /**
     * Unsubscribes and disconnects from the broker.
     */
    @Override
    public synchronized void close() {
        if (client != null) {
            try {
                if (client.isConnected()) {
                    client.disconnect();
                }
                client.close();
            } catch (MqttException exception) {
                System.err.println("Unable to close MQTT consumer: " + exception.getMessage());
            } finally {
                client = null;
            }
        }
    }
}
