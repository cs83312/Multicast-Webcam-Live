package useragent;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import signalPacket.SDPPacket;

/*		*
 * 	File:						useragent/SIPCommunicator.java
 * 
 * 	Use:						A communicator to communicate with SIP proxy servers. 
 * 
 * 	Update Date: 	2016. 6. 2
 * */

public class SIPCommunicator {
	
	private String address; 
	private int port; 
	private String userName; 
	private String sendReceive;
	
	private Socket client; 
	private InetSocketAddress isa; 
	private BufferedOutputStream out; 
	private BufferedInputStream in; 
	
	public SIPCommunicator(String addr, int prt){
		address = addr; 
		port = prt; 
		
		client = new Socket(); 
		isa = new InetSocketAddress(address, port);
		try {
			System.out.println("gg hello");
			client.connect(isa, 15000);
			
		}
		catch (IOException e){
			System.out.println("Socket Connection Failed. "); 
			System.out.println("IOException: " + e.toString()); 
		}
	}
	//chan-fa 
	public SIPCommunicator(String addr,int prt,String sendRecv)
	{
		address = addr; 
		port = prt; 
		sendReceive = sendRecv;
		
		client = new Socket(); 
		isa = new InetSocketAddress(address, port);
		try {
			client.connect(isa, 15000);
		}
		catch (IOException e){
			System.out.println("Socket Connection Failed. "); 
			System.out.println("IOException: " + e.toString()); 
		}
	}

	public void setUserName(String username){
		userName = username; 
	}
	
	public void register() throws IOException{
	  	
		
		SDPPacket register = new SDPPacket();
		
		out = new BufferedOutputStream(client.getOutputStream());
		out.write((register.register("134.208.3.13", userName)).getBytes()); 
		out.flush(); 
		
		//***** ��������閮餃�撩�� ********
		in = new BufferedInputStream(client.getInputStream());
		if(in != null){
		byte[] b = new byte[1024]; 
		int length = in.read(b);
		String data ="" ;
		data = new String(b, 0, length); 
		System.out.println("The data I got: " + data); 
		}
		
		out.close(); 
		out = null; 
		in.close(); 
		in = null; 
		
		
	}
	
	public String invite(String user) throws IOException{
		out = new BufferedOutputStream(client.getOutputStream());
		out.write(("INVITE," + user).getBytes()); 
		out.flush(); 
		System.out.println(this.address);
		//***** ��������閮餃�撩�� ********
		in = new BufferedInputStream(client.getInputStream());
		byte[] b = new byte[1024]; 
		int length = in.read(b);
		String data = new String(b, 0, length); 
		System.out.println("The data I got: " + data); 
		// **************/
		
		String tmp[] = data.split(","); 
		
		out.close(); 
		out = null; 
		in.close(); 
		in = null; 
		
		return tmp[2]; 
		
	}
	
	//chan-fa
	public void senderReceiver()throws IOException
	{
		out = new BufferedOutputStream(client.getOutputStream());
		out.write((sendReceive+"," + userName).getBytes()); 
		out.flush(); 
		System.out.println(sendReceive+" from senderReceiver-SIPComm");
		//***** ��撩���隢� 閬sender or receiver  ********/
		in = new BufferedInputStream(client.getInputStream());
		byte[] b = new byte[2048]; 
		int length = in.read(b);
		System.out.println(" length "+length); 
		String data = new String(b, 0, length); 
		System.out.println("The data I gots: " + data); 
		// **************/
		
		//String tmp[] = data.split(","); 
		
		out.close(); 
		out = null; 
		in.close(); 
		in = null; 
		
		//return tmp[2]; 
	}
	
	public void bye(){


	}
}
