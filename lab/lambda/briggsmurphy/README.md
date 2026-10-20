# Lambda Lab — LidarClosestLambda

**Package:** `edu.calpoly.lambdalab.briggsmurphy`  
**Class:** `LidarClosestLambda`  
**HTTP method:** `POST`  
**Route:** `/5100/lidar-closest`  
**Public endpoint:** `https://api.javiergs.phd/5100/lidar-closest`

## Problem

Find the closest LiDAR measurement and its index.

## Contract

**Input:** JSON distances array  
**Output:** minimum distance and index

Return HTTP `200` for a valid request and HTTP `400` for invalid input. Keep business logic separate from the AWS/API Gateway adapter in `handleRequest()`.

## Your Work

Create:

- `src/main/java/edu/calpoly/lambdalab/briggsmurphy/LidarClosestLambda.java`
- `src/test/java/edu/calpoly/lambdalab/briggsmurphy/LidarClosestLambdaTest.java`

Use the instructor `TemperatureLambda` and its tests as the reference for API Gateway request/response handling. Implement the assigned behavior and write at least three meaningful JUnit tests.

Required test cases: **Normal array; boundary/single value; empty/invalid array**.

## Test the Deployed Lambda

After your code passes GitHub Actions and is deployed:

Run from the IntelliJ Terminal:

`curl -X POST "https://api.javiergs.phd/5100/lidar-closest" -H "Content-Type: application/json" -d '{"distances":[1.4,0.8,2.1,0.5,1.7]}'`

Your response must be JSON and follow the contract above.

## Pull Request

Create **one PR** for this lab. Do not create an Issue, Story, or Task.

PR title:

`Lambda Lab - LidarClosestLambda - <GitHubID>`

In the PR description include:

- Lambda: `LidarClosestLambda`
- Method and route: `POST /5100/lidar-closest`
- Confirmation that JUnit tests pass
- Confirmation that `mvn clean package` succeeds
- The `api.javiergs.phd` endpoint tested
- The result of the deployed test
