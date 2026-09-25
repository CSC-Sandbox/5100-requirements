package edu.calpoly.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
    return Files.readAllLines(Path.of(fileName));
  }
}
