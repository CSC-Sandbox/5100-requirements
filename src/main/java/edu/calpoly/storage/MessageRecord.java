package edu.calpoly.storage;


import com.fasterxml.jackson.annotation.JsonProperty;

/*
 * This class holds the structure of our messages
 * It is responsible to hold the structure of our messages
 * before getting stored.
 *
 * @author Edgard Aviles
 * @version September 25, 202
 */
public class MessageRecord {
  private final String timestamp;
  private final String message;

  /*
   * @param timestamp the time upon the message was received
   * @param message the message that was received
   */
  public MessageRecord (@JsonProperty("timestamp") String timestamp,@JsonProperty("message") String message) {
    this.timestamp = timestamp;
    this.message = message;
  }

  /*
   * @return the structure that we want our message
   */
  public String toFileLine() {
    return timestamp + "," + message;
  }
}
