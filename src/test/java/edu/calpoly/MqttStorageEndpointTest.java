package edu.calpoly.storage;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.calpoly.storage.FileMessageReader;
import edu.calpoly.storage.FileMessageStore;
import edu.calpoly.storage.MessageRecord;
import edu.calpoly.storage.MessageService;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Integration tests for MqttStorageEndpoint: runs the real endpoint against
 * the shared broker and a temp data file, and exercises it with a separate
 * test MQTT client, the way the Consume side will.
 * if you want this test we can delete this, since this was just to test my own version
 * @author Edgard Aviles
 * @version 1.0.0 october 4, 2026
 */
class MqttStorageEndpointTest {

    private static final long TIMEOUT_SECONDS = 10;

    private MqttStorageEndpoint endpoint;
    private MqttClient testClient;
    private FileMessageReader reader;
    private final ObjectMapper mapper = new ObjectMapper();

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws Exception {
        String dataFile = tempDir.resolve("test-messages.csv").toString();
        FileMessageStore store = new FileMessageStore(dataFile);
        reader = new FileMessageReader(dataFile);

        endpoint = new MqttStorageEndpoint(new MessageService(store, reader));
        endpoint.start();

        testClient = new MqttClient(MqttStorageEndpoint.BROKER, MqttClient.generateClientId());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        testClient.connect(options);
    }

    @AfterEach
    void tearDown() throws Exception {
        testClient.disconnect();
        testClient.close();
        endpoint.stop();
    }

    @Test
    void publishingAStoreMessage_persistsIt() throws Exception {
        publish(MqttStorageEndpoint.STORE_TOPIC, "{\"message\":\"LIDAR,1.0,2.0,3.0\"}");

        List<MessageRecord> records = waitUntilStored(1);
        assertEquals(1, records.size());
        assertEquals("LIDAR,1.0,2.0,3.0", records.get(0).getMessage());
    }

    @Test
    void publishingAnInvalidStoreMessage_isNotPersisted() throws Exception {
        // LIDAR requires 3 values; this only has 2 - should be rejected, not stored.
        publish(MqttStorageEndpoint.STORE_TOPIC, "{\"message\":\"LIDAR,1.0,2.0\"}");

        // Give the subscriber a moment to (not) process it, then confirm nothing landed.
        Thread.sleep(2000);
        assertEquals(0, reader.readAllRecords().size());
    }

    @Test
    void retrieveRequest_publishesStoredMessagesToResponseTopic() throws Exception {
        publish(MqttStorageEndpoint.STORE_TOPIC, "{\"message\":\"GAZE,0.5,0.5\"}");
        waitUntilStored(1);

        AtomicReference<String> response = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        testClient.subscribe(MqttStorageEndpoint.RETRIEVE_RESPONSE_TOPIC, (topic, message) -> {
            response.set(new String(message.getPayload()));
            latch.countDown();
        });

        publish(MqttStorageEndpoint.RETRIEVE_REQUEST_TOPIC, "{}");

        assertTrue(latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS), "Timed out waiting for retrieve response.");

        JsonNode records = mapper.readTree(response.get());
        assertTrue(records.isArray());
        assertEquals(1, records.size());
        assertEquals("GAZE,0.5,0.5", records.get(0).get("message").asText());
    }

    private void publish(String topic, String payload) throws Exception {
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(1);
        testClient.publish(topic, message);
    }

    /**
     * Polls the stored file until the expected number of records appears,
     * or fails after TIMEOUT_SECONDS. Needed because MQTT delivery/handling
     * is asynchronous relative to this test thread.
     */
    private List<MessageRecord> waitUntilStored(int expectedCount) throws Exception {
        long deadline = System.currentTimeMillis() + TIMEOUT_SECONDS * 1000;
        List<MessageRecord> records;
        do {
            records = reader.readAllRecords();
            if (records.size() >= expectedCount) {
                return records;
            }
            Thread.sleep(200);
        } while (System.currentTimeMillis() < deadline);
        fail("Timed out waiting for " + expectedCount + " stored record(s); found " + records.size());
        return records; // unreachable
    }
}
