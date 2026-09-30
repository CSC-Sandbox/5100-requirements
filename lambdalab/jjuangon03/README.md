# Lambda Lab — GazeClusterLambda

**Package:** `edu.calpoly.lambdalab.jjuangon03`  
**Class:** `GazeClusterLambda`  
**HTTP method:** `POST`  
**Route:** `/5100/gaze-clusters`  
**Public endpoint:** `https://api.javiergs.phd/5100/gaze-clusters`

## Problem

Detect gaze zones using DBSCAN from normalized x/y gaze points. The DBSCAN helper logic will be provided; integrate it with the Lambda/API contract.

## Contract

**Input:** JSON: points [{x,y}], epsilon, minPoints  
**Output:** numberOfClusters and clusters with id, centerX, centerY, points

Return HTTP `200` for a valid request and HTTP `400` for invalid input. Keep business logic separate from the AWS/API Gateway adapter in `handleRequest()`.

## Your Work

Create:

- `src/main/java/edu/calpoly/lambdalab/jjuangon03/GazeClusterLambda.java`
- `src/test/java/edu/calpoly/lambdalab/jjuangon03/GazeClusterLambdaTest.java`

Use the instructor `TemperatureLambda` and its tests as the reference for API Gateway request/response handling. Implement the assigned behavior and write at least three meaningful JUnit tests.

Required test cases: **Two clear clusters; one cluster; invalid/insufficient input**.

## Test the Deployed Lambda

After your code passes GitHub Actions and is deployed:

Run from the IntelliJ Terminal:

`curl -X POST "https://api.javiergs.phd/5100/gaze-clusters" -H "Content-Type: application/json" -d '{"points":[{"x":0.10,"y":0.20},{"x":0.12,"y":0.21},{"x":0.75,"y":0.70},{"x":0.78,"y":0.72}],"epsilon":0.08,"minPoints":2}'`

Your response must be JSON and follow the contract above.

## Pull Request

Create **one PR** for this lab. Do not create an Issue, Story, or Task.

PR title:

`Lambda Lab - GazeClusterLambda - <GitHubID>`

In the PR description include:

- Lambda: `GazeClusterLambda`
- Method and route: `POST /5100/gaze-clusters`
- Confirmation that JUnit tests pass
- Confirmation that `mvn clean package` succeeds
- The `api.javiergs.phd` endpoint tested
- The result of the deployed test
