package edu.calpoly.provided;

/**
 * Core/domain functionality for the temperature example.
 *
 * Both REST and MQTT adapters use this same service. The communication
 * mechanisms do not contain a second copy of the domain logic.
 *
 * @author Javier Gonzalez-Sanchez
 * @version 1.0 (2026-09-25)
 */
public class TemperatureService {

    private Temperature temperature = new Temperature(72.5, "F");

    public synchronized Temperature getTemperature() {
        return temperature;
    }

    public synchronized void setTemperature(Temperature temperature) {
        if (temperature == null || temperature.unit() == null || temperature.unit().isBlank()) {
            throw new IllegalArgumentException("Temperature and unit are required");
        }
        this.temperature = temperature;
    }
}
