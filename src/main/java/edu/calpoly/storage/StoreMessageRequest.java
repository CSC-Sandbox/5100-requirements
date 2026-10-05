package edu.calpoly.storage;

/**
 * Wire representation of an incoming store request, used by both the REST
 * POST /messages body and the MQTT store-topic payload:
 *   {"message": "..."}
 *
 * Mirrors how Temperature.java is used directly as a JSON.
 *
 * @author Edgard Aviles
 */
public record StoreMessageRequest(String message) {
}
