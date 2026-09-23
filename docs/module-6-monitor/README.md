## Design

### MonitorAvailability.java

- Monitors the communication availability of Robot, Gaze, Affect, and LiDAR.
- Receives messages through the provided `Broker`.
- Tracks the most recent message time for each monitored source.
- Marks a source as `UNAVAILABLE` when more than one second passes without receiving data.
- Changes the source back to `AVAILABLE` when communication resumes.
- Displays the current status of all four sources using a Java Swing GUI.
- Records communication failures when a previously available source becomes unavailable.
- Stores the affected component and timestamp for each recorded communication failure.

### CommunicationFailure

- A Java record contained within `MonitorAvailability`.
- Represents a detected communication failure.
- Stores:
  - The affected component.
  - The timestamp when the failure was detected.

## Design Decisions

- Using the provided `Broker`
  - The assignment provides the communication infrastructure, so no additional socket or networking abstraction was created.

- Tracking the most recent timestamp for each source
  - Availability only depends on whether a source has communicated within the previous second.
  - Complete sensor messages do not need to be stored.

- Recording availability transitions
  - A communication failure is recorded only when a source changes from `AVAILABLE` to `UNAVAILABLE`.
  - This prevents the same outage from being recorded repeatedly while a source remains unavailable.
  - Sources that have never communicated are not recorded as communication failures when the application starts.

- Recording the affected component and timestamp
  - Each detected failure is stored as a `CommunicationFailure`.
  - The application maintains a history of recorded failures that can be accessed through `getCommunicationFailures()`.

- Using a background receiver thread
  - `Broker.receive()` waits for incoming messages.
  - Running message reception on a separate thread prevents it from blocking the Swing interface.

- Using a Swing timer
  - The GUI checks source availability every 100 milliseconds.
  - This allows a source to become `UNAVAILABLE` even when no new message arrives from that source.

- Using thread-safe collections
  - Message reception and GUI updates occur on different threads.
  - `ConcurrentHashMap` and `CopyOnWriteArrayList` are used so shared monitoring data can be safely accessed.

## Running Tests

1. Start `TestMonitorData.java`.
2. Start `MonitorAvailability.java`.
3. Verify that Robot, Gaze, Affect, and LiDAR become `AVAILABLE`.
4. Wait for the tester to simulate an outage.
5. Verify that the affected source becomes `UNAVAILABLE` after more than one second.
6. Verify that the other sources remain `AVAILABLE`.
7. Check the console for a recorded communication failure containing:
  - The affected source.
  - The timestamp of the failure.
8. Verify that only one failure is recorded for the outage.
9. Verify that the source returns to `AVAILABLE` when messages resume.
10. Continue running the tester and verify the same behavior for each monitored source.