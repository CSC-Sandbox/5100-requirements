package edu.calpoly.analysis;

import java.util.List;
import java.util.Optional;

/**
 * Analyzes an ordered sequence of coordinates to determine whether
 * the points form a complete circle.
 *
 * @author Jess A
 * @version September 25, 2026
 */

public final class CircleAnalyzer {
    private static final int MIN_POINTS = 8;
    private static final double MAX_NORMALIZED_RADIAL_ERROR = 0.05;

    private ShapeClassification lastResult;


    /**
     * Classifies an ordered sequence of coordinates.
     *
     * @param points coordinates describing the shape boundary
     * @return the resulting shape classification
     * 
     */
    public ShapeClassification analyze(List<Coordinate> points) {
        if (!isValid(points)) {
            lastResult = ShapeClassification.INSUFFICIENT_DATA;
            return lastResult;
        }

        Circle circle = estimateCircle(points);
        boolean round = radialError(points, circle) <= MAX_NORMALIZED_RADIAL_ERROR;
        boolean complete = isComplete(points, circle);

        lastResult = round && complete
                ? ShapeClassification.CIRCLE
                : ShapeClassification.NOT_A_CIRCLE;
        return lastResult;
    }


    /**
     * Returns the result of the most recent analysis.
     *
     * @return the previous result, or an empty optional (if none exists)
     */
    public Optional<ShapeClassification> getLastResult() {
        return Optional.ofNullable(lastResult);
    }

    private boolean isValid(List<Coordinate> points) {
        if (points == null || points.size() < MIN_POINTS) {
            return false;
        }

        Coordinate first = points.get(0);
        if (!isFinite(first)) {
            return false;
        }


        boolean hasDifferentPoint = false;
        for (Coordinate point : points) {
            if (!isFinite(point)) {
                return false;
            }
            if (!point.equals(first)) {
                hasDifferentPoint = true;
            }
        }
        return hasDifferentPoint;
    }

    private boolean isFinite(Coordinate point) {
        return point != null
                && Double.isFinite(point.x())
                && Double.isFinite(point.y());
    }

    private Circle estimateCircle(List<Coordinate> points) {
        double minX = points.get(0).x();
        double maxX = minX;
        double minY = points.get(0).y();
        double maxY = minY;

        for (Coordinate point : points) {
            minX = Math.min(minX, point.x());
            maxX = Math.max(maxX, point.x());
            minY = Math.min(minY, point.y());
            maxY = Math.max(maxY, point.y());
        }

        double centerX = (minX + maxX) / 2.0;
        double centerY = (minY + maxY) / 2.0;
        double radius = 0.0;

        for (Coordinate point : points) {
            radius += Math.hypot(point.x() - centerX, point.y() - centerY);
        }

        return new Circle(centerX, centerY, radius / points.size());
    }

    private double radialError(List<Coordinate> points, Circle circle) {
        if (circle.radius() == 0.0) {
            return Double.POSITIVE_INFINITY;
        }

        double total = 0.0;
        for (Coordinate point : points) {
            double distance = Math.hypot(
                    point.x() - circle.centerX(),
                    point.y() - circle.centerY());
            double error = distance - circle.radius();
            total += error * error;
        }

        return Math.sqrt(total / points.size()) / circle.radius();
    }

    private boolean isComplete(List<Coordinate> points, Circle circle) {
        for (int i = 0; i < points.size(); i++) {
            Coordinate current = points.get(i);
            Coordinate next = points.get((i + 1) % points.size());
            double gap = Math.hypot(next.x() - current.x(), next.y() - current.y());
            if (gap > circle.radius()) {
                return false;
            }
        }
        return true;
    }

    private record Circle(double centerX, double centerY, double radius) {
    }
}
