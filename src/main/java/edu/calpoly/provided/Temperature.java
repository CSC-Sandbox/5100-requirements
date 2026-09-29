package edu.calpoly.provided;

/**
 * Simple domain object used by the course-provided REST/MQTT example.
 *
 * @author Javier Gonzalez-Sanchez
 * @version 1.0 (2026-09-25)
 */
public record Temperature(double value, String unit) {
}
