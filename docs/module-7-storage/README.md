# Store Messages: Design and Implementation

## Purpose

This feature receives messages from the provided `Broker`, associates each message with its receive timestamp, and persists the records to a file.

## Design

The implementation uses three classes:

- `StoreMessages` coordinates receiving messages and storing them.
- `MessageRecord` represents one timestamped message.
- `FileMessageStore` appends records to persistent storage.

`StoreMessages` uses the provided `Broker` to receive messages. For each received message, it creates a `MessageRecord` using the current timestamp and passes that record to `FileMessageStore`.

## Storage Format

Each stored record is written as one line:

```text
timestamp,message
