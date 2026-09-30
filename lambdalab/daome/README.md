# Lambda Lab — MovingAverageLambda

**Package:** `edu.calpoly.lambdalab.daome`  
**Class:** `MovingAverageLambda`  
**HTTP method:** `POST`  
**Route:** `/5100/moving-average`  
**Public endpoint:** `https://api.javiergs.phd/5100/moving-average`

## Problem

Calculate a moving average over sensor samples.

## Contract

**Input:** JSON samples and window  
**Output:** movingAverage array

Return HTTP `200` for a valid request and HTTP `400` for invalid input. Keep business logic separate from the AWS/API Gateway adapter in `handleRequest()`.

## Your Work

Create:

- `src/main/java/edu/calpoly/lambdalab/daome/MovingAverageLambda.java`
- `src/test/java/edu/calpoly/lambdalab/daome/MovingAverageLambdaTest.java`

Use the instructor `TemperatureLambda` and its tests as the reference for API Gateway request/response handling. Implement the assigned behavior and write at least three meaningful JUnit tests.

Required test cases: **Normal window; window boundary; invalid window/input**.

## Test the Deployed Lambda

After your code passes GitHub Actions and is deployed:

Run from the IntelliJ Terminal:

`curl -X POST "https://api.javiergs.phd/5100/moving-average" -H "Content-Type: application/json" -d '{"samples":[1,2,3,4,5],"window":3}'`

Your response must be JSON and follow the contract above.

## Pull Request

Create **one PR** for this lab. Do not create an Issue, Story, or Task.

PR title:

`Lambda Lab - MovingAverageLambda - <GitHubID>`

In the PR description include:

- Lambda: `MovingAverageLambda`
- Method and route: `POST /5100/moving-average`
- Confirmation that JUnit tests pass
- Confirmation that `mvn clean package` succeeds
- The `api.javiergs.phd` endpoint tested
- The result of the deployed test
