package displayEye;

import edu.calpoly.provided.Broker;

public class DisplayEye {
    private GazeBroker broker;
    public double eyeX;
    public double eyeY;

    DisplayEye() {
        broker = new GazeBroker("localhost", 5000, true);
        eyeX = 0.0;
        eyeY = 0.0;
    }

    public static void main(String[] args) {
        DisplayEye displayEye = new DisplayEye();
        displayEye.broker.loopForever();
    }
}
