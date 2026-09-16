import edu.calpoly.provided.Broker;
import javax.swing.*;

public class DisplayEye {
    private Broker broker;
    public double eyeX;
    public double eyeY;

    DisplayEye() {
        broker = new Broker("localhost", 5000);
        eyeX = 0.0;
        eyeY = 0.0;
    }

    private void mainLoop () {
        while(true) {
            String message = broker.receive();
            String[] splitMsg = message.split(",");

            if (!splitMsg[0].equals("GAZE")) {
                System.out.println("Ignoring message: \"" + splitMsg[0] + "\" is not type \"GAZE\"");
                continue;
            }

            eyeX = Double.parseDouble(splitMsg[1]);
            eyeY = Double.parseDouble(splitMsg[2]);

            System.out.println("New position: " + eyeX + ", " + eyeY);
        }
    }

    public static void main(String[] args) {
        DisplayEye displayEye = new DisplayEye();
        displayEye.mainLoop();
    }
}
