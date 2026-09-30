package edu.calpoly.storage;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {

    @Bean
    public FileMessageReader fileMessageReader() {
        return new FileMessageReader("data/messages.csv");
    }
    @Bean FileMessageStore fileMessageStore() {
    return new FileMessageStore("data/empty-messages.csv");
    }
}
