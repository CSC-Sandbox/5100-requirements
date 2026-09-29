## Design

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

- A data class for parsing "ROBOT" messages. Also allows for deep copying and a formatted `toString()`. Is used to update Robot instances.

### RobotBlackBoard.java

- A class used to post updates. A registered callback function will be called on every post. One use is to register the `RobotGUI::update` function to automatic calls to update the GUI.

## Design Decisions

- Not putting the broker within `Robot.java`.
    - This was done to give more freedom to what is being displayed. I didn't want to restrict the robot class to only live feeds when playbacks with pausing and other sources of information are possible.
    - Feeding live information can also easily be its own abstraction that can fit many more applications.
- Abstracting `RobotMessage.java` away from `Robot.java`
    - I believe that RobotMessage allows for much more freedom with storing and processing the state or commands of the robot.

## Running Tests

1. Start `TestDisplayRobot.java`.
2. Start `DisplayRobot.java`.
