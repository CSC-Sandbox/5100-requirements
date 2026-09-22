## Design

### MonitorAvailability.java

- Monitors the availability of Robot, Gaze, Affect, and LiDAR data sources.
- Receives messages through the provided `Broker`.
- Tracks the most recent message time for each source.
- Marks a source as `UNAVAILABLE` if more than one second passes without receiving data.
- Uses a Java Swing GUI to display the current status of all four sources.
- Uses a background thread for receiving messages and a Swing timer for periodically refreshing source availability.

## Design Decisions

- Using the provided `Broker`
    - The assignment provides the communication infrastructure, so no additional socket or networking abstraction was created.

- Tracking only the most recent timestamp for each source
    - The availability monitor only needs to determine whether each source has produced data within the last second.
    - Storing complete message histories would add unnecessary complexity.

- Keeping the implementation in a single class
    - The monitoring application has a small, focused responsibility.
    - Additional classes were not introduced unless needed by the functionality.

- Using a background receiver thread
    - `Broker.receive()` waits for incoming messages, so receiving data on a separate thread prevents it from blocking the Swing interface.

- Using a Swing timer for availability updates
    - Availability must change even when a source stops sending messages, so the GUI periodically checks each source's last-received timestamp.

## Running Tests

1. Start `TestMonitorData.java`.
2. Start `MonitorAvailability.java`.
3. Verify that all four sources initially become `AVAILABLE`.
4. Observe each source becoming `UNAVAILABLE` during its simulated outage.
5. Verify that the source returns to `AVAILABLE` when messages resume.
