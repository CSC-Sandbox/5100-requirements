# Gather Eye-Tracking Data

## Overview

This feature simulates an eye-tracking device using a Java Swing GUI. The user clicks on a screen area to represent their current gaze position. The application converts the clicked position into normalized X and Y coordinates between 0.0 and 1.0.

The current gaze position is stored by `GazeService`. The same gaze data is provided through both MQTT and REST so that other parts of the system can consume the latest gaze position.

## Gaze Coordinates

The simulated screen uses the following coordinate system:

* `(0.0, 0.0)` — top-left
* `(0.5, 0.5)` — center
* `(1.0, 1.0)` — bottom-right

The clicked pixel coordinates are normalized using the width and height of the screen area.

## Gaze Data Format

Gaze data is represented as a JSON object containing the normalized X and Y coordinates:

{"x":0.42,"y":0.71}

Both MQTT and REST use this same JSON format.

## Communication

### MQTT

Gaze data is published to the MQTT broker:

tcp://broker.hivemq.com:1883

using the topic:

csc5100/gaze/current

A new gaze position is published whenever the user clicks in the simulated screen.

### REST

The latest gaze position is also available through:

GET /gaze

The REST server runs on port `8080` during the integrated Gather Eye application.

For example:

GET http://localhost:8080/gaze

returns:

{"x":0.42,"y":0.71}

The REST endpoint and MQTT provider use the same `GazeService`, so both provide the current gaze position.

### Original Broker

The original Sprint 1 implementation used the course-provided `Broker` with the message format:

GAZE,X,Y

and the communication service at:

localhost:5000

This original communication remains in the Gather Eye implementation for compatibility with the existing Sprint 1 code.

## Validation

`GazeService` validates gaze coordinates before storing them. Both X and Y must be between `0.0` and `1.0`.

Invalid coordinates such as:

x = 1.5

are rejected.

The REST server also rejects unsupported HTTP methods with a `405 Method Not Allowed` response.

## Testing

The Sprint 2 implementation was tested by:

1. Starting the Gather Eye application.
2. Verifying that the MQTT provider connects to the HiveMQ broker.
3. Clicking in the simulated screen.
4. Verifying that the gaze coordinates are published to `csc5100/gaze/current`.
5. Requesting `GET /gaze` and verifying that it returns the latest gaze coordinates.
6. Testing invalid gaze coordinates and verifying that they are rejected.
7. Testing an unsupported REST method and verifying that it returns `405 Method Not Allowed`.
8. Testing the MQTT provider and consumer together with the Display Eye feature.

## Files

* `src/main/java/edu/calpoly/eye/GatherEye.java` — Simulated eye-tracking application.
* `src/main/java/edu/calpoly/eye/GazeService.java` — Stores and validates the current gaze position.
* `src/main/java/edu/calpoly/eye/GazeMqttProvider.java` — Publishes gaze data through MQTT.
* `src/main/java/edu/calpoly/eye/GazeRestServer.java` — Provides the latest gaze position through REST.
* `src/main/java/edu/calpoly/provided/Broker.java` — Course-provided communication service used by the original implementation.
* `src/test/TestGazeService.java` — Tests rejection of invalid gaze coordinates.
* `src/main/java/edu/calpoly/provided/TestGatherEye.java` — Course-provided test receiver for the original Broker communication.
