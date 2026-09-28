package edu.calpoly.monitor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Obtains current data-activity information by periodically polling the
 * paired provider's /activity REST endpoint.
 * @author Aiden Rodriguez
 * @version September 27, 2026
 */
public final class ActivityRestClient implements ActivitySnapshotSource {
    public static final Duration DEFAULT_POLL_INTERVAL = Duration.ofSeconds(1);
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(3);

    private final URI activityUri;
    private final Duration pollInterval;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(REQUEST_TIMEOUT)
            .build();
    private final ObjectMapper mapper = new ObjectMapper();

    private final AtomicReference<ActivitySnapshot> lastSnapshot = new AtomicReference<>();
    private volatile String status = "Not yet connected";

    private ScheduledExecutorService pollExecutor;

    /**
     * Creates a REST client using the default one-second poll interval.
     *
     * @param baseUri provider base URI, e.g. {@code http://localhost:8080}
     */
    public ActivityRestClient(String baseUri) {
        this(baseUri, DEFAULT_POLL_INTERVAL);
    }

    /**
     * Creates a REST client with a caller-supplied poll interval.
     *
     * @param baseUri provider base URI, e.g. {@code http://localhost:8080}
     * @param pollInterval how often to request a fresh snapshot
     */
    public ActivityRestClient(String baseUri, Duration pollInterval) {
        Objects.requireNonNull(baseUri, "baseUri");
        this.activityUri = URI.create(baseUri + ActivityRestServer.PATH);
        this.pollInterval = Objects.requireNonNull(pollInterval, "pollInterval");
        if (pollInterval.isZero() || pollInterval.isNegative()) {
            throw new IllegalArgumentException("pollInterval must be positive");
        }
    }

    @Override
    public synchronized void start() {
        if (pollExecutor != null) {
            return;
        }
        status = "Connecting to " + activityUri + "...";
        pollExecutor = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "activity-rest-poller");
            thread.setDaemon(true);
            return thread;
        });
        pollExecutor.scheduleAtFixedRate(
                this::pollSafely, 0, pollInterval.toMillis(), TimeUnit.MILLISECONDS);
    }

    private void pollSafely() {
        HttpResponse<String> response;
        try {
            HttpRequest request = HttpRequest.newBuilder(activityUri)
                    .timeout(REQUEST_TIMEOUT)
                    .GET()
                    .build();
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException exception) {
            status = "Waiting for the activity provider at " + activityUri + "...";
            return;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return;
        }

        if (response.statusCode() != 200) {
            status = "Provider returned HTTP " + response.statusCode();
            return;
        }

        try {
            ActivitySnapshot snapshot = mapper.readValue(response.body(), ActivitySnapshot.class);
            lastSnapshot.set(snapshot);
            status = "Receiving data from " + activityUri;
        } catch (JsonProcessingException | IllegalArgumentException exception) {
            // Keep the last good snapshot on screen; surface the problem rather than crashing.
            status = "Received malformed activity data: " + exception.getMessage();
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

    @Override
    public synchronized void close() {
        if (pollExecutor != null) {
            pollExecutor.shutdownNow();
            pollExecutor = null;
        }
    }
}