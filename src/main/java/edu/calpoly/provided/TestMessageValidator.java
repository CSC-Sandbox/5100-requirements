package edu.calpoly.provided;

import java.util.List;

/**
 * Course-provided test program for Story #38:
 * Detect Malformed Messages.
 *
 * Expected MessageValidator interface:
 *
 *   public boolean validate(String message)
 *   public String getLastError()
 */
public class TestMessageValidator {

    private record TestCase(
            String message,
            boolean expectedValid,
            String description) {
    }

    private static final List<TestCase> TESTS = List.of(

            // Valid CSV
            new TestCase(
                    "ROBOT,0.42,-0.18,0.75,0.10,-0.32,0.57,0.25,0.10,0.42",
                    true,
                    "Valid ROBOT CSV"),

            new TestCase(
                    "GAZE,0.35,0.72",
                    true,
                    "Valid GAZE CSV"),

            new TestCase(
                    "AFFECT,0.72,0.41,0.65,0.58,0.33",
                    true,
                    "Valid AFFECT CSV"),

            new TestCase(
                    "LIDAR,1.25,-0.40,2.10",
                    true,
                    "Valid LIDAR CSV"),

            // Invalid CSV
            new TestCase(
                    "UNKNOWN,1,2,3",
                    false,
                    "Unknown message type"),

            new TestCase(
                    "ROBOT,0.42,-0.18,0.75",
                    false,
                    "ROBOT missing values"),

            new TestCase(
                    "GAZE,0.35,0.72,0.10",
                    false,
                    "GAZE too many values"),

            new TestCase(
                    "AFFECT,0.72,hello,0.65,0.58,0.33",
                    false,
                    "AFFECT non-numeric value"),

            new TestCase(
                    "LIDAR,1.25,-0.40",
                    false,
                    "LIDAR missing value"),

            new TestCase(
                    "GAZE,1.40,0.72",
                    false,
                    "GAZE value above range"),

            new TestCase(
                    "GAZE,-0.10,0.72",
                    false,
                    "GAZE value below range"),

            new TestCase(
                    "AFFECT,0.72,0.41,1.20,0.58,0.33",
                    false,
                    "AFFECT value above range"),

            new TestCase(
                    "",
                    false,
                    "Empty message"),

            // Valid JSON
            new TestCase(
                    "{\"type\":\"ROBOT\",\"data\":[0.42,-0.18,0.75,0.10,-0.32,0.57,0.25,0.10,0.42]}",
                    true,
                    "Valid ROBOT JSON"),

            new TestCase(
                    "{\"type\":\"GAZE\",\"data\":[0.35,0.72]}",
                    true,
                    "Valid GAZE JSON"),

            new TestCase(
                    "{\"type\":\"AFFECT\",\"data\":[0.72,0.41,0.65,0.58,0.33]}",
                    true,
                    "Valid AFFECT JSON"),

            new TestCase(
                    "{\"type\":\"LIDAR\",\"data\":[1.25,-0.40,2.10]}",
                    true,
                    "Valid LIDAR JSON"),

            // Invalid JSON
            new TestCase(
                    "{\"type\":\"GAZE\",\"data\":[1.40,0.72]}",
                    false,
                    "GAZE JSON value outside range"),

            new TestCase(
                    "{\"type\":\"LIDAR\",\"data\":[1.25,-0.40]}",
                    false,
                    "LIDAR JSON missing value"),

            new TestCase(
                    "{\"type\":\"AFFECT\",\"data\":[0.72,\"hello\",0.65,0.58,0.33]}",
                    false,
                    "AFFECT JSON non-numeric value"),

            new TestCase(
                    "{\"type\":\"ROBOT\",\"data\":[1,2,3]",
                    false,
                    "Malformed JSON"),

            // Make sure processing continues after invalid messages
            new TestCase(
                    "GAZE,0.50,0.50",
                    true,
                    "Valid message after invalid messages")
    );

    public static void main(String[] args) {

        MessageValidator validator = new MessageValidator();

        int passed = 0;

        System.out.println("TestMessageValidator");
        System.out.println("--------------------");

        for (int i = 0; i < TESTS.size(); i++) {

            TestCase test = TESTS.get(i);

            try {

                boolean actual = validator.validate(test.message());
                boolean success = actual == test.expectedValid();

                if (success) {
                    passed++;
                }

                System.out.printf(
                        "%02d. %-40s expected=%-7s actual=%-7s %s%n",
                        i + 1,
                        test.description(),
                        label(test.expectedValid()),
                        label(actual),
                        success ? "PASS" : "FAIL");

                if (!actual) {

                    String error = validator.getLastError();

                    if (error == null || error.isBlank()) {
                        System.out.println(
                                "    WARNING: No error information provided.");
                    } else {
                        System.out.println(
                                "    Reason: " + error);
                    }
                }

            } catch (Exception e) {

                System.out.printf(
                        "%02d. %-40s expected=%-7s actual=ERROR   FAIL%n",
                        i + 1,
                        test.description(),
                        label(test.expectedValid()));

                System.out.println(
                        "    Validator threw an exception: "
                                + e.getMessage());
            }
        }

        System.out.println();

        System.out.printf(
                "Result: %d/%d tests passed.%n",
                passed,
                TESTS.size());

        System.out.println(
                "These tests are not exhaustive. Add your own tests as needed.");
    }

    private static String label(boolean valid) {
        return valid ? "VALID" : "INVALID";
    }
}