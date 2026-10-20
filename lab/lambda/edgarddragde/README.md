# Lambda Lab — RobotReachabilityLambda

**Package:** `edu.calpoly.lambdalab.edgarddragde`  
**Class:** `RobotReachabilityLambda`  
**HTTP method:** `GET`  
**Route:** `/5100/robot-reachability`  
**Public endpoint:** `https://api.javiergs.phd/5100/robot-reachability`

## Problem

Determine whether a 3D position is inside a robot workspace radius.

## Contract

**Input:** Query: x,y,z,radius  
**Output:** reachable and distance

Return HTTP `200` for a valid request and HTTP `400` for invalid input. Keep business logic separate from the AWS/API Gateway adapter in `handleRequest()`.

## Your Work

Create:

- `src/main/java/edu/calpoly/lambdalab/edgarddragde/RobotReachabilityLambda.java`
- `src/test/java/edu/calpoly/lambdalab/edgarddragde/RobotReachabilityLambdaTest.java`

Use the instructor `TemperatureLambda` and its tests as the reference for API Gateway request/response handling. Implement the assigned behavior and write at least three meaningful JUnit tests.

Required test cases: **Inside; exactly on boundary; invalid radius/input**.

## Test the Deployed Lambda

After your code passes GitHub Actions and is deployed:

Open in a browser:

`https://api.javiergs.phd/5100/robot-reachability?x=0.3&y=0.2&z=0.1&radius=0.5`

Your response must be JSON and follow the contract above.

## Pull Request

Create **one PR** for this lab. Do not create an Issue, Story, or Task.

PR title:

`Lambda Lab - RobotReachabilityLambda - <GitHubID>`

In the PR description include:

- Lambda: `RobotReachabilityLambda`
- Method and route: `GET /5100/robot-reachability`
- Confirmation that JUnit tests pass
- Confirmation that `mvn clean package` succeeds
- The `api.javiergs.phd` endpoint tested
- The result of the deployed test
