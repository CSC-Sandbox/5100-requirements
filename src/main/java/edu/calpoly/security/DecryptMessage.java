package edu.calpoly.security;

import edu.calpoly.provided.Encryption;

import java.util.Objects;
import java.util.Scanner;

/**
 * Message decryption implementation for CSC 5100
 * This class is used to decrypt encrypted messages, if the string is compatible.
 *
 * @author Howard Jiang (hwrd22)
 * @version 1.0 (2026-09-21)
 */
public final class DecryptMessage {

    public static void main(String[] args) {
        System.out.println("Type an encrypted message to decrypt it, or type \"/quit\" to quit.");
        String encryptedMessage = "";
        Scanner scanner = new Scanner(System.in);
        while (!Objects.equals(encryptedMessage, "/quit")) {
            encryptedMessage = scanner.nextLine();
            if (!Objects.equals(encryptedMessage, "/quit")) {
                String decryptedMessage = DecryptMessage.decryptMessage(encryptedMessage);
                // Message was successfully decrypted (if not, then skip the below output)
                if (decryptedMessage != null) {
                    System.out.println("Encrypted: " + encryptedMessage);
                    System.out.println("Decrypted: " + decryptedMessage);
                }
            }
        }
        scanner.close();
    }

    /**
     * Message decryption implementation for CSC 5100
     * Utility class to return a decrypted message from a string input.
     *
     * @author Howard Jiang (hwrd22)
     */
    public static String decryptMessage(String encryptedMessage) {
        try {
            return Encryption.decrypt(encryptedMessage);
        } catch (IllegalArgumentException e) {
            // Output error with reason, then continue.
            System.out.println("Error occurred:" + e);
            // String couldn't be decrypted, return null
            return null;
        }
    }
}
