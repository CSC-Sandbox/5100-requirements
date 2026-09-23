package edu.calpoly.provided;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Course-provided LiDAR simulator used to test DisplayLidar.java.
 *
 * @author Javier Gonzalez-Sanchez (javiergs)
 * @version 1.0 (2026-09-01)
 */
public class TestDisplayLidar {

  private static final int PORT = 5000;
  private static final long DELAY_MS = 35;

  private record Point(double x, double y, double z) {
  }

  public static void main(String[] args) {
    System.out.println("TestDisplayLidar running on localhost:" + PORT);
    System.out.println("Run DisplayLidar.java and observe the 2D LiDAR map.");
    List<Point> scan = createScan();
    System.out.println("Simulated scan contains " + scan.size() + " points.");
    try (ServerSocket server = new ServerSocket(PORT)) {
      while (true) {
        try (Socket socket = server.accept(); BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
          String mode = in.readLine();
          if (!"RECEIVE".equals(mode)) continue;
          System.out.println("DisplayLidar connected. Sending simulated LiDAR points...");
          sendScan(out, scan);
        } catch (IOException e) {
          System.out.println("DisplayLidar disconnected. Waiting for another connection...");
        }
      }
    } catch (IOException e) {
      System.err.println("Test server stopped: " + e.getMessage());
    }
  }

  private static void sendScan(PrintWriter out, List<Point> scan) throws IOException {
    while (!out.checkError()) for (Point p : scan) {
      out.printf(Locale.US, "LIDAR,%.2f,%.2f,%.2f%n", p.x(), p.y(), p.z());
      out.flush();
      sleep();
      if (out.checkError()) return;
    }
  }

  private static List<Point> createScan() {
    List<Point> p = new ArrayList<>();
    addH(p, -4, 4, 4);
    addV(p, 4, -4, 4);
    addH(p, 4, -4, -4);
    addV(p, -4, 4, -4);
    addH(p, -2.2, -.7, 1);
    addV(p, -.7, -.9, 1);
    addH(p, -.7, -2.2, -.9);
    addV(p, -2.2, 1, -.9);
    addV(p, 1.1, .3, 2.8);
    addH(p, 1.5, 2.6, -1.9);
    addV(p, 2.6, -1.9, -.9);
    return p;
  }

  private static void addH(List<Point> p, double a, double b, double y) {
    double s = a <= b ? .10 : -.10;
    for (double x = a; s > 0 ? x <= b + 1e-9 : x >= b - 1e-9; x += s) p.add(new Point(x, y, zFor(p.size())));
  }

  private static void addV(List<Point> p, double x, double a, double b) {
    double s = a <= b ? .10 : -.10;
    for (double y = a; s > 0 ? y <= b + 1e-9 : y >= b - 1e-9; y += s) p.add(new Point(x, y, zFor(p.size())));
  }

  private static double zFor(int i) {
    return .10 + .05 * Math.sin(i * .15);
  }

  private static void sleep() throws IOException {
    try {
      Thread.sleep(DELAY_MS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IOException("LiDAR simulation interrupted", e);
    }
  }
}
