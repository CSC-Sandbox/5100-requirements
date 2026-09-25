# Detect Malformed Messages Evolution and Possible Changes

# Refactoring:

Currently for MessageValidator.java, everything is just encapsulated into one class. This has resulted in a large class
that does multiple things at once. I could see this being separated into 2 classes. The first of which being the message
parser, which figures out if the given string is a JSON, CSV, or an incompatible type. This can definitely be its own
class, which would connect back to the main MessageValidator class. The second class is of course, that main message
validator class, which would ensure that once the message is parsed, that all the given values lie within the correct
specified ranges, and are compliant with the given data.

Additionally, an improvement for this class should be the implementation of making MessageValidator a singleton. There
should only need to be one instance of a message validator at any given time, used either before or after encryption.
This means that a singleton would lead requests to one point of reference, instead of creating many instances of the
same thing which would waste resources.

# Planning the Evolution of the Feature

This feature should in no way be a service that runs independently. Its job is very simple, and runs very quickly. As 
such, there is no reason for it to become a service, and it should stay as a local library, only being called upon when
needed. 

For a call to get the instance of the singleton:

| | |
|---|---|
| Input | none |
| Output | the singleton `MessageValidator` |
| Output interface | Static method return value: `MessageValidator.getInstance()` |

For a call to validate()

| | |
|---|---|
| Input | `message: String` - raw CSV or JSON text |
| Input format | Java `String` (plain text; may itself contain CSV or JSON, but the caller treats it as opaque text) |
| Input interface | Java method: `MessageValidator.getInstance().validate(String message)` |
| Output | `boolean` - true if valid |
| Output format | Java primitive |
| Output interface | Method return value |

For a call to get the last reason for an unvalidated message:

| | |
|---|---|
| Input | none |
| Output | `String` - human-readable reason the most recent `validate()` call on this thread failed, or `null` if it succeeded |
| Output format | Java `String` |
| Output interface | Method return value: `MessageValidator.getInstance().getLastError()` |

# Dependencies

Currently, uses `ObjectMapper` for JSON parsing, and no other dependencies. Nothing currently uses this feature right now, but I would expect
in the future that encrypt and decrypt message will use this validation to ensure sent and received messages are valid,
and not in out of bounds ranges or in incorrect formats.

The only things that really crosses interfaces here is the input to validate(), the boolean return value, and if 
applicable, the error message from getLastError(). 