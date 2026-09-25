package edu.calpoly.security;

import edu.calpoly.provided.Encryption;

import java.util.Objects;
import java.util.Scanner;

/**
 * Message encryption implementation for CSC 5100
 * This class is used to encrypt messages.
 *
 * @author Howard Jiang (hwrd22)
 * @version 1.0 (2026-09-21)
 */
public final class EncryptMessage {
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

    /**
     * Message decryption implementation for CSC 5100
     * Utility method to encrypt a message in the form of a string.
     *
     * @author Howard Jiang (hwrd22)
     */
    public static String encryptMessage(String message) {
        return Encryption.encrypt(message);
    }
}
