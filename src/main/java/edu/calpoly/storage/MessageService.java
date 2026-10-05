package edu.calpoly.storage;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

/*
 * This class holds the shared logic for storing and retrieving messages.
 * Both the REST and MQTT interfaces call into this class instead of
 * each reimplementing storage access on their own.
 *
 * @author Edgard Aviles
 * @version October 4, 2026
 */

public class MessageService {
  private final FileMessageStore store;
  private final FileMessageReader reader;


  public MessageService(FileMessageStore store, FileMessageReader reader) {
    this.store = store;
    this.reader = reader;
  }

  /*
   * @param rawMessage the message content to store
   * @throws IOException if the message could not be written
   */
  public void storeMessage(String rawMessage) throws IOException {
    MessageRecord record = new MessageRecord(Instant.now().toString(), rawMessage);
    store.store(record);
  }

  /*
   * @return the list of stored MessageRecords (empty if none)
   * @throws IOException if the stored file could not be read
   */
  public List<MessageRecord> retrieveMessages() throws IOException {
    return reader.readAllRecords();
  }

}
