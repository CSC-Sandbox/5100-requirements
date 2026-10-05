package edu.calpoly.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * This class reads the entire file storing messages in one instance
 * It is responsible for just reading our message file
 *
 * @author Edgard Aviles
 * @version September 25, 2026
 */
public class FileMessageReader {

  private String fileName;
  
  // @param fileName name of the file holding the messages
  public FileMessageReader(String fileName) {
    this.fileName = fileName;
  }

  /*
   * @return list of all of the messages in fileName
   */
  public List<String> readAll() throws IOException {
    Path path = Path.of(fileName);
    if (!Files.exists(path)) {
      return Collections.emptyList();
    }
    return Files.readAllLines(path);
  }

  public List<MessageRecord> readAllRecords() throws IOException {
    List<MessageRecord> records = new ArrayList<>();
    for (String line : readAll()) {
      if (!line.isBlank()) {
        records.add(MessageRecord.fromFileLine(line));
      }
    }
    return records;
  }
}
