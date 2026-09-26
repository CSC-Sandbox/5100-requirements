package edu.calpoly.analysis;

import java.util.ArrayList;
import java.util.List;

// Tests the circle analyzer w/ some circular, non-circular, and non-valid inputs.

public final class TestCircleAnalyzer {
    private TestCircleAnalyzer() {
    }

    public static void main(String[] args) {
        List<TestCase> tests = List.of(
                new TestCase("circle", circle(0, 0, 1, 24), ShapeClassification.CIRCLE),
                new TestCase("offset circle", circle(3, -2, 5, 24), ShapeClassification.CIRCLE),
                new TestCase("wobbly circle", wobblyCircle(24), ShapeClassification.CIRCLE),
                new TestCase("square", square(), ShapeClassification.NOT_A_CIRCLE),
                new TestCase("line", line(), ShapeClassification.NOT_A_CIRCLE),
                new TestCase("partial circle", arc(270, 24), ShapeClassification.NOT_A_CIRCLE),
                new TestCase("too few points", circle(0, 0, 1, 7), ShapeClassification.INSUFFICIENT_DATA),
                new TestCase("same point", identical(), ShapeClassification.INSUFFICIENT_DATA),
                new TestCase("invalid number", withNaN(), ShapeClassification.INSUFFICIENT_DATA),
                new TestCase("null input", null, ShapeClassification.INSUFFICIENT_DATA));

        CircleAnalyzer analyzer = new CircleAnalyzer();
        if (analyzer.getLastResult().isPresent()) {
            throw new AssertionError("result exists before analysis");
        }

        int passed = 0;
        for (TestCase test : tests) {
            ShapeClassification result = analyzer.analyze(test.points());
            if (result != test.expected()) {
                throw new AssertionError(
                        test.name() + ": expected " + test.expected().label()
                                + ", got " + result.label());
            }
            passed++;
        }

        if (analyzer.getLastResult().orElseThrow() != ShapeClassification.INSUFFICIENT_DATA) {
            throw new AssertionError("last result was not saved");
        }

        System.out.println("Circle analysis tests passed: " + passed + "/" + tests.size());
    }

    private static List<Coordinate> circle(
            double centerX, double centerY, double radius, int count) {
        List<Coordinate> points = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            double angle = 2.0 * Math.PI * i / count;
            points.add(new Coordinate(
                    centerX + radius * Math.cos(angle),
                    centerY + radius * Math.sin(angle)));
        }
        return points;
    }

    private static List<Coordinate> wobblyCircle(int count) {
        List<Coordinate> points = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            double angle = 2.0 * Math.PI * i / count;
            double radius = 1.0 + 0.02 * Math.sin(5.0 * angle);
            points.add(new Coordinate(radius * Math.cos(angle), radius * Math.sin(angle)));
        }
        return points;
    }

    private static List<Coordinate> square() {
        return List.of(
                new Coordinate(-1, -1), new Coordinate(0, -1), new Coordinate(1, -1),
                new Coordinate(1, 0), new Coordinate(1, 1), new Coordinate(0, 1),
                new Coordinate(-1, 1), new Coordinate(-1, 0));
    }

    private static List<Coordinate> line() {
        List<Coordinate> points = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            points.add(new Coordinate(i, i));
        }
        return points;
    }

    private static List<Coordinate> arc(double degrees, int count) {
        List<Coordinate> points = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            double angle = Math.toRadians(degrees) * i / (count - 1);
            points.add(new Coordinate(Math.cos(angle), Math.sin(angle)));
        }
        return points;
    }

    private static List<Coordinate> identical() {
        return List.of(
                new Coordinate(1, 1), new Coordinate(1, 1),
                new Coordinate(1, 1), new Coordinate(1, 1),
                new Coordinate(1, 1), new Coordinate(1, 1),
                new Coordinate(1, 1), new Coordinate(1, 1));
    }

    private static List<Coordinate> withNaN() {
        List<Coordinate> points = circle(0, 0, 1, 8);
        points.set(0, new Coordinate(Double.NaN, 0));
        return points;
    }

    private record TestCase(
            String name,
            List<Coordinate> points,
            ShapeClassification expected) {
    }
}
