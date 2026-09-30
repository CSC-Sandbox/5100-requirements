# Lambda Lab — LidarSafetyLambda

**Package:** `edu.calpoly.lambdalab.ejiswkrosmew`  
**Class:** `LidarSafetyLambda`  
**HTTP method:** `POST`  
**Route:** `/5100/lidar-safety`  
**Public endpoint:** `https://api.javiergs.phd/5100/lidar-safety`

## Problem

Classify LiDAR readings as SAFE or WARNING using a safety threshold.

## Contract

**Input:** JSON distances and threshold  
**Output:** status and minimum distance

Return HTTP `200` for a valid request and HTTP `400` for invalid input. Keep business logic separate from the AWS/API Gateway adapter in `handleRequest()`.

## Your Work

Create:

- `src/main/java/edu/calpoly/lambdalab/ejiswkrosmew/LidarSafetyLambda.java`
- `src/test/java/edu/calpoly/lambdalab/ejiswkrosmew/LidarSafetyLambdaTest.java`

Use the instructor `TemperatureLambda` and its tests as the reference for API Gateway request/response handling. Implement the assigned behavior and write at least three meaningful JUnit tests.

Required test cases: **SAFE; WARNING/boundary; invalid input**.

## Test the Deployed Lambda

After your code passes GitHub Actions and is deployed:

Run from the IntelliJ Terminal:

`curl -X POST "https://api.javiergs.phd/5100/lidar-safety" -H "Content-Type: application/json" -d '{"distances":[1.4,0.8,2.1,0.5],"threshold":0.6}'`

Your response must be JSON and follow the contract above.

## Pull Request

Create **one PR** for this lab. Do not create an Issue, Story, or Task.

PR title:

`Lambda Lab - LidarSafetyLambda - <GitHubID>`

In the PR description include:

- Lambda: `LidarSafetyLambda`
- Method and route: `POST /5100/lidar-safety`
- Confirmation that JUnit tests pass
- Confirmation that `mvn clean package` succeeds
- The `api.javiergs.phd` endpoint tested
- The result of the deployed test
