package edu.calpoly.robot;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

/**
 * RobotRESTAdapter is an adapter class for the passed in RobotBlackBoard that
 * updates and retrieves the blackboard data everytime a REST request is given.
 * 
 * @author Paul Motter (PaulMotter)
 * @version 1.0.0 (9/26/2026)
 */
public class RobotRESTAdapter extends Thread{
    /* expected JSON format
        {
            "jointAngles": [v1, v2, v3, v4, v5, v6],
            "postiions": [x, y, z]    
        }
    */
    private ObjectMapper om;
    private HttpServer server;
    private int robotID;
    private Consumer<RobotMessage> onPUT;

    /**
     * Creates a server that can run on a thread.
     * Listens for requests on the resource "robot/<robotID>""
     * @param port The port on which the server runs.
     * @param robotID The resource object id.
     * @throws IOException from creating the server.
     */
    public RobotRESTAdapter(int RESTPort, int robotID, RobotBlackBoard rbb) throws IOException{
        this.robotID = robotID;
        this.onPUT = rbb::post;
        // Create JSON parser.
        om = new ObjectMapper();

        // Create server.
        server = HttpServer.create(
            new InetSocketAddress(RESTPort), 0
        );

        // Setup Server.
        server.createContext("/robot/" + robotID, exchange -> {
            // Get the http method.
            String method = exchange.getRequestMethod().toUpperCase();
            // Match http method.
            switch (method) {
                case "GET":
                    // Insert GET method
                    reply(404, "GET does not exist.".getBytes(), exchange);
                    break;
                case "POST":
                    reply(404, "POST does not exist.".getBytes(), exchange);
                    break;
                case "PUT":
                    robotPUT(exchange);
                    break;
                case "DELETE":
                    reply(404, "DELETE does not exist.".getBytes(), exchange);
                    break;
                default:
                    reply(404, "Method does not exist.".getBytes(), exchange);
                    break;
            }
        });
    }

    /**
     * Defines the PUT method for the context.
     * @param exchange
     */
    private void robotPUT(HttpExchange exchange){
        // Parse JSON
        RobotMessage rm;
        try {
            rm = om.readValue(
                exchange.getRequestBody(),
                RobotMessage.class
            );
        } catch (IOException e){
            // Invalid JSON
            reply(400, "Invalid JSON".getBytes(), exchange);
            return;
        }
        
        // Complete Action
        onPUT.accept(rm);

        // Try to Respond.
        try {
            byte[] reply = om.writeValueAsBytes(rm);
            reply(200, reply, exchange);
        } catch (IOException e){}
        return;
    }


    /**
     * Starts the server created in the constructor.
     * @see java.lang.Thread#run()
     */
    public void run(){
        server.start();
        InetSocketAddress address = server.getAddress();
        System.out.println("REST server running at " 
        + "http://" + address.getHostString() + ":" + address.getPort() + "/robot/" + robotID);
    }

    /**
     * A response handler for the exchange. Define the statusCode, body, and the exchange to send on.
     * Returns whether there was an error sending the reply.
     * @param statusCode
     * @param body
     * @param exchange
     * @return
     */
    private static boolean reply(int statusCode, byte[] body, HttpExchange exchange){
        try {
            // Attempt to send.
            exchange.sendResponseHeaders(
                statusCode, body.length
            );
        
            OutputStream os = exchange.getResponseBody();
            os.write(body);
            os.close();

            exchange.close();

            return true;
        } catch (IOException | IllegalArgumentException e){
            //failed to send.
            return false;
        }
    }

    
}

