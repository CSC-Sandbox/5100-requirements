package edu.calpoly.analysis;


/**
 * Defines the possible results of analyzing a coordinate sequence.
 *
 * @author Jess A
 * @version September 25, 2026
 */

public enum ShapeClassification {
    CIRCLE("Circle"),
    NOT_A_CIRCLE("Not a circle"),
    INSUFFICIENT_DATA("Insufficient data");

    private final String text;

    ShapeClassification(String text) {
        this.text = text;
    }

    /**
     * Returns the readable label for this classification.
     *
     * @return the classification label
     */

    public String label() {
        return text;
    }
}