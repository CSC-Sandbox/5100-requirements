package edu.calpoly.eye;

/**
 * Simple test for the gaze MQTT provider.
 */
public class TestGazeMqttProvider {

    public static void main(String[] args) throws Exception {
        GazeService service = new GazeService();

        GazePoint gazePoint = new GazePoint(0.42, 0.71);
        service.setGazePoint(gazePoint);

        GazeMqttProvider provider = new GazeMqttProvider(service);

        provider.connect();
        provider.publish();
        provider.disconnect();
    }
}