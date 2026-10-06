package edu.calpoly.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.calpoly.message.MessageValidator;
import org.eclipse.paho.client.mqttv3.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;

public class RetrieveMessagesMQTTSubscriber {


    public static void handleStore(MessageService service, ObjectMapper mapper, MessageValidator validator, MqttMessage message) {
        try {
            var mqttMessage = new String(message.getPayload(), StandardCharsets.UTF_8);
            if (!validator.validate(mqttMessage)) {
                System.err.println("Rejected invalid message: " + validator.getLastError());
                return;
            }

            service.storeMessage(mqttMessage);
        } catch (Exception e) {
            System.err.println("Invalid MQTT store payload: " + e.getMessage());
        }
    }
}
