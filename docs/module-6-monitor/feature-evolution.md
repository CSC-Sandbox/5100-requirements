# Recent Data Activity: Feature Evolution Plan

This plan covers `ActivityTracker`, `DataSource`, and `DisplayDataActivity`.
The changes described below are for a future stage and are not implemented yet.

## What the Feature Will Become

The feature should become an independent service using both MQTT and REST.
MQTT fits the continuous Robot, Gaze, Affect, and LiDAR event streams, while
REST gives the Swing display and future clients a simple way to request the
latest activity counts.

Running the tracker separately from the display allows it to collect activity
when no UI is open and lets multiple clients use the same snapshot. The
existing `ActivityTracker` remains the domain logic; MQTT and REST become
replaceable communication adapters.

## Contract

| Interaction | Input and format | Input interface | Output and format | Output interface |
| --- | --- | --- | --- | --- |
| Receive telemetry | One Robot, Gaze, Affect, or LiDAR event as an MQTT message with a UTF-8 `TYPE,data...` payload | MQTT topic `csc5100/telemetry/{source}`; the service subscribes to `csc5100/telemetry/+` | One event recorded for the identified source; no response message | Java call to `ActivityTracker.record(DataSource)` |
| Read activity | HTTP `GET` request with no body | REST endpoint `GET /api/v1/activity` | Window length, snapshot time, and all source counts as JSON | `200 OK` REST response with `Content-Type: application/json` |
| Update display | Activity JSON converted to a Java snapshot object | `ActivityClient.getSnapshot()` in `DisplayDataActivity` | Updated labels, progress bars, and connection status | Local Swing UI |

Example telemetry input:

```text
Topic:   csc5100/telemetry/gaze
Payload: GAZE,0.35,0.72
```

`DataSource` validates the first payload field and confirms that it matches the
topic source. Blank, unknown, or mismatched messages are logged and ignored.
The remaining values are not interpreted because this feature tracks message
activity, not sensor contents. MQTT should use QoS 0 with no retained messages:
these events are frequent and short-lived, and a redelivery would inflate the
count.

Example REST response:

```json
{
  "windowSeconds": 60,
  "generatedAt": "2026-09-25T22:00:00Z",
  "counts": {
    "ROBOT": 240,
    "GAZE": 600,
    "AFFECT": 120,
    "LIDAR": 300
  }
}
```

Every source appears even when its count is zero. `generatedAt` is an ISO 8601
UTC timestamp, and counts are nonnegative integers. The REST adapter gets this
data from `ActivityTracker.snapshot()`. A server failure returns `500 Internal
Server Error` with a JSON error object.

`DisplayDataActivity` polls through `ActivityClient` rather than connecting
directly to the broker. Network requests must run outside Swing's event-dispatch
thread. If a request fails, the UI keeps the last snapshot and marks it as
stale.

## Dependencies

### Features and services used

- **Robot, Gaze, Affect, and LiDAR producers** publish their existing telemetry
  messages to the matching MQTT topics. Only the source identity crosses into
  the activity domain.
- **MQTT broker** routes those messages to the service. Its connection settings
  should be configurable rather than hard-coded.
- **Time source** determines when events leave the rolling window. Keeping an
  injectable clock makes expiration behavior testable.

### Consumers of this feature

- **`DisplayDataActivity`** reads the REST snapshot and renders message counts.
- Future dashboards may use the same read-only endpoint, but no other current
  feature is required to depend on it.

### Replaceable interfaces

- `ActivityEventSource` converts MQTT input into `DataSource` events, allowing
  another message transport to replace MQTT without changing the tracker.
- `ActivitySnapshotProvider` exposes tracker snapshots to the REST adapter,
  allowing another storage implementation later.
- `ActivityClient` hides REST details from `DisplayDataActivity`, allowing tests
  to use an in-memory client without changing the Swing code.
