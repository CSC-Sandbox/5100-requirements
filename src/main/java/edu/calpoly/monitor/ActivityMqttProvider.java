package edu.calpoly.monitor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Publishes current data-activity snapshots through MQTT. Snapshots are sent
 * periodically because counts can decrease as messages leave the 60-second
 * window even when no new messages arrive.
 *
 * @author Dylan Gururajan
 * @version September 27, 2026
 */
public final class ActivityMqttProvider implements AutoCloseable {
    public static final String DEFAULT_BROKER = "tcp://broker.hivemq.com:1883";
    public static final String DEFAULT_TOPIC = "csc5100/activity/dylan/state";
    public static final Duration DEFAULT_PUBLISH_INTERVAL = Duration.ofSeconds(1);
    public static final int QOS = 1;

    private final ProvideDataActivity provider;
    private final String brokerUri;
    private final String topic;
    private final Duration publishInterval;
    private final ObjectMapper mapper = new ObjectMapper();
    private ScheduledExecutorService publisherExecutor;
    private MqttClient client;

    /**
     * Creates an MQTT activity provider.
     *
     * @param provider shared activity provider
     * @param brokerUri MQTT broker URI
     * @param topic topic on which activity state is published
     * @param publishInterval interval between activity snapshots
     */
    public ActivityMqttProvider(
            ProvideDataActivity provider,
            String brokerUri,
            String topic,
            Duration publishInterval) {
        this.provider = Objects.requireNonNull(provider, "provider");
        this.brokerUri = requireText(brokerUri, "brokerUri");
        this.topic = requireText(topic, "topic");
        this.publishInterval = Objects.requireNonNull(publishInterval, "publishInterval");
        if (publishInterval.isZero() || publishInterval.isNegative()) {
            throw new IllegalArgumentException("publishInterval must be positive");
        }
    }

    /**
     * Connects to the MQTT broker and begins periodic publication.
     *
     * @throws MqttException when the initial broker connection fails
     */
    public synchronized void start() throws MqttException {
        if (client != null) {
            return;
        }

        MqttClient newClient = new MqttClient(brokerUri, MqttClient.generateClientId());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        newClient.connect(options);
        client = newClient;

        publisherExecutor = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "activity-mqtt-publisher");
            thread.setDaemon(true);
            return thread;
        });
        publisherExecutor.scheduleAtFixedRate(
                this::publishSafely,
                0,
                publishInterval.toMillis(),
                TimeUnit.MILLISECONDS);
    }

    private void publishSafely() {
        try {
            publishSnapshot();
        } catch (MqttException | JsonProcessingException exception) {
            System.err.println("Unable to publish activity snapshot: "
                    + exception.getMessage());
        }
    }

    private void publishSnapshot() throws MqttException, JsonProcessingException {
        MqttClient currentClient = client;
        if (currentClient == null || !currentClient.isConnected()) {
            return;
        }

        MqttMessage message = new MqttMessage(
                createPayload().getBytes(StandardCharsets.UTF_8));
        message.setQos(QOS);
        message.setRetained(true);
        currentClient.publish(topic, message);
    }

    /**
     * Serializes a snapshot using the same JSON model served through REST.
     * Package access allows deterministic contract testing without a network.
     *
     * @return activity snapshot encoded as JSON
     * @throws JsonProcessingException when serialization fails
     */
    String createPayload() throws JsonProcessingException {
        return mapper.writeValueAsString(provider.getSnapshot());
    }

    private static String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " is required");
        }
        return value;
    }

    /**
     * Stops publication and closes the MQTT connection.
     */
    @Override
    public synchronized void close() {
        if (publisherExecutor != null) {
            publisherExecutor.shutdownNow();
            publisherExecutor = null;
        }
        if (client != null) {
            try {
                if (client.isConnected()) {
                    client.disconnect();
                }
                client.close();
            } catch (MqttException exception) {
                System.err.println("Unable to close MQTT provider: "
                        + exception.getMessage());
            } finally {
                client = null;
            }
        }
    }
}
