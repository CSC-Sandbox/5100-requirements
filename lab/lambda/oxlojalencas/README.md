# Lambda Lab — NearestSampleLambda

**Package:** `edu.calpoly.lambdalab.oxlojalencas`  
**Class:** `NearestSampleLambda`  
**HTTP method:** `POST`  
**Route:** `/5100/nearest-sample`  
**Public endpoint:** `https://api.javiergs.phd/5100/nearest-sample`

## Problem

Find the sensor sample nearest to an event timestamp.

## Contract

**Input:** JSON eventTimestamp and timestamped samples  
**Output:** nearest sample and timeDifference

Return HTTP `200` for a valid request and HTTP `400` for invalid input. Keep business logic separate from the AWS/API Gateway adapter in `handleRequest()`.

## Your Work

Create:

- `src/main/java/edu/calpoly/lambdalab/oxlojalencas/NearestSampleLambda.java`
- `src/test/java/edu/calpoly/lambdalab/oxlojalencas/NearestSampleLambdaTest.java`

Use the instructor `TemperatureLambda` and its tests as the reference for API Gateway request/response handling. Implement the assigned behavior and write at least three meaningful JUnit tests.

Required test cases: **Exact match; nearest neighbor; empty/invalid samples**.

## Test the Deployed Lambda

After your code passes GitHub Actions and is deployed:

Run from the IntelliJ Terminal:

`curl -X POST "https://api.javiergs.phd/5100/nearest-sample" -H "Content-Type: application/json" -d '{"eventTimestamp":10.2,"samples":[{"timestamp":10.0,"value":4.2},{"timestamp":10.4,"value":5.1}]}'`

Your response must be JSON and follow the contract above.

## Pull Request

Create **one PR** for this lab. Do not create an Issue, Story, or Task.

PR title:

`Lambda Lab - NearestSampleLambda - <GitHubID>`

In the PR description include:

- Lambda: `NearestSampleLambda`
- Method and route: `POST /5100/nearest-sample`
- Confirmation that JUnit tests pass
- Confirmation that `mvn clean package` succeeds
- The `api.javiergs.phd` endpoint tested
- The result of the deployed test
