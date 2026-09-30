package edu.calpoly.storage;

import org.eclipse.paho.client.mqttv3.*;

public class StoreMessagesMQTTController implements MqttCallback{


	@Override
	public void connectionLost(Throwable cause) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'connectionLost'");
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'messageArrived'");
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'deliveryComplete'");
	}
}
