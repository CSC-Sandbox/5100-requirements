package edu.calpoly.storage;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FileMessageStore {
  private final String fileName;

  public FileMessageStore(String fileName) {
    this.fileName = fileName;
  }

  public void store(MessageRecord record) throws IOException {
    try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, true))) {
      writer.println(record.toFileLine());
    }
  }
}
