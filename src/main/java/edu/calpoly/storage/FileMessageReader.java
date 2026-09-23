package edu.calpoly.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


public class FileMessageReader {

  private String fileName;
  
  public FileMessageReader(String fileName) {
    this.fileName = fileName;
  }

  public List<String> readAll() throws IOException {
    return Files.readAllLines(Path.of(fileName));
  }
}
