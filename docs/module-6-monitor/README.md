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
- Exposes the current availability state through `getAvailabilitySnapshot()`.
- Provides a reusable `startReceiving()` method so REST and MQTT providers can use the same monitoring instance.

### DataSource.java

- Represents the supported monitored data sources:
  - `ROBOT`
  - `GAZE`
  - `AFFECT`
  - `LIDAR`
- Provides display names for the GUI.
- Parses incoming Broker messages and identifies the corresponding data source.
- Prevents duplicated raw source-name handling throughout the availability feature.

### CommunicationFailure

- A Java record contained within `MonitorAvailability`.
- Represents a detected communication failure.
- Stores:
  - The affected `DataSource`.
  - The timestamp when the failure was detected.

### AvailabilityRestServer.java

- Provides the current availability state through a REST interface.
- Reuses the existing `MonitorAvailability` state instead of calculating availability separately.
- Exposes:

`GET http://localhost:8080/availability`

- Returns the complete current availability snapshot as JSON.

Example response:

```json
{
  "robot": "AVAILABLE",
  "gaze": "AVAILABLE",
  "affect": "UNAVAILABLE",
  "lidar": "AVAILABLE"
}
```

- Returns `405 Method Not Allowed` for unsupported HTTP methods.
- Includes `Allow: GET` in the response for unsupported methods.

### AvailabilityMqttProvider.java

- Provides the current availability state through MQTT.
- Reuses the same `MonitorAvailability` instance used by the existing monitoring logic.
- Connects to:

`tcp://broker.hivemq.com:1883`

- Publishes to:

`csc5100/availability/status`

- Uses QoS `0`.
- Publishes a complete availability snapshot when the provider starts.
- Publishes another complete snapshot whenever the availability state changes.
- Uses the same JSON structure as the REST interface.
- Messages are not retained.

Example payload:

```json
{
  "robot": "AVAILABLE",
  "gaze": "UNAVAILABLE",
  "affect": "AVAILABLE",
  "lidar": "AVAILABLE"
}
```

## Design Decisions

- Using the provided `Broker`
  - The assignment provides the communication infrastructure, so no additional socket or networking abstraction was created.

- Reusing `DataSource`
  - `DataSource` is used as the shared representation of Robot, Gaze, Affect, and LiDAR.
  - Incoming messages are parsed through `DataSource.fromMessage()` rather than duplicating source-name parsing logic.

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
  - `startReceiving()` allows the same monitor instance to be reused by REST and MQTT providers.

- Using a Swing timer
  - The GUI checks source availability every 100 milliseconds.
  - This allows a source to become `UNAVAILABLE` even when no new message arrives from that source.

- Using thread-safe collections
  - Message reception, GUI updates, REST requests, and MQTT publishing may access monitoring state from different threads.
  - `ConcurrentHashMap` and `CopyOnWriteArrayList` are used so shared monitoring data can be safely accessed.

- Using one source of availability logic
  - `MonitorAvailability` remains the source of truth for determining whether each source is available.
  - REST and MQTT do not contain their own timeout or availability calculations.
  - Both communication adapters obtain current state through `getAvailabilitySnapshot()`.

- Using equivalent REST and MQTT payloads
  - REST and MQTT expose the same four source names and the same `AVAILABLE` / `UNAVAILABLE` values.
  - This keeps the provider and consumer stories interoperable across both communication methods.

## REST and MQTT Interface Contract

The full provider/consumer communication contract is documented in:

`availability-interface-contract.md`

### REST

Method:

`GET`

Endpoint:

`/availability`

Full URL:

`http://localhost:8080/availability`

Successful response:

- HTTP `200 OK`
- `Content-Type: application/json`
- Complete availability snapshot

Unsupported methods:

- HTTP `405 Method Not Allowed`
- `Allow: GET`

### MQTT

Broker:

`tcp://broker.hivemq.com:1883`

Topic:

`csc5100/availability/status`

QoS:

`0`

Publishing behavior:

- Publish the complete state when the provider starts.
- Publish another complete state whenever availability changes.
- Messages are not retained.

## Running Tests

### Original Availability Monitor

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

### REST Provider

1. Start `TestMonitorData.java`.
2. Start `AvailabilityRestServer.java`.
3. Request the current state:

```bash
curl http://localhost:8080/availability
```

4. Verify that the response contains all four monitored sources.
5. Repeat the request while `TestMonitorData` simulates outages.
6. Verify that `AVAILABLE -> UNAVAILABLE` transitions appear in the REST response.
7. Verify that the source returns to `AVAILABLE` when communication resumes.
8. Verify unsupported methods:

```bash
curl -i -X POST http://localhost:8080/availability
```

9. Verify that the response is `405 Method Not Allowed` with `Allow: GET`.

### MQTT Provider

1. Start `TestMonitorData.java`.
2. Start an MQTT subscriber for:

`csc5100/availability/status`

3. Start `AvailabilityMqttProvider.java`.
4. Verify that the initial complete availability snapshot is published.
5. Wait for `TestMonitorData` to simulate an outage.
6. Verify that another complete snapshot is published with the affected source set to `UNAVAILABLE`.
7. Verify that another snapshot is published when the source returns to `AVAILABLE`.

A command-line subscriber can be run with:

```bash
mosquitto_sub -h broker.hivemq.com -p 1883 -t csc5100/availability/status -v
```

## Integration Testing

The provider implementation was tested with the paired Consume Availability Status story.

REST integration verified that:

- The consumer successfully called `GET /availability`.
- The consumer parsed and displayed Robot, Gaze, Affect, and LiDAR.
- Availability changes were reflected correctly.
- Sources returned to `AVAILABLE` when communication resumed.

MQTT integration verified that:

- The consumer successfully subscribed to `csc5100/availability/status`.
- The consumer received the shared JSON availability payload.
- `AVAILABLE -> UNAVAILABLE` transitions were received correctly.
- `UNAVAILABLE -> AVAILABLE` recovery transitions were received correctly.

Both REST and MQTT use the same underlying availability state and shared communication contract.

## Documentation

- `README.md` - design, implementation, and testing information.
- `availability-interface-contract.md` - REST and MQTT provider/consumer contract.
- `monitor-availability-uml.png` - rendered Sprint 2 UML diagram.