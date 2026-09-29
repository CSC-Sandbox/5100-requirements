package edu.calpoly.storage;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/*
 * This class is implemented to store the messages received
 * It is responsible to write/store a message record once received.
 *
 * @author Edgard Aviles
 * @version September 25, 2026
 */

public class FileMessageStore {
  private final String fileName;

  /*
   * @param fileName name of the file we are writing to
   */
  public FileMessageStore(String fileName) {
    this.fileName = fileName;
  }

  /*
   * @param record the message we want to store
   */
  public void store(MessageRecord record) throws IOException {
    try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, true))) {
      writer.println(record.toFileLine());
    }
  }
}
