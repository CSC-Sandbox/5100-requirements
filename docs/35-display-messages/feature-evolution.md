# Feature Evolution Plan: Display Messages

### Current Features

Currently this Feature only Displays messages from any active broker without allowing the user to filter for specific host or MQTT topics.

## How Feature will evolve

This feature will become and logging system where a system admin can see what messages specific topics have
received as a MQTT topic. AS a REST endpoint it will be able to display over system statics not just specific messages going to and being received to topics.

- Be able to specify Which topics to monitor.
- Filter Messages by Host or Port.
- Follow a conversation between different Host.

### Contract

| Item             | Contract                             | Format               |
| ---------------- | ------------------------------------ | -------------------- |
| Input            | A Message                            | MQTT or HTTP request |
| Output           | A Message being displayed on the GUI | Other (A GUI Object) |
| Input Interface  | REST and MQTT TOPIC                  | N/A                  |
| Output Interface | REST and MQTT TOPIC                  | N/A                  |

## Dependencies

- This component will use a Message formatter to Display Messages.
- Main GUI component will use this feature.
- Main information that will cross this interface will be
