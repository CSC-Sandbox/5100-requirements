package edu.calpoly.provided;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class RetrieveMessages {
  public static void main(String[] args) {
    Broker broker = new Broker("localhost", 5000);

    try {
      List<String> records = Files.readAllLines(Path.of("data/empty-messages.csv"));
      for (String r : records) {
        if (!r.isBlank()) {
          broker.send(r);
        }
      }
    } catch (IOException e) {
      System.out.println("Could not read stored messages");
    }
  }
}
