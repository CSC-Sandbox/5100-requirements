# Lambda Lab — EventRateLambda

**Package:** `edu.calpoly.lambdalab.wliu4040`  
**Class:** `EventRateLambda`  
**HTTP method:** `GET`  
**Route:** `/5100/event-rate`  
**Public endpoint:** `https://api.javiergs.phd/5100/event-rate`

## Problem

Calculate event frequency over a duration.

## Contract

**Input:** Query: events, duration  
**Output:** eventsPerSecond

Return HTTP `200` for a valid request and HTTP `400` for invalid input. Keep business logic separate from the AWS/API Gateway adapter in `handleRequest()`.

## Your Work

Create:

- `src/main/java/edu/calpoly/lambdalab/wliu4040/EventRateLambda.java`
- `src/test/java/edu/calpoly/lambdalab/wliu4040/EventRateLambdaTest.java`

Use the instructor `TemperatureLambda` and its tests as the reference for API Gateway request/response handling. Implement the assigned behavior and write at least three meaningful JUnit tests.

Required test cases: **Normal rate; zero events; invalid duration/input**.

## Test the Deployed Lambda

After your code passes GitHub Actions and is deployed:

Open in a browser:

`https://api.javiergs.phd/5100/event-rate?events=30&duration=60`

Your response must be JSON and follow the contract above.

## Pull Request

Create **one PR** for this lab. Do not create an Issue, Story, or Task.

PR title:

`Lambda Lab - EventRateLambda - <GitHubID>`

In the PR description include:

- Lambda: `EventRateLambda`
- Method and route: `GET /5100/event-rate`
- Confirmation that JUnit tests pass
- Confirmation that `mvn clean package` succeeds
- The `api.javiergs.phd` endpoint tested
- The result of the deployed test
