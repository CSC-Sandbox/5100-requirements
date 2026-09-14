# CSC 5100 — Software Engineering

This package contains the course-provided infrastructure and test programs for the CSC 5100 programming work.

The Product Backlog and the authoritative User Stories and Acceptance Criteria are maintained in the CSC 5100 GitHub requirements repository. Each assigned Feature contains the specific programming, testing, and documentation requirements for that work.

## Repository Structure

```text
CSC5100-starter-files/
├── README.md
├── ASSIGNMENTS.md
├── pom.xml
├── docs/
├── data/
│   ├── store-input.txt
│   ├── messages.csv
│   └── empty-messages.csv
└── src/main/java/edu/calpoly/provided/
    ├── Broker.java
    ├── Encryption.java
    ├── TestGatherRobot.java
    ├── TestDisplayRobot.java
    ├── TestGatherEye.java
    ├── TestDisplayEye.java
    ├── TestDisplayAffect.java
    ├── TestDisplayLidar.java
    ├── TestEncryption.java
    ├── TestEnterMessage.java
    ├── TestDisplayMessages.java
    ├── TestMonitorData.java
    ├── TestStoreMessages.java
    └── TestRetrieveMessages.java
```

The package `edu.calpoly.provided` contains infrastructure supplied by the instructor. Students should use these classes but should not modify them unless specifically instructed.

## Common Data Message Contract

Robot, Gaze, Affect, and LiDAR modules use the following common text format:

```text
TYPE,data...
```

```text
ROBOT,J1,J2,J3,J4,J5,J6,X,Y,Z
GAZE,X,Y
AFFECT,focus,excitement,engagement,interest,stress
LIDAR,X,Y,Z
```

Examples:

```text
ROBOT,0.420,-0.180,0.750,0.100,-0.320,0.570,0.250,0.100,0.420
GAZE,0.35,0.72
AFFECT,0.72,0.44,0.65,0.58,0.33
LIDAR,1.25,-0.40,0.15
```

`Broker` transports these messages as strings and does not interpret their type or values.

## Programming Workflow

```text
Assigned Feature
    ↓
Break Feature into Tasks (sub-issues)
    ↓
Create a branch
    ↓
Design with UML
    ↓
Implement in Java
    ↓
Test
    ↓
Open a Pull Request
    ↓
Instructor review
    ↓
Merge
```

Do not commit programming work directly to `main`.

For example, for Feature #9:

```text
9-gather-robot-state
```

A Pull Request should identify the Feature being implemented, for example:

```text
Implement #9 Gather Robot State
```

## Provided Infrastructure

Course-provided classes are located under:

```text
src/main/java/edu/calpoly/provided/
```

`Broker` supplies the communication interface used by assignments that exchange data through the course communication service.

```java
import edu.calpoly.provided.Broker;

Broker broker = new Broker("localhost", 5000);
```

`Encryption` provides the encryption/decryption operations used by the security module.

## Testing

Provided test programs use the `Test...` naming convention. Your assigned GitHub Feature identifies the relevant test program and expected behavior. In general, start the provided test program first and leave it running, then run your implementation.

## Documentation

Design documentation belongs under `docs/` in the directory specified by the assigned Feature. Do not modify the main README as part of the programming assignment.
