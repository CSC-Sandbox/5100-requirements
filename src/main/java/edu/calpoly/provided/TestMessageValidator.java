package edu.calpoly.provided;

import edu.calpoly.message.MessageValidator;

import java.util.List;

/**
 * Test program for the MessageValidator class.
 *
 * Course-provided test program for Story #38:
 * Testing the MessageValidator class.
 *
 * @author Javier Gonzalez-Sanchez (javiergs)
 * @version 1.0 (2026-09-01)
 */
public class TestMessageValidator {

  private record TestCase(
      String message,
      boolean expectedValid,
      String description) {
  }

  private static final List<TestCase> TESTS = List.of(

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

      // ----------------------------------------------------------
      // Additional CSV cases: case sensitivity, whitespace, counts
      // ----------------------------------------------------------

      new TestCase(
          "robot,0.42,-0.18,0.75,0.10,-0.32,0.57,0.25,0.10,0.42",
          true,
          "ROBOT CSV lowercase type accepted"),

      new TestCase(
          "Robot,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9",
          true,
          "ROBOT CSV mixed-case type accepted"),

      new TestCase(
          "gaze,0.35,0.72",
          true,
          "GAZE CSV lowercase type accepted"),

      new TestCase(
          " ROBOT , 0.42 , -0.18 , 0.75 , 0.10 , -0.32 , 0.57 , 0.25 , 0.10 , 0.42 ",
          true,
          "ROBOT CSV with extra surrounding whitespace"),

      new TestCase(
          "GAZE, 0.35 , 0.72 ",
          true,
          "GAZE CSV with whitespace around values"),

      new TestCase(
          "ROBOT,1e2,-0.18,0.75,0.10,-0.32,0.57,0.25,0.10,0.42",
          true,
          "ROBOT CSV with scientific notation value"),

      new TestCase(
          "LIDAR,1.0,2.0,3.0",
          true,
          "Valid LIDAR CSV with all-positive values"),

      new TestCase(
          "GAZE,0.0,1.0",
          true,
          "GAZE CSV boundary values valid (0.0 and 1.0)"),

      new TestCase(
          "AFFECT,0.0,0.0,0.0,0.0,0.0",
          true,
          "AFFECT CSV all-zero boundary valid"),

      new TestCase(
          "AFFECT,1.0,1.0,1.0,1.0,1.0",
          true,
          "AFFECT CSV all-one boundary valid"),

      new TestCase(
          "ROBOT,0.42,-0.18,0.75,0.10,-0.32,0.57,0.25,0.10,0.42,1.0",
          false,
          "ROBOT CSV too many values"),

      new TestCase(
          "GAZE,0.0,1.0001",
          false,
          "GAZE CSV just above upper boundary"),

      new TestCase(
          "GAZE,-0.0001,0.5",
          false,
          "GAZE CSV just below lower boundary"),

      new TestCase(
          "AFFECT,0.5,0.5,0.5,0.5",
          false,
          "AFFECT CSV missing one value"),

      new TestCase(
          "AFFECT,0.5,0.5,0.5,0.5,0.5,0.5",
          false,
          "AFFECT CSV too many values"),

      new TestCase(
          "LIDAR,1.0,2.0,3.0,4.0",
          false,
          "LIDAR CSV too many values"),

      new TestCase(
          "LIDAR,abc,2.0,3.0",
          false,
          "LIDAR CSV non-numeric value"),

      new TestCase(
          "ROBOT,0.1,0.2,0.3,0.4,0.5,0.6,x,0.8,0.9",
          false,
          "ROBOT CSV non-numeric value"),

      new TestCase(
          "GAZE,,0.5",
          false,
          "GAZE CSV with empty field"),

      new TestCase(
          "GAZE,0.5,",
          false,
          "GAZE CSV with trailing empty field"),

      new TestCase(
          "   ",
          false,
          "Whitespace-only message"),

      new TestCase(
          null,
          false,
          "Null message"),

      new TestCase(
          "ROBOT",
          false,
          "ROBOT type only, no values"),

      new TestCase(
          "not json at all { but starts weird",
          false,
          "Brace mid-string treated as CSV, unknown type"),

      // ----------------------------------------------------------
      // Additional JSON cases: structure, types, boundaries
      // ----------------------------------------------------------

      new TestCase(
          "{\"type\":\"robot\",\"data\":[0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9]}",
          true,
          "ROBOT JSON lowercase type accepted"),

      new TestCase(
          "  {\"type\": \"LIDAR\", \"data\": [1.0, 2.0, 3.0]}  ",
          true,
          "LIDAR JSON with surrounding whitespace"),

      new TestCase(
          "{\"type\":\"GAZE\",\"data\":[0.0,1.0]}",
          true,
          "GAZE JSON boundary values valid (0.0 and 1.0)"),

      new TestCase(
          "{\"type\":\"AFFECT\",\"data\":[0.0,0.0,0.0,0.0,0.0]}",
          true,
          "AFFECT JSON all-zero boundary valid"),

      new TestCase(
          "{\"type\":\"AFFECT\",\"data\":[1.0,1.0,1.0,1.0,1.0]}",
          true,
          "AFFECT JSON all-one boundary valid"),

      new TestCase(
          "{\"type\":\"GAZE\",\"data\":[0.0,1.0001]}",
          false,
          "GAZE JSON just above upper boundary"),

      new TestCase(
          "{\"type\":\"AFFECT\",\"data\":[0.72,0.41,1.0001,0.58,0.33]}",
          false,
          "AFFECT JSON value just above upper boundary"),

      new TestCase(
          "{\"type\":\"LIDAR\",\"data\":[1.0,2.0,3.0,4.0]}",
          false,
          "LIDAR JSON too many values"),

      new TestCase(
          "{\"type\":\"ROBOT\",\"data\":[0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8]}",
          false,
          "ROBOT JSON missing one value"),

      new TestCase(
          "{\"type\":\"LIDAR\",\"data\":[1.0,2.0,\"three\"]}",
          false,
          "LIDAR JSON non-numeric value"),

      new TestCase(
          "{\"type\":\"UNKNOWN\",\"data\":[1,2,3]}",
          false,
          "Unknown message type in JSON"),

      new TestCase(
          "{\"data\":[0.1,0.2]}",
          false,
          "JSON missing 'type' field"),

      new TestCase(
          "{\"type\":\"GAZE\"}",
          false,
          "JSON missing 'data' field"),

      new TestCase(
          "{\"type\":123,\"data\":[0.1,0.2]}",
          false,
          "JSON 'type' field is not a string"),

      new TestCase(
          "{\"type\":\"GAZE\",\"data\":\"not-an-array\"}",
          false,
          "JSON 'data' field is not an array"),

      new TestCase(
          "[1,2,3]",
          false,
          "JSON top-level array instead of object"),

      new TestCase(
          "{}",
          false,
          "Empty JSON object"),

      new TestCase(
          "{\"type\": \"GAZE\", \"data\": [0.35, 0.72],}",
          false,
          "Malformed JSON with trailing comma"),

      new TestCase(
          "{'type':'GAZE','data':[0.35,0.72]}",
          false,
          "Malformed JSON with single quotes"),

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
            "%02d. %-55s expected=%-7s actual=%-7s %s%n",
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
            "%02d. %-55s expected=%-7s actual=ERROR   FAIL%n",
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