package edu.calpoly.storage;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

public class MessageService {
  private final FileMessageStore store;
  private final FileMessageReader reader;

  public MessageService(FileMessageStore store, FileMessageReader reader) {
    this.store = store;
    this.reader = reader;
  }

  public void storeMessage(String rawMessage) throws IOException {
    MessageRecord record = new MessageRecord(Instant.now().toString(), rawMessage);
    store.store(record);
  }

  public List<MessageRecord> retrieveMessages() throws IOException {
    return reader.readAllRecords();
  }

}
