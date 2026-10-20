# Lambda Lab — EncryptMessageLambda

**Package:** `edu.calpoly.lambdalab.aidenrodriguez`  
**Class:** `EncryptMessageLambda`  
**HTTP method:** `GET`  
**Route:** `/5100/encrypt`  
**Public endpoint:** `https://api.javiergs.phd/5100/encrypt`

## Problem

Encrypt a message using a Caesar shift.

## Contract

**Input:** Query: message, shift  
**Output:** original, encrypted, shift

Return HTTP `200` for a valid request and HTTP `400` for invalid input. Keep business logic separate from the AWS/API Gateway adapter in `handleRequest()`.

## Your Work

Create:

- `src/main/java/edu/calpoly/lambdalab/aidenrodriguez/EncryptMessageLambda.java`
- `src/test/java/edu/calpoly/lambdalab/aidenrodriguez/EncryptMessageLambdaTest.java`

Use the instructor `TemperatureLambda` and its tests as the reference for API Gateway request/response handling. Implement the assigned behavior and write at least three meaningful JUnit tests.

Required test cases: **Hello shift 3; XYZ wraparound; missing/invalid input**.

## Test the Deployed Lambda

After your code passes GitHub Actions and is deployed:

Open in a browser:

`https://api.javiergs.phd/5100/encrypt?message=Hello&shift=3`

Your response must be JSON and follow the contract above.

## Pull Request

Create **one PR** for this lab. Do not create an Issue, Story, or Task.

PR title:

`Lambda Lab - EncryptMessageLambda - <GitHubID>`

In the PR description include:

- Lambda: `EncryptMessageLambda`
- Method and route: `GET /5100/encrypt`
- Confirmation that JUnit tests pass
- Confirmation that `mvn clean package` succeeds
- The `api.javiergs.phd` endpoint tested
- The result of the deployed test
