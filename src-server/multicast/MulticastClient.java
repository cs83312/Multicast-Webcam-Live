package multicast;

import java.io.*;
 import java.net.*;
 import java.util.*;
 import java.lang.Math;
  
 public class MulticastClient extends java.lang.Thread{
  
     public static void main(String[] args) throws IOException {
  
    	 
         MulticastSocket socket = new MulticastSocket(4446);
         InetAddress address = InetAddress.getByName("230.0.0.1");
         socket.joinGroup(address);
         String uniqueID = UUID.randomUUID().toString();
         DatagramPacket packet;
      
         // get a few quotes
         for (int i = 0; i < 5; i++) {
  
             byte[] buf = new byte[256];
             packet = new DatagramPacket(buf, buf.length);
             socket.receive(packet);
  
             String received = new String(packet.getData(), 0, packet.getLength());
             System.out.println("Quote of the Moment: " + received+" id:"+uniqueID);
         }
  
         socket.leaveGroup(address);
         socket.close();
     }
 }
