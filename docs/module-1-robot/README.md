## Design

### Robot.java

- Manages a robot at a high level and is what would primarily be interacted with. Decides when to update the displays and stores a copy of passed update data. Manages the composition of the `RobotDisplay` and `RobotDataPanel`
    - **Instantiating**: Can decide the resolution of visualization.
    - **Updating**: Re-draws the visualization and updates data.
    - **Add to frame**: Adds the visualization to a JFrame.
    - **Toggle showData**: Decides whether to display robot data in addition to the visualization.

### RobotDisplay.java

- Manages the display of the robot. Resizing to fit the container. Drawing and storing a 6 segmented arm into a BufferedImage and then displayed.

### RobotDataPanel.java

- Manages the displaying of the robot data. Includes when it was last modified and general angles and position data.

### RobotMessage.java

- A data class for parsing "ROBOT" messages. Also allows for deep copying and a formatted `toString()`. Is used to update Robot instances.

## Design Decisions

- Not putting the broker within `Robot.java`.
    - This was done to give more freedom to what is being displayed. I didn't want to restrict the robot class to only live feeds when playbacks with pausing and other sources of information are possible.
    - Feeding live information can also easily be its own abstraction that can fit many more applications.
- Abstracting `RobotMessage.java` away from `Robot.java`
    - I believe that RobotMessage allows for much more freedom with storing and processing the state or commands of the robot.

## Running Tests

1. Start `TestDisplayRobot.java`.
2. Start `DisplayRobot.java`
    - Note: if you comment out `robot.showData(true);` the robot will render without the extra written data.
