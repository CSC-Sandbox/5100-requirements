package edu.calpoly.security;

import edu.calpoly.provided.Encryption;

import java.util.Objects;
import java.util.Scanner;

public class EncryptMessage {
    public static void main(String[] args) {
        System.out.println("Type a message to encrypt it, or type \"/quit\" to quit.");
        String message = "";
        Scanner scanner = new Scanner(System.in);
        while (!Objects.equals(message, "/quit")) {
            message = scanner.nextLine();
            if (!Objects.equals(message, "/quit")) {
                String encryptedMessage = EncryptMessage.encryptMessage(message);
                System.out.println("Original: " + message);
                System.out.println("Encrypted: " + encryptedMessage);
            }
        }
        scanner.close();
    }

    // Utility method to be used by decryption program
    public static String encryptMessage(String message) {
        return Encryption.encrypt(message);
    }
}
