# Gather Eye-Tracking Data

## Overview

This feature simulates an eye-tracking device using a Java Swing GUI. The user clicks on a screen area to represent their current gaze position. The application converts the clicked position into normalized X and Y coordinates between 0.0 and 1.0 and publishes the gaze data through the provided `Broker`.

## Gaze Coordinates

The simulated screen uses the following coordinate system:

* `(0.0, 0.0)` — top-left
* `(0.5, 0.5)` — center
* `(1.0, 1.0)` — bottom-right

The clicked pixel coordinates are normalized using the width and height of the screen area.

## Communication

Gaze data is sent through the provided `Broker` using the format:

```text
GAZE,X,Y
```

For example:

```text
GAZE,0.50,0.50
```

The application connects to the communication service at:

```text
localhost:5000
```

## Testing

The provided `TestGatherEye` program was used to verify communication.

The test was performed by:

1. Starting `TestGatherEye`.
2. Starting `GatherEye`.
3. Clicking in different locations on the simulated screen.
4. Verifying that the receiver displayed the published `GAZE,X,Y` messages.

The top-left, center, and bottom-right areas of the screen were tested to verify that the normalized coordinates were approximately `0.0`, `0.5`, and `1.0` respectively.

## Files

* `src/main/java/edu/calpoly/eye/GatherEye.java` — Simulated eye-tracking application.
* `src/main/java/edu/calpoly/provided/Broker.java` — Course-provided communication service.
* `src/main/java/edu/calpoly/provided/TestGatherEye.java` — Course-provided test receiver.
