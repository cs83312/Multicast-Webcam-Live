package useragent;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

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
	  	
		// 發出註冊訊息	
		
		out = new BufferedOutputStream(client.getOutputStream());
		out.write(("REGISTER," + userName).getBytes()); 
		out.flush(); 
		
		//***** 暫時先直接連去註冊伺服器 ********
		in = new BufferedInputStream(client.getInputStream());
		byte[] b = new byte[1024]; 
		int length = in.read(b);
		String data = new String(b, 0, length); 
		System.out.println("The data I got: " + data); 
		// **************/
		
		
		// TODO: 收到 Proxy Server 的權限索取 response 後同意，再次發出註冊訊息
		// TODO: 因應不同回應的處理
					// 目前先假定不會有錯誤發生=口=
			
		
		out.close(); 
		out = null; 
		in.close(); 
		in = null; 
		
		
	}
	
	public String invite(String user) throws IOException{
		out = new BufferedOutputStream(client.getOutputStream());
		out.write(("INVITE," + user).getBytes()); 
		out.flush(); 
		
		//***** 暫時先直接連去註冊伺服器 ********
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
		//***** 向伺服器申請 要當sender or receiver  ********/
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
