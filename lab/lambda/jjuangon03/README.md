# Lambda Lab — GazeClusterLambda

**Package:** `edu.calpoly.lambdalab.jjuangon03`  
**Class:** `GazeClusterLambda`  
**HTTP method:** `POST`  
**Route:** `/5100/gaze-clusters`  
**Public endpoint:** `https://api.javiergs.phd/5100/gaze-clusters`

## Problem

Receive normalized gaze points `(x,y)` and identify gaze zones using DBSCAN clustering.

**The goal of this lab is AWS Lambda integration, testing, CI/CD, and deployment — not implementing DBSCAN.** The clustering algorithm below is provided. You may use it as-is.

## Contract

Request body:

```json
{
  "points": [
    {"x": 0.10, "y": 0.20},
    {"x": 0.12, "y": 0.21},
    {"x": 0.11, "y": 0.18},
    {"x": 0.75, "y": 0.70},
    {"x": 0.78, "y": 0.72},
    {"x": 0.76, "y": 0.69}
  ],
  "epsilon": 0.08,
  "minPoints": 2
}
```

Response:

```json
{
  "numberOfClusters": 2,
  "clusters": [
    {"id": 0, "centerX": 0.11, "centerY": 0.20, "points": 3},
    {"id": 1, "centerX": 0.76, "centerY": 0.70, "points": 3}
  ]
}
```

Return HTTP `200` for a valid request and HTTP `400` for invalid input.

## Provided DBSCAN Algorithm

Use this algorithm as the business-logic component. You do **not** need to design or reimplement DBSCAN.

```text
DBSCAN(points, epsilon, minPoints)

    clusterId = 0
    labels = all UNVISITED

    for each point P in points

        if P is already visited
            continue

        mark P as visited

        neighbors = all points whose
                    Euclidean distance from P <= epsilon

        if number of neighbors < minPoints
            mark P as NOISE
            continue

        create cluster clusterId
        add P to cluster

        queue = neighbors

        while queue is not empty

            Q = remove next point from queue

            if Q has not been visited

                mark Q as visited

                qNeighbors = all points whose
                             Euclidean distance from Q <= epsilon

                if number of qNeighbors >= minPoints
                    add qNeighbors to queue

            if Q is not already assigned to a cluster
                assign Q to cluster clusterId

        clusterId = clusterId + 1

    return clusters
```

Distance between two gaze points:

```text
distance(P, Q) =
    sqrt((P.x - Q.x)^2 + (P.y - Q.y)^2)
```

For each resulting cluster, calculate:

```text
centerX = average x of all points in cluster
centerY = average y of all points in cluster
points  = number of points in cluster
```

Noise points are not returned as clusters.

## Your Work

Create:

- `src/main/java/edu/calpoly/lambdalab/jjuangon03/GazeClusterLambda.java`
- `src/test/java/edu/calpoly/lambdalab/jjuangon03/GazeClusterLambdaTest.java`

Focus on the Lambda layer:

1. Read the API Gateway POST body.
2. Deserialize the JSON request.
3. Validate `points`, `epsilon`, and `minPoints`.
4. Call the provided DBSCAN business logic.
5. Convert the clusters into the required JSON response.
6. Return the appropriate HTTP status.
7. Keep the clustering logic separate from `handleRequest()`.

Use the instructor `TemperatureLambda` as the reference for API Gateway request/response handling.

Write at least three meaningful JUnit tests:

- two obvious gaze clusters,
- one gaze cluster,
- invalid or insufficient input.

## Test the Deployed Lambda

After your code passes GitHub Actions and is deployed, run this from the IntelliJ Terminal:

`curl -X POST "https://api.javiergs.phd/5100/gaze-clusters" -H "Content-Type: application/json" -d '{"points":[{"x":0.10,"y":0.20},{"x":0.12,"y":0.21},{"x":0.11,"y":0.18},{"x":0.75,"y":0.70},{"x":0.78,"y":0.72},{"x":0.76,"y":0.69}],"epsilon":0.08,"minPoints":2}'`

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
