# Module 4: Security
## Encrypt Message
This class runs a loop that asks the user to type messages. Each message sent will output an encrypted version of that message.<br>
Alternatively, the user may enter `/quit` to exit the program.<br>
To test the encrypted message, copy the encrypted output and paste it the running program of `DecryptMessage.java`.<br>
The class currently displays the encrypted message for demonstration purposes.
## Decrypt Message
This class runs a loop that asks the user to type encrypted messages. Each encrypted message sent will output the original message.<br>
If an invalid encrypted string is passed into the running program, an error message will output without disrupting runtime.<br>
Alternatively, the user may enter `/quit` to exit the program.<br>
To test, encrypt a message in `EncryptMessage.java` and paste its output into the running program of `DecryptMessage.java`.
## Future Implementation
Encrypting and decrypting messages should not be an API or MQTT service, as encrypting and decrypting messages does not entail sending the message to a recipient.<br>
As such, encrypting and decrypting message should instead be a local library used by an API/MQTT service that handles message communication.<br>
Moreover, the methods provided by the classes should not be overridden. Errors are likely to occur if overridden incorrectly.<br>
The encryption class, in the final product, should take in messages in JSON format and outputs encrypted bytes, possibly as a string.<br>
The decryption class will take in the encrypted bytes and output the original JSON data.<br>
There is currently no dependency needed by these classes aside from the provided encryption algorithm class.