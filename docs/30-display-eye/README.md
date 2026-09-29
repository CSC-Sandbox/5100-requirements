# Display Real-Time Gaze Position

## Design

### DisplayEye.java

The main file to run for the visualization. Creates and starts the broker (`GazeBroker.java`) used to receive messages as well as initializes the GUI frame

### GazePoint.java

A class whose job is to store a pair of `(X, Y)` coordinates. It has a setter in the case both `X` and `Y` are desired to be set simultaneously. 

### GazeBroker.java

A class who's main job is to receive and validate `GAZE` messages. Upon receiving a `GAZE` message, it firsts checks to make sure all of the fields are valid (message type `GAZE`, numerical values within range for `X` and `Y`, etc.). A `Consumer<GazePoint>` should be passed when starting the broker via `loopForever`. The `Consumer` is called everytime a valid message was received with the new `(X, Y)` values being passed as a `GazePoint` into the `Consumer`.

### GazeGUI.java

The main job of `GazeGUI.java` is to manage the different UI components that will be shown to the user. It also makes sure to update those components upon receiving a new message from the `GazeBroker`.

### GazeArea.java

This inherits from `JPanel` and creates the main visualization of the gaze position. As noted in the assignment, the top-left corner is `(0.0, 0.0)`, and the bottom-right corner is `(1.0, 1.0)`. The current (or more accurately the last received) gaze position is visualized with a red circle on the screen.

### GazeInfo.java

This inherits from `JPanel` and creates the information displayed on the side of the main visualization. This includes the current `(X, Y)` position of the gaze in numbers, the connection status, and instructions when using this for the first time. *Note about connection status: It's admittedly not completely accurate as it's won't know when the publisher stops publishing, but it can acknowledge the initial connection, at least.*

## Running Tests

1. Run `TestDisplayEye.java` **first**
2. Run `DisplayEye.java`
3. Observe the gaze position move in a snake pattern
4. If invalid messages want to be tested, go to `TestDisplayEye.java`, comment out `sendSnake(out)`, and uncomment `sendInvalidTests(out)`
5. Repeat from step 1