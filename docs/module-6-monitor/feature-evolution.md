# Provide Recent Data Activity

The activity provider reuses `ActivityTracker` and `DataSource` from the
original display feature. The provider receives the existing `TYPE,data...`
messages, records their sources, and exposes one current activity snapshot
through both REST and MQTT. Neither communication adapter calculates activity.

## Shared Snapshot

`ProvideDataActivity` creates the shared `ActivitySnapshot` record from the
existing tracker. Provider and consumer code can import this same record, and
both outward interfaces serialize it identically:

```json
{
  "windowSeconds": 60,
  "generatedAt": "2026-09-27T20:15:30Z",
  "counts": {
    "ROBOT": 12,
    "GAZE": 24,
    "AFFECT": 6,
    "LIDAR": 15
  }
}
```

- `windowSeconds` is always `60` for the production provider.
- `generatedAt` is an ISO-8601 UTC timestamp.
- Every source is present, including sources with a zero count.
- Counts are nonnegative integers.
- Consumers should ignore additional fields they do not recognize.

## REST Contract

| Item | Value |
| --- | --- |
| Method | `GET` |
| Path | `/activity` |
| Default URL | `http://localhost:8080/activity` |
| Request body | None |
| Success | `200 OK` |
| Content type | `application/json; charset=utf-8` |
| Unsupported method | `405 Method Not Allowed` with `Allow: GET` |
| Provider failure | `500 Internal Server Error` with a JSON error object |

## MQTT Contract

| Item | Value |
| --- | --- |
| Broker | `tcp://broker.hivemq.com:1883` |
| Topic | `csc5100/activity/dylan/state` |
| Publisher | Provide Data Activity |
| Subscriber | Consume Data Activity |
| Payload | The shared snapshot JSON above, encoded as UTF-8 |
| Interval | One snapshot per second, including an immediate first snapshot |
| QoS | `1` |
| Retained | `true` |

Periodic publishing is required because counts can decrease when messages age
out of the 60-second window even if no new messages arrive. The retained state
lets a new consumer receive a snapshot immediately. A consumer can use
`generatedAt` to identify a stale retained snapshot.

MQTT request and response topics are not used. Activity is read-only state, so
one provider publishing state to subscribers is the simpler MQTT interaction.

## Runtime Structure

`ProvideDataActivity` owns one `ActivityTracker` and performs three runtime
responsibilities:

1. Its background receiver records recognized source messages.
2. `ActivityRestServer` answers `GET /activity` from the provider.
3. `ActivityMqttProvider` publishes snapshots from the provider.

`DisplayDataActivity` is preserved as the original standalone GUI and continues
to use `ActivityTracker` directly. REST and MQTT share the provider's tracker;
neither adapter contains a second activity calculation.

See [uml.png](uml.png) for the updated UML class diagram.

## Configuration

The provider defaults can be overridden with Java system properties:

| Property | Default |
| --- | --- |
| `activity.input.host` | `localhost` |
| `activity.input.port` | `5000` |
| `activity.rest.port` | `8080` |
| `activity.mqtt.broker` | `tcp://broker.hivemq.com:1883` |
| `activity.mqtt.topic` | `csc5100/activity/dylan/state` |
| `activity.mqtt.intervalMillis` | `1000` |

Provider and consumer must use the same MQTT broker and topic. The REST
consumer must use the provider machine's host, port, and `/activity` path.

## Verification

Compile and package the project:

```bash
mvn clean package
```

For a local end-to-end run, start the existing test-data source first:

```bash
mvn -q -Dexec.mainClass=edu.calpoly.TestMonitorData \
  -Dexec.classpathScope=test exec:java
```

Then start the provider in another terminal:

```bash
mvn -q -Dexec.mainClass=edu.calpoly.monitor.ProvideDataActivity exec:java
```

Query the REST interface:

```bash
curl -i http://localhost:8080/activity
```

The MQTT integration test requires the paired Consume Data Activity subscriber
to connect before or after the provider and verify the equivalent payload.
