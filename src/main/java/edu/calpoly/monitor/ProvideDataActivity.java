package edu.calpoly.monitor;

import edu.calpoly.provided.Broker;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

/**
 * Owns the shared activity tracker and starts the complete provider with its
 * existing input stream, REST interface, and MQTT interface. Both outward
 * interfaces request snapshots from this same provider instance.
 *
 * @author Dylan Gururajan
 * @version September 27, 2026
 */
public final class ProvideDataActivity {
    private static final String INPUT_HOST = "localhost";
    private static final int INPUT_PORT = 5000;
    private static final int REST_PORT = 8080;
    private static final long RETRY_DELAY_MILLIS = 1_000;

    private final ActivityTracker tracker;
    private final Clock clock;
    private Thread receiverThread;

    /**
     * Creates a provider using the standard 60-second tracker and system UTC
     * clock.
     */
    public ProvideDataActivity() {
        this(new ActivityTracker(), Clock.systemUTC());
    }

    /**
     * Creates a provider with caller-supplied domain and time dependencies.
     *
     * @param tracker tracker that owns the moving-window calculation
     * @param clock clock used to timestamp outward-facing snapshots
     */
    public ProvideDataActivity(ActivityTracker tracker, Clock clock) {
        this.tracker = Objects.requireNonNull(tracker, "tracker");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    /**
     * Parses and records one incoming broker message when its source is known.
     *
     * @param message incoming message using the {@code TYPE,data...} format
     * @return {@code true} when the message source was recognized and recorded
     */
    public boolean recordMessage(String message) {
        return DataSource.fromMessage(message)
                .map(source -> {
                    tracker.record(source);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Creates the current activity snapshot for REST and MQTT.
     *
     * @return immutable snapshot containing all monitored source counts
     */
    public ActivitySnapshot getSnapshot() {
        return new ActivitySnapshot(
                tracker.window().toSeconds(),
                Instant.now(clock).toString(),
                tracker.snapshot());
    }

    /**
     * Starts receiving existing course-broker messages on a background thread.
     *
     * @param broker course communication broker
     * @param statusListener recipient for connection-status messages
     */
    public synchronized void startReceiver(
            Broker broker,
            Consumer<String> statusListener) {
        Objects.requireNonNull(broker, "broker");
        Objects.requireNonNull(statusListener, "statusListener");
        if (receiverThread != null && receiverThread.isAlive()) {
            return;
        }

        receiverThread = new Thread(
                () -> receiveMessages(broker, statusListener),
                "activity-broker-receiver");
        receiverThread.setDaemon(true);
        receiverThread.start();
    }

    private void receiveMessages(Broker broker, Consumer<String> statusListener) {
        boolean connected = false;
        statusListener.accept("Connecting to the data broker...");

        while (!Thread.currentThread().isInterrupted()) {
            try {
                String message = broker.receive();
                if (!connected) {
                    statusListener.accept("Receiving data from the broker");
                    connected = true;
                }
                recordMessage(message);
            } catch (IllegalStateException exception) {
                connected = false;
                statusListener.accept("Waiting for the data broker...");
                if (!pauseBeforeRetry()) {
                    return;
                }
            }
        }
    }

    private static boolean pauseBeforeRetry() {
        try {
            Thread.sleep(RETRY_DELAY_MILLIS);
            return true;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * Requests that the background receiver stop.
     */
    public synchronized void stopReceiver() {
        if (receiverThread != null) {
            receiverThread.interrupt();
            receiverThread = null;
        }
    }

    /**
     * Runs the data-activity provider until the process is stopped.
     *
     * @param args command-line arguments; currently unused
     * @throws Exception when an outward interface cannot start
     */
    public static void main(String[] args) throws Exception {
        String inputHost = System.getProperty("activity.input.host", INPUT_HOST);
        int inputPort = Integer.getInteger("activity.input.port", INPUT_PORT);
        int restPort = Integer.getInteger("activity.rest.port", REST_PORT);
        String mqttBroker = System.getProperty(
                "activity.mqtt.broker", ActivityMqttProvider.DEFAULT_BROKER);
        String mqttTopic = System.getProperty(
                "activity.mqtt.topic", ActivityMqttProvider.DEFAULT_TOPIC);
        long mqttIntervalMillis = Long.getLong(
                "activity.mqtt.intervalMillis",
                ActivityMqttProvider.DEFAULT_PUBLISH_INTERVAL.toMillis());

        ProvideDataActivity provider = new ProvideDataActivity();
        ActivityRestServer restServer = new ActivityRestServer(provider, restPort);
        ActivityMqttProvider mqttProvider = new ActivityMqttProvider(
                provider,
                mqttBroker,
                mqttTopic,
                Duration.ofMillis(mqttIntervalMillis));

        restServer.start();
        try {
            mqttProvider.start();
        } catch (Exception exception) {
            restServer.close();
            throw exception;
        }
        provider.startReceiver(
                new Broker(inputHost, inputPort),
                System.out::println);

        System.out.println("REST provider: http://localhost:"
                + restServer.getPort() + ActivityRestServer.PATH);
        System.out.println("MQTT provider: " + mqttBroker + " topic " + mqttTopic);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            provider.stopReceiver();
            restServer.close();
            mqttProvider.close();
        }, "activity-provider-shutdown"));

        new CountDownLatch(1).await();
    }
}
