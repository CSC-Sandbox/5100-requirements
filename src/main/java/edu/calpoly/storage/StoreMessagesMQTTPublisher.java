package edu.calpoly.storage;

public class StoreMessagesMQTTPublisher {
    private static final String broker = "tcp://test.messages.org:5873";
    private static final String topic = "csc5100/messages";
    private static final String clientId = "message-retriever";

    public StoreMessagesMQTTPublisher() {
    }


}
