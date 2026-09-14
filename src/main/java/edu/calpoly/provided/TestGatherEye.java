package edu.calpoly.provided;
import java.io.*; import java.net.*;
/** Course-provided receiver used to test GatherEye.java. */
public class TestGatherEye {
 private static final int PORT=5000;
 public static void main(String[] args){
  System.out.println("Waiting for gaze data on localhost:"+PORT+"..."); System.out.println("Expected format: GAZE,X,Y");
  try(ServerSocket server=new ServerSocket(PORT)){while(true){try(Socket socket=server.accept(); BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()))){String m=in.readLine(); if(m!=null){System.out.println("Gaze Position Received"); System.out.println(m); if(!m.startsWith("GAZE,")) System.out.println("WARNING: message should begin with GAZE,");}}}}catch(IOException e){System.err.println("Test server stopped: "+e.getMessage());}
 }
}
