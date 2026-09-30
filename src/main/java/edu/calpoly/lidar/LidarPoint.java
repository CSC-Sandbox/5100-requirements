package edu.calpoly.lidar;

/**
 * Represents 1 LiDAR measurement in 3D space.
 * The map uses x and y while retaining z from the received message.
 *
 * @author Jess A
 * @version September 30, 2026
 */
public record LidarPoint(double x, double y, double z) {
}