package edu.calpoly.robot;

import java.util.function.Consumer;

import edu.calpoly.provided.Broker;

/**
 * RobotMQttAdapter is an adapter class for the passed in RobotBlackBoard that
 * updates the blackboard everytime the MQTT subscriber is passed new data.
 * 
 * @author Paul Motter (PaulMotter)
 * @version 1.0.0 (9/26/2026)
 */
public class RobotMQTTAdapter extends Thread {

    Broker broker;
    Consumer<RobotMessage> onSub;

    RobotMQTTAdapter(String brokerHost, int brokerPort, RobotBlackBoard rbb){
        broker = new Broker(brokerHost, brokerPort);
        onSub = rbb::post;
    }

    /**
     * 
     * @see java.lang.Thread#run()
     */
    public void run(){
        System.out.println("MQTT subscriber on");
    } 
}
