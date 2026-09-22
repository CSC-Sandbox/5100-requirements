package edu.calpoly.provided;

import java.io.IOException;
import java.util.List;

public class RetrieveMessages {
  public static void main(String[] args) {
    Broker broker = new Broker("localhost", 5000);
    FileMessageReader reader = new FileMessageReader("data/empty-messages.csv");

    try {
      List<String> records = reader.readAll();
      boolean sentAny = false;

      for (String r : records) {
        if (!r.isBlank()) {
          broker.send(r);
          sentAny = true;
        }
      }
      if (!sentAny) {
        System.out.println("No stored messages found");
      }
    } catch (IOException e) {
      System.out.println("Could not read stored messages");
    }
  }
}
