package edu.calpoly.provided;
import java.io.*; import java.net.*;
/** Course-provided receiver used to test GatherRobot.java. */
public class TestGatherRobot {
 private static final int PORT=5000;
 public static void main(String[] args){
  System.out.println("Waiting for robot pose data on localhost:"+PORT+"...");
  System.out.println("Expected format: ROBOT,J1,J2,J3,J4,J5,J6,X,Y,Z");
  try(ServerSocket server=new ServerSocket(PORT)){while(true){try(Socket socket=server.accept(); BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()))){String m=in.readLine(); if(m!=null){System.out.println("Robot Pose Received"); System.out.println(m); if(!m.startsWith("ROBOT,")) System.out.println("WARNING: message should begin with ROBOT,");}}}}catch(IOException e){System.err.println("Test server stopped: "+e.getMessage());}
 }
}
