# Lambda Lab — DataFreshnessLambda

**Package:** `edu.calpoly.lambdalab.hwrd22`  
**Class:** `DataFreshnessLambda`  
**HTTP method:** `GET`  
**Route:** `/5100/data-freshness`  
**Public endpoint:** `https://api.javiergs.phd/5100/data-freshness`

## Problem

Determine whether sensor data is FRESH or STALE.

## Contract

**Input:** Query: current, sample, threshold  
**Output:** status and age

Return HTTP `200` for a valid request and HTTP `400` for invalid input. Keep business logic separate from the AWS/API Gateway adapter in `handleRequest()`.

## Your Work

Create:

- `src/main/java/edu/calpoly/lambdalab/hwrd22/DataFreshnessLambda.java`
- `src/test/java/edu/calpoly/lambdalab/hwrd22/DataFreshnessLambdaTest.java`

Use the instructor `TemperatureLambda` and its tests as the reference for API Gateway request/response handling. Implement the assigned behavior and write at least three meaningful JUnit tests.

Required test cases: **Fresh; stale/boundary; invalid timestamps**.

## Test the Deployed Lambda

After your code passes GitHub Actions and is deployed:

Open in a browser:

`https://api.javiergs.phd/5100/data-freshness?current=100&sample=95&threshold=10`

Your response must be JSON and follow the contract above.

## Pull Request

Create **one PR** for this lab. Do not create an Issue, Story, or Task.

PR title:

`Lambda Lab - DataFreshnessLambda - <GitHubID>`

In the PR description include:

- Lambda: `DataFreshnessLambda`
- Method and route: `GET /5100/data-freshness`
- Confirmation that JUnit tests pass
- Confirmation that `mvn clean package` succeeds
- The `api.javiergs.phd` endpoint tested
- The result of the deployed test
