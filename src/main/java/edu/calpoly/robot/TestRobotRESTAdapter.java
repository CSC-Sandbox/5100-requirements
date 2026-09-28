package edu.calpoly.robot;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;;

/**
 * TestRobotRESTAdapter is a test for the REST adapter.
 * Test Instructions: 
 *  1. Start RobotServer on localHost, 
 *  2. Start TestRobotRESTAdapter. 
 *  3. You should see prints of the communication check wether posts and returns are equivalent.
 * @author Paul Motter (PaulMotter)
 * @version 1.0.0 (9/27/2026)
 */
public class TestRobotRESTAdapter {
    public static void main(String[] args) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        // Sends PUT request.
        String PUTBody = "{\"jointAngles\":[2.0,1.75,1.0,0.5,0.25,0.0],\"position\":[0.5,-10.0,5.25]}";
        HttpRequest PUTRequest = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:5001/robot/1"))
            .PUT(BodyPublishers.ofString(PUTBody))
            .build();
        System.out.println("\nPUT: " + PUTBody);
        
        // Gets PUT response.
        HttpResponse<String> PUTResponse = client.send(PUTRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println("Return: Status=" + PUTResponse.statusCode() + " Body=" + PUTResponse.body());
    
        // Sends GET request.
        HttpRequest GETRequest = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:5001/robot/1"))
            .GET()
            .build();
            
    
        // Gets GET response.
        HttpResponse<String> GETResponse = client.send(GETRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println("\nGET Return: Status=" + GETResponse.statusCode() + " Body=" + GETResponse.body());
    
        // Print If data equals what is expected.
        if (PUTBody.equals(PUTResponse.body()))System.out.println("\nCorrect: PUTBody equals PUTReturn");
        else System.out.println("\nIncorrect: PUTBody does not equal PUTReturn");

        if (PUTBody.equals(GETResponse.body())) System.out.println("Correct: PUTBody equals GETBody");
        else System.out.println("Incorrect: PUTBody does not equal GETBody");

        // Sends malformed PUT request. only 5 joints and 4 postion values.
        String badPUTBody = "{\"jointAngles\":[1.75,1.0,0.5,0.25,0.0],\"position\":[0.5,-10.0,5.25,-18]}";
        HttpRequest badPUTRequest = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:5001/robot/1"))
            .PUT(BodyPublishers.ofString(badPUTBody))
            .build();
        System.out.println("\nbadPUT: " + badPUTBody);
        

        // Gets PUT response.
        HttpResponse<String> badPUTResponse = client.send(badPUTRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println("Return: Status=" + badPUTResponse.statusCode() + " Body=" + badPUTResponse.body());
    }
}
