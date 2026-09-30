package edu.calpoly.storage;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
/**
 * REST Interface for RetrieveMessages that will get messages from server.
 * @author David Montiel
 * @version 1.0.0
 * 
 */
@RestController 
public class RetrieveMessagesController {

    private final FileMessageReader fileReader;
    public RetrieveMessagesController(FileMessageReader fileReader) {
        this.fileReader = fileReader;
    }

    /**
     * @return List All the lines in a file.
     * @throws ResponseStatusException when an Error reading file occurs this does not crash the system.
     */
    @GetMapping("/messages")
    public ResponseEntity<List<String>> addMessage() {
        try {
            var list = fileReader.readAll();
            return ResponseEntity.ok(list);
        }
        catch(IOException e) {
            System.err.println("Unable to read file");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
