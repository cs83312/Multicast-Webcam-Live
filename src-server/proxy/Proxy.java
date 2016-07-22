package proxy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/*		*
 * 	File:						proxy/Proxy.java
 * 
 * 	Use:						The SIP proxy server. 
 * 								It will listen to wait User Agent to connect.
 * 								And pass the packet to proxy/Client.java to do something    
 * 
 * 	Update Date: 	2016.  5. 23
 * */

public class Proxy extends java.lang.Thread {
	private boolean outServer = false; 
	private ServerSocket server; 
	private int serverPort; 
	
	private Socket client;   
	private final String clientAddress = "127.0.0.1"; 
	private final int clientPort = 13577; 
	InetSocketAddress isa; 
	
	public Proxy(int port){
		try {
			serverPort = port; 
			server = new ServerSocket(serverPort);
			
			client = new Socket(); 
			isa = new InetSocketAddress(clientAddress, clientPort); 
		}
		catch (IOException e) {
			System.out.println("Socket start failed. "); 
			System.out.println("IOException: " + e.toString()); 
		}
	}
	
	public void run(){
		Socket socketServer; 
		BufferedInputStream uaIn;
		BufferedOutputStream uaOut; 
		
		BufferedInputStream proxyIn;
		BufferedOutputStream proxyOut; 
		
		System.out.println("Server start!!"); 
		
		while(!outServer){
			socketServer = null; 
			try{
				synchronized(server){
					socketServer = server.accept(); 
				}
				System.out.println("Connect: InetAddress = " + socketServer.getInetAddress()); 
				socketServer.setSoTimeout(15000); 
				
				
				uaIn = new BufferedInputStream(socketServer.getInputStream()); 
				byte[] b = new byte[1024]; 
				int length = uaIn.read(b); 
				String data = new String(b, 0, length); 
				System.out.println("The data I got from UA: " + data);
				
				proxyOut = new BufferedOutputStream(client.getOutputStream());
				String srcIP = socketServer.getInetAddress().toString(); 
				proxyOut.write((data + "," + srcIP).getBytes()); 
				proxyOut.flush(); 
				
				proxyIn = new BufferedInputStream(client.getInputStream());  
				length = proxyIn.read(b);
				data = new String(b, 0, length); 
				System.out.println("The data I got from server: " + data); 
				
				uaOut = new BufferedOutputStream(socketServer.getOutputStream()); 
				uaOut.write(data.getBytes()); 
				uaOut.flush(); 
				
				
				uaIn.close();
				uaOut.close(); 
				proxyIn.close(); 
				proxyOut.close(); 
				uaIn = null; 
				uaOut = null; 
				proxyIn = null; 
				proxyOut = null; 
				socketServer.close(); 
				client.close(); 
			}
			catch(IOException e){
				System.out.println("Socket connection failed. "); 
				System.out.println("IOException: " + e.toString()); 
			}
		}
	}		
}