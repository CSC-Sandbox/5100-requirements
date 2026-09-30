# Availability Interface Contract

This document defines the communication contract between the Provide Availability Status and Consume Availability Status stories.

Both REST and MQTT expose the same availability state maintained by `MonitorAvailability`. Neither communication interface implements its own availability rules.

## Availability State

The system monitors four data sources:

- ROBOT
- GAZE
- AFFECT
- LIDAR

Each source has one of two availability values:

- AVAILABLE
- UNAVAILABLE

Both REST and MQTT use the same JSON representation.

Example:

```json
{
  "robot": "AVAILABLE",
  "gaze": "AVAILABLE",
  "affect": "UNAVAILABLE",
  "lidar": "AVAILABLE"
}
```

The field names are lowercase. Status values are uppercase strings.

## REST Contract

Base URL:

`http://localhost:8080`

Endpoint:

`GET /availability`

Full URL:

`http://localhost:8080/availability`

Successful response:

- HTTP status: `200 OK`
- Content-Type: `application/json`
- Body: complete availability snapshot

Example:

```json
{
  "robot": "AVAILABLE",
  "gaze": "AVAILABLE",
  "affect": "AVAILABLE",
  "lidar": "AVAILABLE"
}
```

Methods other than GET are not supported.

Unsupported methods return:

- HTTP status: `405 Method Not Allowed`
- `Allow: GET`
- Content-Type: `application/json`

Example error body:

```json
{
  "error": "Method not allowed"
}
```

REST Client behavior: The client sends a GET request to `/availability`, retrieves the response (if valid), and parses the JSON.<br>
For demonstration purposes, the running program for the REST API Client will periodically send GET requests and outputs the response.

## MQTT Contract

Broker:

`tcp://broker.hivemq.com:1883`

Topic:

`csc5100/availability/status`

QoS:

`0`

Payload:

The MQTT payload uses the same JSON structure as the REST response.

Example:

```json
{
  "robot": "AVAILABLE",
  "gaze": "UNAVAILABLE",
  "affect": "AVAILABLE",
  "lidar": "AVAILABLE"
}
```

Subscriber behavior:

The subscriber receives a complete availability snapshot from the provider whenever the provider sends the snapshot. This occurs when the availability state changes. <br>
For demonstration purposes, the subscriber will output the availability snapshot to the console.

## Shared Domain State

REST and MQTT do not calculate availability independently.

Both interfaces obtain their state from the same `MonitorAvailability` instance through `getAvailabilitySnapshot()`.

The existing timeout and message-receipt logic remains the source of truth for determining whether each source is AVAILABLE or UNAVAILABLE.