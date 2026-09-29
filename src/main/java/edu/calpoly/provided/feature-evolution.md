# Feature Evolution Plan: Affective State Monitor

## Current Feature

The current `DisplayAffect` application receives `AFFECT` messages through the provided `Broker` class and displays five affective values—focus, excitement, engagement, interest, and stress—in a real-time chart.

## What the Feature Will Become

This feature should become a service that runs independently from the rest of the system.

It should use MQTT for its main communication mechanism. Affective data is continuous, real-time data, so MQTT is a good fit because publishers can send updates to a topic and multiple subscribers can receive them without being directly connected to one another.

A REST interface could be added later for configuration or retrieving saved historical data, but MQTT is the primary need for receiving live affect data.

## Contract

### Receive affective-state updates

| Item | Contract |
|---|---|
| Input interface | MQTT topic |
| Proposed topic | `affect/state` |
| Input format | MQTT message containing JSON |
| Example input | `{"focus":0.72,"excitement":0.44,"engagement":0.65,"interest":0.58,"stress":0.33}` |
| Output | Updated chart, current-value cards, sample count, and elapsed-time display |
| Output interface | Local GUI display |

The service will validate that all five affect values are present and are within the range `0.0` through `1.0`. Invalid messages will be ignored and may later be logged for troubleshooting.

### Optional future status interface

| Item | Contract |
|---|---|
| Input interface | MQTT connection status |
| Input format | Connection or error event |
| Output | Status text in the GUI |
| Output interface | Local GUI display |

For example, the application can show whether it is connected, disconnected, or receiving invalid data.

## Dependencies

The feature currently depends on:

- The provided `Broker` class to receive messages.
- JFreeChart to create the real-time line chart.
- Java Swing to provide the graphical interface.

In the next stage, the feature will depend on an MQTT client library instead of directly depending on the provided Broker implementation.

Other features that may use this feature include:

- A sensor or affect-analysis feature that publishes affect values.
- A robot-control feature that may react to the user's affective state.
- A data-storage feature that records affect values over time.
- A dashboard feature that displays affect data to users or developers.

The information crossing these interfaces is the current affective state: focus, excitement, engagement, interest, and stress.

## Future Refactoring Opportunities

### Separate message parsing from the user interface

`DisplayAffect` currently handles both receiving/parsing messages and displaying them. In the future, message parsing should move into a separate class, such as `AffectMessageParser`.

This would make the code easier to test because message validation could be tested without opening the GUI.

### Create an AffectData model class

The five values are currently passed as five separate `double` parameters. A class such as `AffectData` should store these values together.

For example:

```java
public record AffectData(
        double focus,
        double excitement,
        double engagement,
        double interest,
        double stress
) { }