package edu.calpoly.student;

import edu.calpoly.provided.Broker;

import java.util.HashMap;
import java.util.Map;

public class MonitorAvailability {

    private static final long TIMEOUT_MS = 1000;

    private final Map<String, Long> lastReceived = new HashMap<>();

    public MonitorAvailability() {
        lastReceived.put("ROBOT", 0L);
        lastReceived.put("GAZE", 0L);
        lastReceived.put("AFFECT", 0L);
        lastReceived.put("LIDAR", 0L);
    }

    public void recordMessage(String source) {
        if (lastReceived.containsKey(source)) {
            lastReceived.put(source, System.currentTimeMillis());
        }
    }
    public boolean isAvailable(String source) {
        long lastTime = lastReceived.get(source);

        if (lastTime == 0) {
            return false;
        }

        return System.currentTimeMillis() - lastTime <= TIMEOUT_MS;
    }
}
