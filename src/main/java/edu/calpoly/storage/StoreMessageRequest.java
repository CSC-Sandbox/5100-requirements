package edu.calpoly.storage;

/**
 * Wire representation of an incoming store request, used by both the REST
 * POST /messages body and the MQTT store-topic payload:
 *   {"message": "..."}
 *
 * Mirrors how Temperature.java is used directly as a JSON wire type in the
 * course-provided example - no separate JSON-building utility needed.
 *
 * @author Edgard Aviles
 */
public record StoreMessageRequest(String message) {
}
