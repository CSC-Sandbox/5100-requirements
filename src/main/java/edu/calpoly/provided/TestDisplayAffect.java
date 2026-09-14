package edu.calpoly.provided;

import java.io.*; import java.net.*; import java.util.Locale;
/** Course-provided affective-data simulator used to test DisplayAffect.java. */
public class TestDisplayAffect {
 private static final int PORT=5000; private static final long DELAY_MS=200;
 public static void main(String[] args){System.out.println("TestDisplayAffect running on localhost:"+PORT); System.out.println("Run DisplayAffect.java and observe the affective-state chart."); try(ServerSocket server=new ServerSocket(PORT)){while(true){try(Socket socket=server.accept(); BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream())); PrintWriter out=new PrintWriter(socket.getOutputStream(),true)){String mode=in.readLine(); if(!"RECEIVE".equals(mode)) continue; System.out.println("DisplayAffect connected. Sending simulated affective data..."); sendAffect(out);}catch(IOException e){System.out.println("DisplayAffect disconnected. Waiting for another connection...");}}}catch(IOException e){System.err.println("Test server stopped: "+e.getMessage());}}
 private static void sendAffect(PrintWriter out) throws IOException{int sample=0; while(!out.checkError()){double t=sample*.10; double focus=wave(t,0,.55,.35), excitement=wave(t,1.2,.45,.30), engagement=wave(t*.70,2.1,.65,.25), interest=wave(t*.85,3,.55,.30), stress=wave(t*.55,4,.35,.25); out.printf(Locale.US,"AFFECT,%.2f,%.2f,%.2f,%.2f,%.2f%n",focus,excitement,engagement,interest,stress); out.flush(); sample++; sleep();}}
 private static double wave(double t,double phase,double center,double amp){double v=center+amp*Math.sin(t+phase); return Math.max(0,Math.min(1,v));}
 private static void sleep() throws IOException{try{Thread.sleep(DELAY_MS);}catch(InterruptedException e){Thread.currentThread().interrupt(); throw new IOException("Affective-data simulation interrupted",e);}}
}
