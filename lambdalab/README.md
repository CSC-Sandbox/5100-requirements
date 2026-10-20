# CSC 5100 Lambda Lab

Each student has an individual Lambda problem in the package matching their normalized GitHub username.

## Goal

Implement and test a small Java AWS Lambda, submit it through a clearly identified Pull Request, and verify the deployed service through `api.javiergs.phd`.

Use the instructor `TemperatureLambda` as the reference: Java + JUnit → GitHub → GitHub Actions → AWS Lambda → API Gateway.

## Submission

1. Work only in your assigned package and corresponding test package.
2. Keep business logic separate from `handleRequest()`.
3. Add at least three meaningful JUnit tests: normal, boundary, and invalid input.
4. Verify `mvn clean package` locally.
5. Push your branch and create one Pull Request.
6. PR title: `Lambda Lab - <LambdaName> - <GitHubID>`.
7. Clearly identify the Lambda Lab and tested endpoint in the PR description.
8. Do not create an Issue, Story, or Task for this lab.

GET assignments can be tested directly in a browser. POST assignments include a one-line `curl` command intended for the IntelliJ Terminal.

## Assignments

- `jjuangon03` — GazeClusterLambda — POST `/5100/gaze-clusters`
- `aidenrodriguez` — EncryptMessageLambda — GET `/5100/encrypt`
- `adrian0427` — DominantAffectLambda — POST `/5100/dominant-affect`
- `oxlojalencas` — NearestSampleLambda — POST `/5100/nearest-sample`
- `paulmotter` — RobotDistanceLambda — GET `/5100/robot-distance`
- `edgarddragde` — RobotReachabilityLambda — GET `/5100/robot-reachability`
- `briggsmurphy` — LidarClosestLambda — POST `/5100/lidar-closest`
- `ejiswkrosmew` — LidarSafetyLambda — POST `/5100/lidar-safety`
- `hwrd22` — DataFreshnessLambda — GET `/5100/data-freshness`
- `daome` — MovingAverageLambda — POST `/5100/moving-average`
- `dylangururajan` — CoordinateNormalizerLambda — GET `/5100/coordinate-normalizer`
- `wliu4040` — EventRateLambda — GET `/5100/event-rate`
