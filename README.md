# CSC 5100 — Software Engineering

This repository contains the shared software project for CSC 5100, including course-provided infrastructure, student implementations, data, tests, and design documentation.

The Product Backlog, User Stories, Acceptance Criteria, and development work are maintained through GitHub Issues and the CSC 5100 GitHub Project.

## Repository Structure

The Java project follows the standard Maven directory structure.

```text
5100-requirements/
├── README.md
├── ASSIGNMENTS.md
├── pom.xml
├── data/
├── docs/
└── src/
    └── main/
        └── java/
            └── edu/
                └── calpoly/
                    ├── analysis/
                    ├── eye/
                    ├── message/
                    ├── monitor/
                    ├── provided/
                    ├── robot/
                    ├── security/
                    └── storage/
```

Student implementations are organized into packages according to their responsibility.

The package:

```text
edu.calpoly.provided
```

contains infrastructure and test programs supplied by the instructor. Students should use these classes but should not modify them unless specifically instructed.

## Maven

This project uses Maven for project structure, dependency management, compilation, testing, and packaging.

The Maven configuration is defined in:

```text
pom.xml
```

The project currently uses Java 17.

Compile the project from the repository root with:

```bash
mvn clean compile
```

Build the project with:

```bash
mvn clean package
```

As automated tests are introduced, they will be placed under the standard Maven test directory:

```text
src/test/java/
```

and can be executed with:

```bash
mvn test
```

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

Development follows the GitHub workflow practiced during Sprint 1:

```text
Assigned User Story
    ↓
Break User Story into Tasks (sub-issues)
    ↓
Create a branch
    ↓
Design / Update Documentation
    ↓
Implement in Java
    ↓
Test
    ↓
Open a Pull Request
    ↓
Instructor Review
    ↓
Merge
```

Students should not commit programming work directly to `main`.

A branch should identify the work being performed. For example:

```text
9-gather-robot-state
```

A Pull Request should identify the corresponding User Story, for example:

```text
Implement #9 Gather Robot State
```

## Provided Infrastructure

Course-provided infrastructure is located under:

```text
src/main/java/edu/calpoly/provided/
```

`Broker` supplies the communication interface used by modules that exchange data through the course communication service.

```java
import edu.calpoly.provided.Broker;

Broker broker = new Broker("localhost", 5000);
```

`Encryption` provides the encryption and decryption operations used by the security module.

The `Test...` programs in the `provided` package are instructor-provided programs for exercising different parts of the system. These should not be confused with automated unit tests that will later be placed under `src/test/java`.

## Documentation

Design and architecture documentation belongs under:

```text
docs/
```

Documentation should be associated with the corresponding User Story or module.

Do not modify this main README as part of assigned programming work unless specifically instructed.
