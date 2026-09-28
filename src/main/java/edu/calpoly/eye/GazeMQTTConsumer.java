package edu.calpoly.eye;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

public class GazeMQTTConsumer {
    /**
     * Start the consumer with the hivemq broker as default
     *
     * @see #start(String)
     */
    public void start() throws Exception {
        start("tcp://broker.hivemq.com:1883");
    }

    /**
     * Starts the consumer to be subscribed to the "csc/5100/gaze/current" topic
     * Upon a valid message received, send the data to GazeBlackboard
     *
     * @param broker The URL of the broker to connect to
     * @throws Exception
     */
    public void start(String broker) throws Exception {
        MqttClient client = new MqttClient(broker, MqttClient.generateClientId());
        MqttConnectOptions options =  new MqttConnectOptions();
        options.setCleanSession(true);
        client.connect(options);

        client.subscribe("csc5100/gaze/current", (topic, message) -> {
            GazePoint gp;
            try {
                gp = GazePoint.fromJSON(message.getPayload());
                //TODO: send gp to GazeBlackboard
                System.out.println("New GazePoint received: (" + gp.x + ", " + gp.y + ")");
            } catch (Exception e) {
                System.err.println("Invalid GAZE message received: " + e.getMessage());
            }
        });
    }
}
