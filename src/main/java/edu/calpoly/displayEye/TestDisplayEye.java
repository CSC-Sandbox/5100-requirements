package edu.calpoly.displayEye;

import java.io.*;
import java.net.*;
import java.util.Locale;

/**
 * Course-provided gaze simulator used to test DisplayEye.java.
 */
public class TestDisplayEye {
    private static final int PORT = 5000, STEPS_X = 20, ROWS = 6;
    private static final long DELAY_MS = 150;

    public static void main(String[] args) {
        System.out.println("TestDisplayEye running on localhost:" + PORT);
        System.out.println("Run DisplayEye.java and observe the gaze point.");
        try (ServerSocket server = new ServerSocket(PORT)) {
            while (true) {
                try (Socket socket = server.accept(); BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                    String mode = in.readLine();
                    if (!"RECEIVE".equals(mode)) continue;
                    System.out.println("DisplayEye connected. Sending test sequence...");

                    // Choose one to test
                    // sendSnake(out);
                    sendInvalidTests(out);
                } catch (IOException e) {
                    System.out.println("DisplayEye disconnected. Waiting for another connection...");
                }
            }
        } catch (IOException e) {
            System.err.println("Test server stopped: " + e.getMessage());
        }
    }

    // Base snake taken from provided
    private static void sendSnake(PrintWriter out) throws IOException {
        while (!out.checkError()) {
            for (int row = 0; row < ROWS; row++) {
                double y = (double) row / (ROWS - 1);
                boolean ltr = row % 2 == 0;
                for (int step = 0; step <= STEPS_X; step++) {
                    double x = (double) step / STEPS_X;
                    if (!ltr) x = 1.0 - x;
                    out.printf(Locale.US, "GAZE,%.2f,%.2f%n", x, y);
                    out.flush();
                    sleep();
                }
            }
        }
    }

    // Test sending invalid messages (Recommended to turn debug on in DisplayEye.java to see logs reacting to the messages)
    private static void sendInvalidTests(PrintWriter out) throws IOException {
        while (!out.checkError()) {
            // Not type "GAZE" (should be ignored by broker)
            for (int step = 0; step <= 4; step++) {
                double x = (double) step / 4;
                out.printf(Locale.US, "GAME,%.2f,%.2f%n", x, 0.0);
                out.flush();
                sleep();
            }

            // X not a number (should be ignored by broker)
            for (int step = 0; step <= 4; step++) {
                double x = (double) step / 4;
                out.printf(Locale.US, "GAZE,a%.2f,%.2f%n", x, 0.0);
                out.flush();
                sleep();
            }

            // Y not a number (should be ignored by broker)
            for (int step = 0; step <= 4; step++) {
                double y = (double) step * 2 - 4.5;
                out.printf(Locale.US, "GAZE,%.2f,%.2fa%n", 0.0, y);
                out.flush();
                sleep();
            }

            // X not within 0.0-1.0 (should be ignored by broker)
            for (int step = 0; step <= 4; step++) {
                double x = (double) step * 2 - 4.5;
                out.printf(Locale.US, "GAZE,%.2f,%.2f%n", x, 0.0);
                out.flush();
                sleep();
            }

            // Y not within 0.0-1.0 (should be ignored by broker)
            for (int step = 0; step <= 4; step++) {
                double y = (double) step * 2 - 4.5;
                out.printf(Locale.US, "GAZE,%.2f,%.2f%n", 0.0, y);
                out.flush();
                sleep();
            }

            // Extra argument (should be ignored by broker)
            for (int step = 0; step <= 4; step++) {
                double y = (double) step * 2 - 6.5;
                out.printf(Locale.US, "GAZE,%.2f,%.2f,2%n", 0.0, y);
                out.flush();
                sleep();
            }
        }
    }

    private static void sleep() throws IOException {
        try {
            Thread.sleep(DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Test sequence interrupted", e);
        }
    }
}
