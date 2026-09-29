package edu.calpoly;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Locale;
import java.util.Random;

/**
 * Course-provided combined data generator for the monitoring module.
 *
 * @author Javier Gonzalez-Sanchez (javiergs)
 * @version 1.0 (2026-09-01)
 */
public class TestMonitorData {
  private static final int PORT = 5000;
  private static final long TICK_MS = 50, ROBOT_PERIOD_MS = 250, GAZE_PERIOD_MS = 100, AFFECT_PERIOD_MS = 500, LIDAR_PERIOD_MS = 200, NORMAL_DURATION_MS = 6000, OUTAGE_DURATION_MS = 4000;
  private static final Random RANDOM = new Random();

  private enum Source {ROBOT, GAZE, AFFECT, LIDAR}

  public static void main(String[] args) {
    System.out.println("TestMonitorData running on localhost:" + PORT);
    System.out.println("Run a Module 6 monitoring application and observe source status changes.");
    System.out.println("Messages use the common TYPE,data... contract.");
    try (ServerSocket server = new ServerSocket(PORT)) {
      while (true) {
        try (Socket socket = server.accept(); BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
          String mode = in.readLine();
          if (!"RECEIVE".equals(mode)) continue;
          System.out.println("Monitoring client connected. Sending combined data streams...");
          sendCombinedStreams(out);
        } catch (IOException e) {
          System.out.println("Monitoring client disconnected. Waiting for another connection...");
        }
      }
    } catch (IOException e) {
      System.err.println("Test server stopped: " + e.getMessage());
    }
  }

  private static void sendCombinedStreams(PrintWriter out) throws IOException {
    long start = System.currentTimeMillis(), nextRobot = start, nextGaze = start, nextAffect = start, nextLidar = start;
    int affectSample = 0, outageIndex = 0;
    Source lastOutage = null;
    while (!out.checkError()) {
      long now = System.currentTimeMillis(), elapsed = now - start, cycleLength = NORMAL_DURATION_MS + OUTAGE_DURATION_MS, cyclePosition = elapsed % cycleLength;
      Source outage = cyclePosition >= NORMAL_DURATION_MS ? Source.values()[outageIndex % Source.values().length] : null;
      if (outage != lastOutage) {
        if (lastOutage != null) {
          System.out.println(lastOutage + " resumed.");
          outageIndex++;
        }
        if (outage != null)
          System.out.println("Simulating outage: " + outage + " will stop for " + (OUTAGE_DURATION_MS / 1000) + " seconds.");
        lastOutage = outage;
      }
      if (now >= nextRobot) {
        if (outage != Source.ROBOT) out.println(robotMessage());
        nextRobot = now + ROBOT_PERIOD_MS;
      }
      if (now >= nextGaze) {
        if (outage != Source.GAZE) out.println(gazeMessage(now));
        nextGaze = now + GAZE_PERIOD_MS;
      }
      if (now >= nextAffect) {
        if (outage != Source.AFFECT) out.println(affectMessage(affectSample++));
        nextAffect = now + AFFECT_PERIOD_MS;
      }
      if (now >= nextLidar) {
        if (outage != Source.LIDAR) out.println(lidarMessage(now));
        nextLidar = now + LIDAR_PERIOD_MS;
      }
      out.flush();
      sleep();
    }
  }

  private static String robotMessage() {
    double[] v = new double[9];
    for (int i = 0; i < 6; i++) v[i] = -Math.PI + RANDOM.nextDouble() * 2 * Math.PI;
    for (int i = 6; i < 9; i++) v[i] = -1 + RANDOM.nextDouble() * 2;
    return String.format(Locale.US, "ROBOT,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f", v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8]);
  }

  private static String gazeMessage(long now) {
    double t = (now % 10000) / 10000.0, x = .5 + .45 * Math.sin(t * Math.PI * 2), y = .5 + .40 * Math.cos(t * Math.PI * 2);
    return String.format(Locale.US, "GAZE,%.2f,%.2f", x, y);
  }

  private static String affectMessage(int s) {
    double t = s * .20;
    return String.format(Locale.US, "AFFECT,%.2f,%.2f,%.2f,%.2f,%.2f", bounded(.55 + .30 * Math.sin(t)), bounded(.45 + .25 * Math.sin(t + 1)), bounded(.65 + .20 * Math.sin(t + 2)), bounded(.55 + .25 * Math.sin(t + 3)), bounded(.35 + .20 * Math.sin(t + 4)));
  }

  private static String lidarMessage(long now) {
    double a = (now % 8000) / 8000.0 * Math.PI * 2, r = 2.5 + .5 * Math.sin(a * 3);
    return String.format(Locale.US, "LIDAR,%.2f,%.2f,%.2f", r * Math.cos(a), r * Math.sin(a), .10 + .05 * Math.sin(a * 2));
  }

  private static double bounded(double v) {
    return Math.max(0, Math.min(1, v));
  }

  private static void sleep() throws IOException {
    try {
      Thread.sleep(TICK_MS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IOException("Monitoring simulation interrupted", e);
    }
  }
}
