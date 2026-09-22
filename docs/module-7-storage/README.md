# Retrieve Messages: Design and Implementation

## Purpose

This feature retrieves previously stored message records and sends each record through the provided Broker.

## Design

The implementation uses two classes:

- `FileMessageReader` reads all records from `data/messages.csv`.
- `RetrieveMessages` retrieves those records and sends each one through `Broker`.

The provided `Broker` is not modified.

## Behavior

`FileMessageReader` returns the stored lines in file order. `RetrieveMessages` sends each returned line unchanged, preserving the timestamp, message content, and ordering from persistent storage.

If the storage file is empty, the reader returns an empty list and `RetrieveMessages` sends no messages.

## Testing

`TestRetrieveMessages.java` passed with the provided storage file, confirming that all records were sent unchanged and in order.

The empty-file case was also tested: the tester expected zero records and passed, while `RetrieveMessages` exited without sending any messages.
