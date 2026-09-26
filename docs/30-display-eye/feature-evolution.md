# Feature Evolution

## Refactor Planning

One of the refactoring opportunities I believe exist is by splitting up the work done in `GazeBroker.java`. Currently, it handles both receiving and validating messages, as well as calling the update method upon receiving a valid message. It would most likely be better to only have the class perform message handling and have another class handle whcih callback functions to execute upon an update similar to a blackboard design pattern. That way, most of the code can be left untouched if another source of gaze messages are to be received.

## What Will the Feature Become?

This will most likely become a service that runs independently. Currently, the communication mechanism uses the provided `Broker` class to send messages across `localhost:5000`. In the future, it will most likely use MQTT as the communication required is generally just the GUI getting updated with live gaze data. So compared to constantly requiring request/response with REST, MQTT being designed for long-lived asyncrhonous messaging works better here.

## Defining the Contract

**What are the inputs?**

This feature accepts a stream of `GAZE` messages in the format of `GAZE,X,Y`. Each message should signify the current gaze position and be sent via MQTT. 

**What are the outputs?**

A GUI visually showing the most recent gaze position received from the `GAZE` messages.

## Dependencies

As `GAZE` messages are necessary to display gaze information, the related features such as gathering the data are necessary. 

In the future, if the blackboard class becomes implemented, the message handling will be separated from the callback function, which would allow another implementation of message handling to be implemented without needing to change most of the GUI code.