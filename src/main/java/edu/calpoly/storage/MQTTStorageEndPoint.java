package edu.calpoly.storage;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.calpoly.message.MessageValidator;
import edu.calpoly.storage.FileMessageReader;
import edu.calpoly.storage.FileMessageStore;
import edu.calpoly.storage.MessageRecord;
import edu.calpoly.storage.MessageService;
import edu.calpoly.storage.StoreMessageRequest;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.List;

/**
 * MQTT interface over MessageService, providing equivalent store/retrieve
 * behavior to the REST interface. Since MQTT has no native request/response,
 * retrieval uses a request topic and a separate response topic.
 *
 * IMPORTANT: this connects to the same shared public broker as the
 * Temperature example (broker.hivemq.com) - topic should stay to the agreed csc5100/storage/
 *
 * @author Edgard Aviles
 * @version 1.0.0 October 4, 2026
 */
public class MqttStorageEndpoint {

    public static final String BROKER = "tcp://broker.hivemq.com:1883";
    public static final String STORE_TOPIC = "csc5100/storage/store";
    public static final String RETRIEVE_REQUEST_TOPIC = "csc5100/storage/retrieve/request";
    public static final String RETRIEVE_RESPONSE_TOPIC = "csc5100/storage/retrieve/response";
    private static final int QOS = 1;

    private final MessageService service;
    private final MessageValidator validator = new MessageValidator();
    private final ObjectMapper mapper = new ObjectMapper();
    private MqttClient client;

    public MqttStorageEndpoint(MessageService service) {
        this.service = service;
    }

    public void start() throws Exception {
        client = new MqttClient(BROKER, MqttClient.generateClientId());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setAutomaticReconnect(true);
        client.connect(options);

        client.subscribe(STORE_TOPIC, (topic, message) -> handleStore(message));
        client.subscribe(RETRIEVE_REQUEST_TOPIC, (topic, message) -> handleRetrieve());

        System.out.println("MQTT provider subscribed to " + STORE_TOPIC + " and " + RETRIEVE_REQUEST_TOPIC);
    }

    public void stop() throws Exception {
        client.disconnect();
        client.close();
    }

    private void handleStore(MqttMessage message) {
        try {
            StoreMessageRequest request = mapper.readValue(message.getPayload(), StoreMessageRequest.class);

            if (request.message() == null) {
                System.out.println("Rejected MQTT store payload: missing 'message' field.");
                return;
            }
            if (!validator.validate(request.message())) {
                System.out.println("Rejected invalid message: " + validator.getLastError());
                return;
            }

            service.storeMessage(request.message());
        } catch (Exception e) {
            System.out.println("Invalid MQTT store payload: " + e.getMessage());
        }
    }

    private void handleRetrieve() {
        try {
            List<MessageRecord> records = service.retrieveMessages();
            publish(RETRIEVE_RESPONSE_TOPIC, mapper.writeValueAsString(records));
        } catch (Exception e) {
            publish(RETRIEVE_RESPONSE_TOPIC, "{\"error\":\"Could not read stored messages.\"}");
        }
    }

    private void publish(String topic, String json) {
        try {
            MqttMessage message = new MqttMessage(json.getBytes());
            message.setQos(QOS);
            client.publish(topic, message);
        } catch (Exception e) {
            System.out.println("Could not publish to " + topic + ": " + e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        FileMessageStore store = new FileMessageStore("data/messages.csv");
        FileMessageReader reader = new FileMessageReader("data/messages.csv");
        MqttStorageEndpoint endpoint = new MqttStorageEndpoint(new MessageService(store, reader));
        endpoint.start();
        System.out.println("Waiting for storage requests. Press Ctrl+C to stop.");
    }
}
