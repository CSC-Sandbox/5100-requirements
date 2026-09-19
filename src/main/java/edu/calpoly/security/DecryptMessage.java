package edu.calpoly.security;

import edu.calpoly.provided.Encryption;

import java.util.Objects;
import java.util.Scanner;

public class DecryptMessage {
    public static void main(String[] args) {
        System.out.println("Type an encrypted message to decrypt it, or type \"/quit\" to quit.");
        String encryptedMessage = "";
        Scanner scanner = new Scanner(System.in);
        while (!Objects.equals(encryptedMessage, "/quit")) {
            encryptedMessage = scanner.nextLine();
            if (!Objects.equals(encryptedMessage, "/quit")) {
                String decryptedMessage = DecryptMessage.decryptMessage(encryptedMessage);
                System.out.println("Encrypted: " + encryptedMessage);
                System.out.println("Decrypted: " + decryptedMessage);
            }
        }
        scanner.close();
    }

    // Utility class to return a decrypted message from a string input.
    public static String decryptMessage(String encryptedMessage) {
        return Encryption.decrypt(encryptedMessage);
    }
}
