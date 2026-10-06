package edu.calpoly.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static edu.calpoly.monitor.ActivityMqttProvider.QOS;
import static edu.calpoly.storage.MqttStorageEndpoint.RETRIEVE_RESPONSE_TOPIC;

public class StoreMessagesMQTTPublisher {

    public static void handleRetrieve(MessageService service, ObjectMapper mapper, MqttClient client) throws MqttException {
        try {
            List<MessageRecord> records = service.retrieveMessages();
            var message = new MqttMessage(mapper.writeValueAsBytes(records));
            message.setQos(QOS);
            client.publish(RETRIEVE_RESPONSE_TOPIC, message);
        } catch (JsonProcessingException e) {
            var errorMessage = "{\"error\":\"Could not read stored messages.\"}".getBytes(StandardCharsets.UTF_8);
            var mqttMessage = new MqttMessage(errorMessage);
            mqttMessage.setQos(QOS);
            client.publish(RETRIEVE_RESPONSE_TOPIC, mqttMessage);
        } catch (IOException e) {
            System.err.println("Error while reading lines");
        }
    }


}
