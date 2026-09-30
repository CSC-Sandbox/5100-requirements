package edu.calpoly.robot;

import java.io.IOException;
import java.net.http.HttpResponse;

/**
 * TestRobotRESTAdapter is a test for the REST adapter.
 * Test Instructions: 
 *  1. Start RobotServer on localHost, 
 *  2. Start TestRobotRESTAdapter. 
 *  3. You should see prints of test cases and wether the check passed.
 * @author Paul Motter (PaulMotter)
 * @version 1.0.0 (9/27/2026)
 */
public class TestRobotRESTAdapter {

    private static final String VALID_MESSAGE =
        "{\"jointAngles\":[2.0,1.75,1.0,0.5,0.25,0.0],"
        + "\"position\":[0.5,-10.0,5.25]}";

private static final String INVALID_MESSAGE =
        "{\"jointAngles\":[1.0,2.0],\"position\":[0.0]}";
    public static void main(String[] args) throws IOException, InterruptedException {
        RobotRESTProvider provider = new RobotRESTProvider(
            RobotServer.REST_PORT,
            RobotServer.ROBOT_ID);

        HttpResponse<String> response;
        String testName;

        testName = "PUT Valid Message";
        response = provider.requestPUT(VALID_MESSAGE);
        checkInt(200, response.statusCode(), testName+" Status 200");
        checkString(VALID_MESSAGE, response.body(), testName+" Body Match");

        testName = "PUT Invalid Message";
        response = provider.requestPUT(INVALID_MESSAGE);
        checkInt(400, response.statusCode(), testName+" Status 400");
        checkString("Invalid JSON", response.body(), testName+" Body Match");

        testName = "GET";
        response = provider.requestGET();
        checkInt(200, response.statusCode(), testName+" Status 200");
        checkString(VALID_MESSAGE, response.body(), testName+" Body Match");
    }

    private static void checkString(String expected, String actual, String testName){
        if (expected.equals(actual)) System.out.println("[pass] "+testName);
        else System.out.println("[fail] "+testName);
    }

    private static void checkInt(int expected, int actual, String testName){
    if (expected == actual) System.out.println("[pass] "+testName);
    else System.out.println("[fail] "+testName);
    }
}
