# Lambda Lab — DominantAffectLambda

**Package:** `edu.calpoly.lambdalab.adrian0427`  
**Class:** `DominantAffectLambda`  
**HTTP method:** `POST`  
**Route:** `/5100/dominant-affect`  
**Public endpoint:** `https://api.javiergs.phd/5100/dominant-affect`

## Problem

Return the dominant affective metric from a set of affect values.

## Contract

**Input:** JSON affect metrics  
**Output:** dominant metric and value

Return HTTP `200` for a valid request and HTTP `400` for invalid input. Keep business logic separate from the AWS/API Gateway adapter in `handleRequest()`.

## Your Work

Create:

- `src/main/java/edu/calpoly/lambdalab/adrian0427/DominantAffectLambda.java`
- `src/test/java/edu/calpoly/lambdalab/adrian0427/DominantAffectLambdaTest.java`

Use the instructor `TemperatureLambda` and its tests as the reference for API Gateway request/response handling. Implement the assigned behavior and write at least three meaningful JUnit tests.

Required test cases: **Clear maximum; boundary/tie rule; invalid input**.

## Test the Deployed Lambda

After your code passes GitHub Actions and is deployed:

Run from the IntelliJ Terminal:

`curl -X POST "https://api.javiergs.phd/5100/dominant-affect" -H "Content-Type: application/json" -d '{"stress":0.2,"engagement":0.8,"focus":0.6,"interest":0.5}'`

Your response must be JSON and follow the contract above.

## Pull Request

Create **one PR** for this lab. Do not create an Issue, Story, or Task.

PR title:

`Lambda Lab - DominantAffectLambda - <GitHubID>`

In the PR description include:

- Lambda: `DominantAffectLambda`
- Method and route: `POST /5100/dominant-affect`
- Confirmation that JUnit tests pass
- Confirmation that `mvn clean package` succeeds
- The `api.javiergs.phd` endpoint tested
- The result of the deployed test
