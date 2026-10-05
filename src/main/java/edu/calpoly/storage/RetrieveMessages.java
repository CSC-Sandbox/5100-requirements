package edu.calpoly.storage;

import edu.calpoly.provided.Broker;

import java.io.IOException;
import java.util.List;

/*
 * This class reads from the stored messages
 * and sends them to MQTT. It makes sure to 
 * alert of no messages at all.
 *
 * @author Edgard Aviles
 * @version September 25, 2026
 */

public class RetrieveMessages {
  private final FileMessageReader fileReader;
  public RetrieveMessages(FileMessageReader fileReader) {
    this.fileReader = fileReader;
  }

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

  /**
   * @return All lines read of opened {@link FileMessageReader} file.
   * @throws IOException
   */
  public List<String> readAllLines() throws IOException{
    return fileReader.readAll();
  }
}
