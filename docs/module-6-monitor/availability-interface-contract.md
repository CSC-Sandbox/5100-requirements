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

Publishing behavior:

The provider publishes a complete availability snapshot when it starts and publishes another complete snapshot whenever the availability state changes.

For example, if GAZE changes from AVAILABLE to UNAVAILABLE, the provider sends the complete four-source snapshot rather than only the changed source.

Messages are not retained.

## Shared Domain State

REST and MQTT do not calculate availability independently.

Both interfaces obtain their state from the same `MonitorAvailability` instance through `getAvailabilitySnapshot()`.

The existing timeout and message-receipt logic remains the source of truth for determining whether each source is AVAILABLE or UNAVAILABLE.