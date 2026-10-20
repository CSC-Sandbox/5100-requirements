# Shape Analysis Feature Evolution

## Feature Type

This feature will become a local Java library/API. Other Java code can call it directly to classify coordinates. I believe that we currently do not need REST or MQTT b/c the analysis is a small local calculation.

## Contract

### Analyze Coordinates

- **Input:** An ordered `List<Coordinate>` containing at least eight finite points.
- **Input format:** Java objects.
- **Input interface:** `analyze(List<Coordinate> points)`.
- **Output:** `CIRCLE`, `NOT_A_CIRCLE`, or `INSUFFICIENT_DATA`.
- **Output format:** `ShapeClassification` enum.
- **Output interface:** Java return value.

### Read Previous Result

- **Input:** None.
- **Interface:** `getLastResult()`.
- **Output:** The previous classification, if one exists.
- **Output format:** `Optional<ShapeClassification>`.

## Dependencies

The feature uses Java collections and math utilities. A coordinate-gathering component provides the input points. A display, storage, or other component can use the returned classification.

A future `ShapeAnalyzer` interface would allow another analyzer to replace `CircleAnalyzer` or support additional shapes.

## Refactoring Opportunities

### Completed

- Renamed `MAX_ERROR` to `MAX_NORMALIZED_RADIAL_ERROR` to make its purpose clearer.

### Future Work

- Add a `ShapeAnalyzer` interface so other shapes can be supported.
- Convert the manual tests to JUnit so Maven can run them automatically.
- Make the point and error limits configurable.
- Add a separate result for invalid input.
- Remove stored results from `CircleAnalyzer` if they are not needed.