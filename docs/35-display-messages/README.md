# Dispay Messages

## Design 

### DisplayMessages.java
The main file that is an JComponent that contains a JTextArea and a JScrollBar. This class also 
instantiates a Broker and runs in a continuous loop to receive new messages, This is only done for testing.
On a real application it would be best to create a new thread and have it continously listen for new messages.



## Running Test
1. Run TestDisplayMessages First so that it is connected to a host and socket
2. Run DisplayMessages.java main function
3. Wait and see the newly arrived messages with a timestamp, of when the message arrived.