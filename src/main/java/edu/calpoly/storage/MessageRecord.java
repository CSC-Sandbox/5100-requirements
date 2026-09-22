package edu.calpoly.provided;


public class MessageRecord {
  private final String timestamp;
  private final String message;

  public MessageRecord (String timestamp, String message) {
    this.timestamp = timestamp;
    this.message = message;
  }

  public String toFileLine() {
    return timestamp + "," + message;
  }
}
