package edu.calpoly.storage;

import org.springframework.stereotype.Component;

@FunctionalInterface
@Component
public interface FileWriter {
    void write(MessageRecord message);
}
