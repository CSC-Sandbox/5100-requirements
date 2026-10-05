package edu.calpoly.storage;

import java.util.Objects;

/*
 * This class holds the structure of our messages.
 * It is responsible to hold the structure of our messages
 * before getting stored, and to reconstruct one from a stored line.
 *
 * @author Edgard Aviles
 * @version September 25, 2026
 */
public class MessageRecord {
  private final String timestamp;
  private final String message;

  public MessageRecord(String timestamp, String message) {
    this.timestamp = timestamp;
    this.message = message;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public String getMessage() {
    return message;
  }

  public String toFileLine() {
    return timestamp + "," + message;
  }

  public static MessageRecord fromFileLine(String line) {
    if (line == null) {
      throw new IllegalArgumentException("Line is null.");
    }
    int commaIndex = line.indexOf(',');
    if (commaIndex < 0) {
      throw new IllegalArgumentException("Malformed stored line (no comma): " + line);
    }
    String timestamp = line.substring(0, commaIndex);
    String message = line.substring(commaIndex + 1);
    return new MessageRecord(timestamp, message);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof MessageRecord)) {
      return false;
    }
    MessageRecord other = (MessageRecord) o;
    return Objects.equals(timestamp, other.timestamp) && Objects.equals(message, other.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(timestamp, message);
  }
}
