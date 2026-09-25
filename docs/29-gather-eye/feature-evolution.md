# Feature Evolution

## What Will the Feature Become?

The Gather Eye feature will evolve from a simple GUI-based gaze tracker into a **service that publishes gaze data through MQTT**.

Currently, the feature detects the user's mouse position on a GUI screen, converts the pixel coordinates into normalized coordinates between `0.0` and `1.0`, and publishes the result through the provided `Broker`.

In the future, the feature can use MQTT as the communication mechanism so that other components of the system can subscribe to gaze data without needing to depend directly on the Gather Eye implementation.

The expected MQTT topic will be:

gaze

The gaze data will continue to use the format:

GAZE,X,Y

For example:

GAZE,0.50,0.50

This allows the gaze-tracking implementation to change independently from the components that consume the gaze data.

## Define the Contract

### Input

The feature receives the user's mouse position from the GUI.

The mouse position is initially represented as pixel coordinates:

X = pixel X position
Y = pixel Y position

The feature converts these coordinates into normalized values between `0.0` and `1.0`.

### Output

The feature produces gaze data containing the normalized X and Y coordinates.

The current message format is:

GAZE,X,Y

For example:

GAZE,0.50,0.50

### Communication Mechanism

The current implementation uses the provided `Broker` at:

localhost:5000

As the feature evolves into a service, MQTT will be used to publish the gaze data.

The MQTT topic will be:

gaze

Consumers can subscribe to this topic to receive gaze updates.

## Identify Dependencies

Gather Eye currently depends on:

* A GUI for receiving mouse input.
* `MouseEvent` to obtain the mouse's pixel X and Y coordinates.
* Logic that normalizes the pixel coordinates to values between `0.0` and `1.0`.
* The provided `Broker` for publishing gaze data.

As the feature evolves, it will depend on an MQTT publisher rather than being tightly coupled to the provided broker implementation.

Other system features may depend on Gather Eye by consuming its gaze data. For example, display or interaction components could subscribe to the `gaze` topic and use the normalized coordinates to determine where the user is looking.

## Interfaces and Abstractions

The feature can be separated into three main responsibilities:

Gaze Source
     ↓
Gaze Data
     ↓
Publisher

### Gaze Source

The Gaze Source is responsible for obtaining the user's current position.

The current implementation uses mouse input from the GUI. This abstraction allows the source of gaze information to change in the future without changing the rest of the feature.

### Gaze Data

Gaze Data represents the normalized X and Y coordinates produced by the feature.

The data follows the existing format:

GAZE,X,Y

Keeping this format consistent allows other components to consume gaze information without knowing how the coordinates were collected.

### Publisher

The Publisher is responsible for sending gaze data to the rest of the system.

The current implementation uses the provided `Broker`. In the future, this responsibility can be implemented using MQTT and the `gaze` topic.

Separating the publisher from the gaze source means the system can change its communication mechanism without changing how gaze information is collected.
