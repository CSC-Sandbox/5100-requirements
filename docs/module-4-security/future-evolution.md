# Future Evolutions
This feature should interact with other messaging features, such as Message Validation, as well as features involving data streams between different machines. In addition, for more sensitive information, having stored data be encrypted may also be necessary.
# Input/Output
For simple messages, the encryption and decryption classes should take in:
- Encryption: String as input, encrypted bytes as output
- Decryption: Encrypted bytes as input, original string as output

For files (Such as JSON files/data):
- Encryption: File/byte stream as input, encrypted file/byte stream as output
- Decryption: Encrypted file/byte stream as input, original file/byte stream as output