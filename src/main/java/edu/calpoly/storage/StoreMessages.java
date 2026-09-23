package edu.calpoly.provided;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.time.Instant;

public class StoreMessages {
  public static void main(String[] args) {
    Broker broker = new Broker("localhost", 5000);
    FileMessageStore store = new FileMessageStore("data/stored-messages.csv");
    try {
      while (true) {
        String message = broker.receive();
        MessageRecord rec = new MessageRecord(Instant.now().toString(), message);
        
        store.store(rec);
      }
    } catch (IOException e) {
      System.out.println("Could not write file.");
    } catch (IllegalStateException e) {
    }
  }
}
