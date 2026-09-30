## Design

### RobotServer.java

- Starts the robot server with a GUI, BlackBoard, MQTT adapter, and REST adapter. Rest endpoints are `PUT /robot` and `GET /robot`. This server provides Robot information on GET requests and visualizes all information from PUTs and publishers.

### RobotRESTAdapter.java

- Defines REST interactions with `RobotServer.java` specifically through stored callback functions.
- Endpoints
    - `PUT http://localhost:5001/robot` receives a JSON body of `{"jointAngles":[j1,j2,j3,j4,j5,j6],"position":[x,y,z]}` and updates the `RobotBlackBoard` within the `RobotServer`.
    - `GET http://localhost:5001/robot` returns the latest robot data as JSON, or `404` when no data exists.

### TestRobotRESTAdapter.java

- Creates a RobotRESTProvider and sends messages to a RobotServer running on localhost. The test cases will print pass or fail.

### RobotMQTTAdapter.java

- Subscribes to MQTT topic `csc5100/robot/1` using Paho.
- Valid JSON robot data is posted to `RobotBlackBoard`.
- Invalid messages are reported and ignored.

### TestRobotMQTTAdapter.java

- Creates a RobotMQTTProvider and publishes to the RobotServer. Every 10 messages the published messages alternate between random valid messages to invalid messages.

### RobotRESTProvider.java

- Assists in sending REST requests to the RobotServer.
- Both PUT and GET requests can be made.
- Payloads can be String or RobotMessage.

### RobotMQTTProvider.java

- Publishes robot data to the MQTT broker for the RobotServer to receive.
- Payloads can be String or RobotMessage.

### RobotGUI.java

- Manages a robot GUI at a high level and is what would primarily be interacted with. Decides when to update the displays and decides what to display. Manages the composition of the `RobotDisplayPanel` and `RobotDataPanel`
    - **Instantiating**: creates a blank instance.
    - **Show DataPanel**: Shows or hides the data panel.
    - **Show RobotPanel**: Shows or hides the data panel.
    - **Updating**: Re-draws the visualization and updates data.
    - **Add to frame**: Adds the visualization to a JFrame.
    - **Toggle showData**: Decides whether to display robot data in addition to the visualization.

### RobotDisplayPanel.java

- Manages the display of the robot. Resizing to fit the container. Drawing and storing a 6 segmented arm into a BufferedImage and then displayed.

### RobotDataPanel.java

- Manages the displaying of the robot data. Includes when it was last modified and general angles and position data.

### RobotMessage.java

- Represents robot data containing six joint angles and a 3D position. The same format is used by REST and MQTT. Also allows for deep copying and a formatted `toString()`. Is used to update the robot display.

### RobotBlackBoard.java

- A class used to post updates. A registered callback function will be called on every post. One use is to register the `RobotGUI::update` function to automatic calls to update the GUI.

## Design Decisions

- Not putting the broker within `Robot.java`.
    - This was done to give more freedom to what is being displayed. I didn't want to restrict the robot class to only live feeds when playbacks with pausing and other sources of information are possible.
    - Feeding live information can also easily be its own abstraction that can fit many more applications.
- Abstracting `RobotMessage.java` away from `Robot.java`
    - I believe that RobotMessage allows for much more freedom with storing and processing the state or commands of the robot.

## Running Tests

1. Run `mvn clean test`.
2. Start `RobotServer`, then run `TestRobotRESTAdapter` to test REST endpoints.
3. Start `RobotServer`, then run `TestRobotMQTTAdapter` to test MQTT consumption.

## Communication Contract

Below is the following of what we established:

- REST endpoint: `GET /robot`
- MQTT topic: `csc5100/robot/1`
- Both interfaces use the same JSON `RobotMessage` format.
