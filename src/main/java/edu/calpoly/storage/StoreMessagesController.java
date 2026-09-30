package edu.calpoly.storage;

import java.io.IOException;
import java.time.LocalTime;

import org.springframework.boot.jackson.autoconfigure.JacksonProperties.Json;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import edu.calpoly.provided.Broker;

@RestController 
public class StoreMessagesController {

    private final StoreMessages store;

    public StoreMessagesController(StoreMessages store) {
        this.store = store;
    }

    @PostMapping("/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public void storeMessage(@RequestBody String message) {
        if (message.isBlank()) {
            return;
        }

        try {
            store.storeMessage(message);
        } catch (IOException e) {
            System.err.println("Could not Write to File.");
        }
    }
}
