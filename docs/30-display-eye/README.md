# Display Real-Time Gaze Position

## Design

### DisplayEye.java

The main file to run for the visualization. Initializes the GUI frame and, depending on what kind of interface is desired to use (`Broker`, REST, MQTT), initializes the necessary classes to start receiving Gaze data.

### GazePoint.java

A class whose job is to store a pair of `(X, Y)` coordinates. It has a setter in the case both `X` and `Y` are desired to be set simultaneously. 

### GazeBroker.java

A class who's main job is to receive and validate `GAZE` messages. Upon receiving a `GAZE` message, it firsts checks to make sure all of the fields are valid (message type `GAZE`, numerical values within range for `X` and `Y`, etc.). A `GazeBlackboard` should be passed when starting the broker via `loopForever`. The `GazeBlackboard` given will be updated everytime a valid message was received.

### GazeGUI.java

The main job of `GazeGUI.java` is to manage the different UI components that will be shown to the user. It also makes sure to update those components upon receiving a new message from the `GazeBroker`.

### GazeArea.java

This inherits from `JPanel` and creates the main visualization of the gaze position. As noted in the assignment, the top-left corner is `(0.0, 0.0)`, and the bottom-right corner is `(1.0, 1.0)`. The current (or more accurately the last received) gaze position is visualized with a red circle on the screen.

### GazeInfo.java

This inherits from `JPanel` and creates the information displayed on the side of the main visualization. This includes the current `(X, Y)` position of the gaze in numbers, the connection status, and instructions when using this for the first time. *Note about connection status: It's admittedly not completely accurate as it's won't know when the publisher stops publishing, but it can acknowledge the initial connection, at least.*

### GazeBlackboard.java

A blackboard for the gaze data receiving process to be separated from the GUI classes. Is a singleton whose instance can be acquire via `getInstance()`. Other classes can set/update the currently stored gaze data via `updateGazePoint(GazePoint)`. Additionally, it is possible to add callback methods via `addCallback(Consumer<GazePoint>)` which will be called every time new gaze data has been received.

### GazeMqttConsumer.java

A MQTT consumer that subscribes to the `csc5100/gaze/current` topic. The `start` method is used to start receiving MQTT messages. The messages will either be sent to the `GazeBlackboard` specified when using the `start` method, or default to the `GazeBlackboard`'s singleton instance if none is specified. Additionally, `start(GazeBlackboard, String)` can be used to specify both the blackboard and the broker URL if desired.

### GazeRestClient.java

A REST client that performs `GET` requests to the specified URL upon initialization (default is `http://localhost:8080/gaze`). The blackboard the data gets sent to is also `GazeBlackboard`'s singleton instance unless specified. To start making requests and receiving gaze data, `loopForver(int)` needs to be called with the `int` representing the delay between requests in milliseconds. By default, it is 150ms if called without arguments. This is not multithreaded by default, so it is recommended to have a thread run `loopForever` when using this.  

## Running Tests

1. Run `TestDisplayEye.java` **first**
2. Run `DisplayEye.java`
3. Observe the gaze position move in a snake pattern
4. If invalid messages want to be tested, go to `TestDisplayEye.java`, comment out `sendSnake(out)`, and uncomment `sendInvalidTests(out)`
5. Repeat from step 1

### Testing MQTT

1. Set `interfaceMode` to `'m'` in `DisplayEye.java` (Alternatively, call `DisplayEye` with the `m` argument later)
2. Run `DisplayEye.java`
3. Run `TestGazeMqttConsumer.java`
4. Watch the output in logs to see which messages were accepted and which messages were deemed invalid


### Testing REST

1. Set `interfaceMode` to `'r'` in `DisplayEye.java` (Alternatively, call `DisplayEye` with the `r` argument later)
2. Run `TestGazeRestClient.java` **first**
3. Run `DisplayEye.java`
4. Watch the output in logs to see which messages were accepted and which messages were deemed invalid