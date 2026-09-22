package edu.calpoly.analysis;

// The three possible results of analyzing a coordinate sequence.
public enum ShapeClassification {
    CIRCLE("Circle"),
    NOT_A_CIRCLE("Not a circle"),
    INSUFFICIENT_DATA("Insufficient data");

    private final String text;

    ShapeClassification(String text) {
        this.text = text;
    }

    public String label() {
        return text;
    }
}
