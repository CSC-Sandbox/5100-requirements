package edu.calpoly.provided;
import java.io.*; import java.net.*;
public class TestEnterMessage {
 private static final int PORT=5000;
 public static void main(String[] args){System.out.println("TestEnterMessage running on localhost:"+PORT); System.out.println("Run EnterMessage.java and send messages from the GUI."); try(ServerSocket server=new ServerSocket(PORT)){while(true){try(Socket socket=server.accept(); BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()))){String m=in.readLine(); if(m!=null) System.out.println("Received: "+m);}catch(IOException e){System.out.println("Connection ended. Waiting for the next message...");}}}catch(IOException e){System.err.println("Test server stopped: "+e.getMessage());}}
}
