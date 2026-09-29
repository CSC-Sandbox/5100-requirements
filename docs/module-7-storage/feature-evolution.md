# Feature Evolution and Refactoring Plan


### Improve error and status reporting

The current programs report conditions such as file-read errors or empty storage through console output.

In future work, the feature should return structured success, failure, or status information.
This would allow calling code to distinguish between successful 
retrieval, empty storage, file failures, and communication failures without depending on printed text.

### Abstract Broker communication

The feature currently depends directly on the provided `Broker` class. 
In future work, a small `MessageTransport` interface could define the 
operations this feature needs, such as sending and receiving messages.

A `BrokerTransport` implementation could adapt the provided `Broker` to this interface. 
This would allow another transport mechanism, such as MQTT, REST, or a test fake, to be 
substituted later without changing message-storage logic.

### Abstract file storage

The current implementation directly uses CSV files for persistent storage. 
In future work, a `MessageRepository` interface could hide that implementation detail.

For example:

```java
void append(MessageRecord record);
List<MessageRecord> readAll();
```

A file-based implementation could continue using CSV files, 
while a later implementation could use a database or cloud storage. 
The rest of the system would depend on the repository interface rather than a specific file format.

### Use MessageRecord as the shared data contract

Retrieval currently sends complete stored lines to preserve timestamps and message content exactly. 
In future work, both storage and retrieval should use `MessageRecord` as the main Java data object.

This would make the timestamp and payload explicit and prevent other classes from depending 
directly on the CSV representation. A separate serialization component could later 
convert between `MessageRecord` objects and stored file lines.

## Future Form of the Feature

The feature should evolve into both a **MQTT / REST** service used directly by other Java code.

This choice makes sense because as a mqtt service it will allow to digest live events eventually,
not just messages. Also as REST it could allow developers to retrieve history, of the events.

This allows asynchronous actions and developers to ensure persistence.

## Future Contract

### Persist a message

| Item             | Contract                                                   |
| ---------------- | ---------------------------------------------------------- |
| Input            | `MessageRecord` containing a timestamp and message payload |
| Input format     | Java object                                                |
| Input interface  | `MessageRepository.append(MessageRecord record)`           |
| Output           | Successful durable storage or a storage failure            |
| Output interface | Method completion or a structured result                   |

### Retrieve stored messages

| Item             | Contract                                                         |
| ---------------- | ---------------------------------------------------------------- |
| Input            | Optional retrieval options, such as a time range or record limit |
| Input format     | Java object                                                      |
| Input interface  | `MessageRepository.readAll()` or a future query method           |
| Output           | Ordered `List<MessageRecord>`                                    |
| Output format    | Java objects                                                     |
| Output interface | Return value                                                     |

Returned records must preserve their original timestamp, message content, and storage order.

### Send retrieved messages externally

| Item             | Contract                                 |
| ---------------- | ---------------------------------------- |
| Input            | A serialized message record              |
| Input format     | String or future JSON message            |
| Input interface  | `MessageTransport.send(String message)`  |
| Output           | Delivery success or delivery failure     |
| Output interface | Method completion or a structured result |

The current `Broker` is the initial transport implementation. 
A later `MessageTransport` implementation could use a different communication mechanism if needed.

## Dependencies

| Feature relationship                                 | Information crossing the interface                                                        |
| ---------------------------------------------------- | ----------------------------------------------------------------------------------------- |
| Storage feature uses persistent storage              | Timestamped message records are written to and read from a local file.                    |
| Retrieval feature uses storage feature               | Ordered stored records are retrieved for transmission.                                    |
| Store and retrieve features use Broker communication | Incoming message text is received and stored; retrieved records are sent outward.         |
| Other future Java components use this feature        | Components persist and retrieve `MessageRecord` objects through the repository interface. |

`MessageRepository` abstraction would allow the file-based implementation to be replaced later. 
`MessageTransport` abstraction would allow the `Broker` to be replaced 
if the system later requires another communication mechanism.

