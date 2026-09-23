package edu.calpoly.provided;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Random;

/**
 * Course-provided message simulator used to test DisplayMessages.java.
 *
 * @author Javier Gonzalez-Sanchez (javiergs)
 * @version 1.0 (2026-09-01)
 */
public class TestDisplayMessages {

  private static final int PORT = 5000, MIN_DELAY_MS = 1000, MAX_DELAY_MS = 3000;
  private static final Random RANDOM = new Random();
  private static final List<String> MESSAGES = List.of("Hello!", "How are you?", "CSC 5100", "Software Engineering", "Testing the chat...", "Requirements become software.", "One message at a time.", "Message received!");

  public static void main(String[] args) {
    System.out.println("TestDisplayMessages running on localhost:" + PORT);
    System.out.println("Run DisplayMessages.java and observe the message list update.");
    try (ServerSocket server = new ServerSocket(PORT)) {
      while (true) {
        try (Socket socket = server.accept(); BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
          String mode = in.readLine();
          if (!"RECEIVE".equals(mode)) continue;
          System.out.println("DisplayMessages connected. Sending simulated messages...");
          int i = 0;
          while (!out.checkError()) {
            String m = MESSAGES.get(i % MESSAGES.size());
            out.println(m);
            out.flush();
            System.out.println("Sent: " + m);
            i++;
            sleepRandomly();
          }
        } catch (IOException e) {
          System.out.println("DisplayMessages disconnected. Waiting for another connection...");
        }
      }
    } catch (IOException e) {
      System.err.println("Test server stopped: " + e.getMessage());
    }
  }

  private static void sleepRandomly() throws IOException {
    int d = MIN_DELAY_MS + RANDOM.nextInt(MAX_DELAY_MS - MIN_DELAY_MS + 1);
    try {
      Thread.sleep(d);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IOException("Message simulation interrupted", e);
    }
  }
}
