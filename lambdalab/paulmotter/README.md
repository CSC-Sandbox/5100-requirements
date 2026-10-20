# Lambda Lab — RobotDistanceLambda

**Package:** `edu.calpoly.lambdalab.paulmotter`  
**Class:** `RobotDistanceLambda`  
**HTTP method:** `GET`  
**Route:** `/5100/robot-distance`  
**Public endpoint:** `https://api.javiergs.phd/5100/robot-distance`

## Problem

Calculate Euclidean distance between two 3D robot positions.

## Contract

**Input:** Query: x1,y1,z1,x2,y2,z2  
**Output:** distance

Return HTTP `200` for a valid request and HTTP `400` for invalid input. Keep business logic separate from the AWS/API Gateway adapter in `handleRequest()`.

## Your Work

Create:

- `src/main/java/edu/calpoly/lambdalab/paulmotter/RobotDistanceLambda.java`
- `src/test/java/edu/calpoly/lambdalab/paulmotter/RobotDistanceLambdaTest.java`

Use the instructor `TemperatureLambda` and its tests as the reference for API Gateway request/response handling. Implement the assigned behavior and write at least three meaningful JUnit tests.

Required test cases: **3-4-5 distance; identical points; invalid input**.

## Test the Deployed Lambda

After your code passes GitHub Actions and is deployed:

Open in a browser:

`https://api.javiergs.phd/5100/robot-distance?x1=0&y1=0&z1=0&x2=3&y2=4&z2=0`

Your response must be JSON and follow the contract above.

## Pull Request

Create **one PR** for this lab. Do not create an Issue, Story, or Task.

PR title:

`Lambda Lab - RobotDistanceLambda - <GitHubID>`

In the PR description include:

- Lambda: `RobotDistanceLambda`
- Method and route: `GET /5100/robot-distance`
- Confirmation that JUnit tests pass
- Confirmation that `mvn clean package` succeeds
- The `api.javiergs.phd` endpoint tested
- The result of the deployed test
