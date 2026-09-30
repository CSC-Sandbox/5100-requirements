package edu.calpoly.eye;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

/**
 * MQTT Consumer for gaze data
 * Gaze data is expected to be a JSON object representing a GazePoint
 *
 * @author James Yaguma
 * @version 1.0 (2026-09-28)
 */
public class GazeMqttConsumer {
    /**
     * Start the consumer with the hivemq broker and GazeBlackboard as default
     *
     * @see #start(GazeBlackboard, String)
     */
    public void start() throws Exception {
        start(GazeBlackboard.getInstance(), "tcp://broker.hivemq.com:1883");
    }

    /**
     * Start the consumer to the specified blackboard with the hivemq broker as default
     *
     * @see #start(GazeBlackboard, String)
     */
    public void start(GazeBlackboard blackboard) throws Exception {
        start(blackboard, "tcp://broker.hivemq.com:1883");
    }

    /**
     * Starts the consumer to be subscribed to the "csc/5100/gaze/current" topic
     * Upon a valid message received, send the data to GazeBlackboard
     *
     * @param blackboard The blackboard to send gaze data to
     * @param broker The URL of the broker to connect to
     * @throws Exception
     */
    public void start(GazeBlackboard blackboard, String broker) throws Exception {
        MqttClient client = new MqttClient(broker, MqttClient.generateClientId());
        MqttConnectOptions options =  new MqttConnectOptions();
        options.setCleanSession(true);
        client.connect(options);

        client.subscribe("csc5100/gaze/current", (topic, message) -> {
            GazePoint gazePoint;
            try {
                gazePoint = GazePoint.fromJSON(message.getPayload());
                //TODO: send gp to GazeBlackboard
                blackboard.updateGazePoint(gazePoint);
                System.err.println("New GazePoint received: (" + gazePoint.x + ", " + gazePoint.y + ")");
            } catch (Exception e) {
                System.err.println("Invalid GAZE message received: " + e.getMessage());
            }
        });
    }
}
