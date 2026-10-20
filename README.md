<img width="1536" height="404" alt="CSC 5100 banner" src="https://github.com/user-attachments/assets/60356cf4-d5df-470b-be2b-44597bec95af" />

# CSC 5100 — Software Engineering

Shared course repository for requirements, design, implementation, testing, integration, and cloud deployment.

The course combines a **shared Java application** (Sprint 1 and Sprint 2), **individual AWS Lambda exercises (Lab 5)**, and subsequent assignments. The product backlog, user stories, acceptance criteria, and tasks are tracked through [GitHub Issues](../../issues) and the course GitHub Project.

**Start here:** [Assignment log](ASSIGNMENTS.md) · [Lab 5 — AWS Lambda](lab/lambda/README.md) · [Pull requests](../../pulls)

## Repository structure

```text
5100-requirements/
├── README.md                 # Course repository guide
├── ASSIGNMENTS.md            # Chronological assignment log
├── pom.xml                   # Java 17 / Maven configuration
├── docs/                     # Design and module documentation
├── lab/                      # Course labs
│   └── lambda/               # Lab 5 instructions and individual guides
└── src/
    ├── main/
    │   ├── java/edu/calpoly/
    │   │   ├── analysis/
    │   │   ├── eye/
    │   │   ├── lambdalab/   # Lab 5 implementations
    │   │   ├── message/
    │   │   ├── monitor/
    │   │   ├── provided/    # Instructor infrastructure
    │   │   ├── robot/
    │   │   ├── security/
    │   │   └── storage/
    │   └── resources/       # Input fixtures (including storage samples)
    └── test/java/           # Automated JUnit tests
```

## Build and test

Use Java 17 and Maven from the repository root:

```bash
mvn clean compile
mvn test
mvn clean package
```

## Shared application: message contract

The original shared application uses comma-separated text messages through the instructor-provided `edu.calpoly.provided.Broker`:

```text
ROBOT,J1,J2,J3,J4,J5,J6,X,Y,Z
GAZE,X,Y
AFFECT,focus,excitement,engagement,interest,stress
LIDAR,X,Y,Z
```

The broker transports strings; consumers interpret message types. Later services may expose REST endpoints and MQTT contracts documented with their modules. Do not assume the original broker is an MQTT broker.

## Development workflow

For **story-based assignments**: assigned story → Tasks (sub-issues) → branch → design and implementation → tests → Pull Request → instructor review → merge.

**Lab 5 is different:** follow [its individual submission instructions](lab/lambda/README.md); do not create an Issue, Story, or Task for Lab 5.

### Important Notes — Required for Every Submission

- Do not push student changes directly to `main`. Open a PR and wait for instructor approval.
- Identify the relevant story in story-based PRs; describe the implementation and evidence of testing.
- Complete the assigned scope, update the corresponding documentation, and add meaningful tests.
- Run `mvn clean package` before submitting Java changes, unless the assignment specifies otherwise.
- Do not edit instructor-provided infrastructure in `src/main/java/edu/calpoly/provided/` or this root README unless instructed.
- Do not commit generated build output, secrets, credentials, or machine-specific files.
- Follow assignment-specific instructions when they differ from the general workflow.

## Storage sample files

The original storage sample inputs live in `src/main/resources/storage/`. Java classpath resources can be loaded using `getResourceAsStream("/storage/messages.csv")`; runtime output files should be written to an explicitly configured writable path, not packaged resources. See [storage documentation](docs/module-7-storage/README.md).

## Documentation and assignments

- [Assignment history and links](ASSIGNMENTS.md)
- [Lab 5 — Lambda lab](lab/lambda/README.md)
- [Module documentation](docs/)
