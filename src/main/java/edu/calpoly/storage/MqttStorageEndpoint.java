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
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.List;

/**
 * MQTT interface over MessageService, providing equivalent store/retrieve
 * behavior to the REST interface. Since MQTT has no native request/response,
 * retrieval uses a request topic and a separate response topic.
 * <p>
 * IMPORTANT: this connects to the same shared public broker as the
 * Temperature example (broker.hivemq.com) - topic should stay to the agreed csc5100/storage/
 *
 * @author Edgard Aviles
 * @version 1.0.0 October 4, 2026
 */
public class MqttStorageEndpoint {

    public static final String BROKER = "tcp://localhost:1883";
    public static final String STORE_TOPIC = "csc5100/storage/store";
    public static final String RETRIEVE_REQUEST_TOPIC = "csc5100/storage/retrieve/request";
    public static final String RETRIEVE_RESPONSE_TOPIC = "csc5100/storage/retrieve/response";
    private static final int QOS = 1;

    private final MessageService service;
    private final MessageValidator validator = new MessageValidator();
    private final ObjectMapper mapper = new ObjectMapper();
    private static MqttClient client;

    public MqttStorageEndpoint(MessageService service) {
        this.service = service;
    }

    public static void main(String[] args) throws Exception {
        FileMessageStore store = new FileMessageStore("data/messages.csv");
        FileMessageReader reader = new FileMessageReader("data/messages.csv");
        MqttStorageEndpoint endpoint = new MqttStorageEndpoint(new MessageService(store, reader));
        endpoint.start();
        System.out.println("Waiting for storage requests. Press Ctrl+C to stop.");


    }

    public void start() throws Exception {

        client = new MqttClient(BROKER, MqttClient.generateClientId()); // 👈 fix

        MqttConnectOptions options = new MqttConnectOptions();
        client.connect(options);

        System.out.println("Connected: " + client.isConnected()); // confirm connection

        client.subscribe(STORE_TOPIC, (topic, message) -> {
            System.err.println("Got Store Request");
            try {
                RetrieveMessagesMQTTSubscriber.handleStore(service, mapper, validator, message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        client.subscribe(RETRIEVE_REQUEST_TOPIC, (topic, message) -> {
            System.err.println("Got Retrieve Request");
            try {
                StoreMessagesMQTTPublisher.handleRetrieve(service, mapper, client);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        });

        System.out.println("MQTT provider subscribed to " + STORE_TOPIC + " and " + RETRIEVE_REQUEST_TOPIC);
    }

    public void stop() throws Exception {
        client.disconnect();
        client.close();
    }

}
