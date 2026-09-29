package edu.calpoly.storage;

import edu.calpoly.provided.Broker;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.time.Instant;

/*
 * This class runs the entire program of storing
 * the incoming messages. It patiently awaits
 * to receive the messages to store.
 *
 * @author Edgard Aviles
 * @version September 25, 2026
 */

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
