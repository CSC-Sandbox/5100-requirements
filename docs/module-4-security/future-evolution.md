# Future Evolutions
This feature should interact with other messaging features, such as Message Validation, as well as features involving data streams between different machines. In addition, for more sensitive information, having stored data be encrypted may also be necessary.<br>
Specifically, features such as Send Message, Receive Message, Store Messages, and Retrieve Stored Messages will use this as a library.<br>
Any feature that sends/stores messages will call `EncryptMessage.java`'s `encryptMessage()` static method via the code `EncryptMessage.encryptMessage(String message)`, and any feature that receives/retrieves messages will call `DecryptMessage.java`'s `decryptMessage()` static method via the code `DecryptMessage.decryptMessage(String encryptedMessage)`. Both methods will return a String.
# Input/Output
For simple messages, the encryption and decryption classes should take in:
- Encryption: String as input, encrypted bytes as output
- Decryption: Encrypted bytes as input, original string as output

For files (Such as JSON files/data):
- Encryption: File/byte stream as input, encrypted file/byte stream as output
- Decryption: Encrypted file/byte stream as input, original file/byte stream as output