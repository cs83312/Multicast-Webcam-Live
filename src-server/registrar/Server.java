package registrar;

import java.io.*;
import java.lang.*; 
import java.net.*;

/*		*
 * 	File:						registrar/Server.java
 * 
 * 	Use:						The SIP registrar server. 
 * 								It will listen to wait Proxy Server or User Agent to connect.    
 * 
 * 	Update Date: 	2016.  6. 2
 * */

public class Server extends java.lang.Thread {
	private boolean outServer = false; 
	private ServerSocket server; 
	private int serverPort; 
	
	public Server(int port){
		try {
			serverPort = port; 
			server = new ServerSocket(serverPort);
		}
		catch (IOException e) {
			System.out.println("Socket start failed. "); 
			System.out.println("IOException: " + e.toString()); 
		}
	}
	
	public void run(){
		Socket socket; 
		BufferedInputStream in;
		BufferedOutputStream out; 
		
		DataFile datas = new DataFile("userdata"); 
		System.out.println("Server start!!"); 
		
		while(!outServer){
			socket = null; 
			try{
				synchronized(server){
					socket = server.accept(); 
				}
				System.out.println("\nConnect: InetAddress = " + socket.getRemoteSocketAddress()); 
				socket.setSoTimeout(15000); 
				
				// get the incomming packet
				in = new BufferedInputStream(socket.getInputStream()); 
				byte[] b = new byte[1024];  
				int length = in.read(b); 
				String data = new String(b, 0, length);
				System.out.println("The data I got: " + data); 
				
				// process the incomming packet
				String tmp[];
				String addr = socket.getRemoteSocketAddress().toString();  
				tmp = data.split(",");
				String cmd = tmp[0]; 
				System.out.println("cmd :"+cmd);
				String account = tmp[1]; 
				String connection; 
				out = new BufferedOutputStream(socket.getOutputStream()); 
				if(cmd.equals("REGISTER")){
					datas.addData(account, addr.split("/")[1]); 
					
					// responese ACK
					
					out.write(("REGISTER,ACK," + account + " " + addr.split("/")[1]).getBytes()); 
					out.flush(); 
				}
				else if(cmd.equals("INVITE")){
					connection = datas.findUser(account);
					
					// response connection info
					if(connection != null){
						out.write(("INVITE,ACK," + account + " " + connection).getBytes());  
					}
					else{
						out.write(("INVITE,ACK,ERROR").getBytes()); 
					}
					out.flush(); 
				}
				
				 if(cmd.equals("SENDRECV TRUE")||cmd.equals("SENDRECV FALSE")){
					
					System.out.println("server say hello");
						
					//check member is in database?
					//connection from datas
					connection = datas.findUser(account);
						//set sender or receiver to server
						if(cmd.equals("SENDRECV TRUE")){
							
							out.write(("SENDRECV,ACK," + account + " " + connection).getBytes());
							System.out.println("sender");
						}
						if(cmd.equals("SENDRECV FALSE")){
							System.out.println("receiver");
							out.write(("SENDRECV,ACK," + account + " " + connection).getBytes());
						}
						out.flush();
					
					
				}
				

				in.close(); 
				in = null; 
				
				out.close(); 
				out = null; 
				
				socket.close(); 
			}
			catch(IOException e){
				System.out.println("Socket connection failed. "); 
				System.out.println("IOException: " + e.toString()); 
			}
		}
	}
	
		
}
