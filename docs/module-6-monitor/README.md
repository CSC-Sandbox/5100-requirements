# Recent Data Activity Monitor

`DisplayDataActivity` is a Swing application that shows the number of Robot,
Gaze, Affect, and LiDAR messages received during a moving 60-second window.
The count labels and comparison bars refresh four times per second, including
when no new messages arrive, so expired messages disappear automatically.

## Design

The implementation is in `edu.calpoly.monitor` and uses three classes:

- `DisplayDataActivity` builds the Swing interface, receives messages from the
  course-provided `Broker` on a daemon thread, and routes each recognized first
  field to the tracker. A Swing timer refreshes all four displayed counts every
  250 ms, including when no new messages arrive.
- `ActivityTracker` maintains one timestamp queue per `DataSource`. Its
  synchronized API lets the receiver thread record messages while Swing reads
  snapshots safely. Both recording and snapshot creation discard timestamps
  older than 60 seconds.
- `DataSource` defines Robot, Gaze, Affect, and LiDAR and parses the source name
  from the first message field.

The provided `Broker` class is used without modification.

## Build and test with the provided simulator

Java 17 and Maven are required. Compile the project from the repository root:

```bash
mvn package
```

In one terminal, start the course-provided data simulator and leave it running:

```bash
java -cp target/classes edu.calpoly.provided.TestMonitorData
```

In a second terminal, start the activity dashboard:

```bash
java -cp target/classes edu.calpoly.monitor.DisplayDataActivity
```

The four counts should rise at different rates. After the application has run
for at least 60 seconds, a simulated source outage causes that source's count
to fall as its older messages leave the window. Once the source resumes, its
new messages are included automatically; its count may initially remain level
while equally old messages expire. No manual refresh is needed.
