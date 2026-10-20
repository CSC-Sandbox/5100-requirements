# Feature Evolution Plan: Enter Messages

## Current Features

The current feature accepts a message and send it over a Specified broker which is given to the
component as a Dependency, The Component does not send the message if it is empty of blank.

- Detects if a message is empty and does not send to the broker.
- Sends Messages to a Given Broker.
- Broker must be setup and running before the Dialog Box is loaded.
-

# What the Feature will Become

This feature may become an independent part of the system as it would be nice to be able to specify where
messages are going, along with spawning new brokers in the case that a new server should be connected to system.

- Be able to specify the Broker which you want to send the message too.
- If port or host are not already in spawned host allow user to spawn a new Broker instance.
- Be able to Send Specific Message Types over broker.
- Allow user to specify multiple Brokers to send messages to.

This feature may become either a REST or MQTT service as you may be sending messages directly to the server as a REST service,
or you may be sending instructions to subscribers by MQTT.

## Contract

### Send Message (MQTT message | HTTP request / response | File)

| Item             | Contract          |
| ---------------- | ----------------- |
| Input            | A Message to Send |
| Input Interface  | Java Method       |
| Output           | None              |
| Output Interface | None              |

## Dependencies

### Current Dependencies

- An active `Broker` Instance.
- Java Swing to provide GUI component.
-
- Potential Abstraction would be to specify a Validator
  to make sure messages are valid, according to a specific implementation.

