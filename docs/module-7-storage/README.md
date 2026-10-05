# Store Messages: Design and Implementation

## Purpose

This feature receives messages from the provided `Broker`, associates each message with its receive timestamp, and persists the records to a file.

## Design

The implementation uses four classes:

- `StoreMessages` coordinates receiving messages and storing them.
- `MessageRecord` represents one timestamped message.
- `FileMessageStore` appends records to persistent storage.
- `MessageService` wraps `FileMessageStore`/`FileMessageReader` with `storeMessage(String)`/`retrieveMessages()`.

`StoreMessages` uses the provided `Broker` to receive messages. 
For each received message, it creates a `MessageRecord` using the 
current timestamp and passes that record to `FileMessageStore`.

`MessageRecord` gained `getTimestamp()`/`getMessage()` accessors and a `fromFileLine(String)` factory, 
so a stored line can be read back as an object instead of only ever being written. 
This is what lets the REST/MQTT interfaces below serialize stored messages to JSON.

`MessageService` exists so REST and MQTT aren't each reimplementing store/retrieve logic — 
both call the same two methods, and the timestamp is generated in one place.

## Storage Format

Each stored record is written as one line:

```text
timestamp,message
```

## REST Interface

`MessageRestServer` exposes `POST /messages`, accepting `{"message": "..."}` and delegating to `MessageService.storeMessage`. 
Invalid or malformed input returns `400` without crashing the server. Validated via `MessageValidator` (ROBOT/GAZE/AFFECT/LIDAR format rules).

- To store: `POST http://localhost:8080/messages` with body `{"message": "..."}`.
- To retrieve: `GET http://localhost:8080/messages`, parse the JSON array response.

## MQTT Interface

`MqttStorageEndpoint` subscribes to `csc5100/storage/store` on `tcp://broker.hivemq.com:1883`. 
Same payload shape and validation as REST. Must be running (`main()`) for a consumer to have anything to publish to.


# Retrieve Messages: Design and Implementation

## Purpose

This feature retrieves previously stored message records and sends each record through the provided Broker.

## Design

The implementation uses three classes:

- `FileMessageReader` reads all records from `data/messages.csv`.
- `RetrieveMessages` retrieves those records and sends each one through `Broker`.
- `MessageService.retrieveMessages()` is the shared entry point REST/MQTT use instead of calling `FileMessageReader` directly.

The provided `Broker` is not modified.

## Behavior

`FileMessageReader.readAllRecords()` returns the stored lines, 
parsed into `MessageRecord` objects via `MessageRecord.fromFileLine`, in file order. 
`RetrieveMessages` sends each returned line unchanged, preserving the timestamp, message content, and ordering from persistent storage.

If the storage file is empty **or does not exist yet** (nothing has ever been stored), 
the reader returns an empty list and `RetrieveMessages` sends no messages. 

## REST Interface

`MessageRestServer` exposes `GET /messages`, returning all stored records as a JSON array: 
`[{"timestamp": "...", "message": "..."}, ...]`, or `[]` if none exist.

## MQTT Interface

`MqttStorageEndpoint` subscribes to `csc5100/storage/retrieve/request` 
(any payload triggers retrieval) and publishes the JSON array result to `csc5100/storage/retrieve/response`.

## Testing

`TestRetrieveMessages.java` passed with the provided storage file, 
confirming that all records were sent unchanged and in order.

The empty-file case was also tested: the tester expected zero records and passed,
while `RetrieveMessages` exited without sending any messages.

