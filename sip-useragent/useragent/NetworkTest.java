package useragent;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import signalPacket.SLPPacket;

public class NetworkTest extends Thread{
	
	public static int port = 13579;
	Socket toServer;
	
	public NetworkTest(){
		
		
		try {
			toServer = new Socket("134.208.3.13",port);
			
			
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			System.out.println("fail?UnknownHostException");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("fail?IOExceptionz");
			e.printStackTrace();
		}
		
		
	}
	public void send(SLPPacket packet){
		 try {
			ObjectOutputStream oos = new ObjectOutputStream(toServer.getOutputStream());
			oos.writeObject(packet);
			oos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	public static void main(String[] args){
		NetworkTest test = new NetworkTest();
		SDPConverttoSLP convert = new SDPConverttoSLP();
		SLPPacket packet = convert.SDPToSLP(convert.requestBody,60,false,0);
		test.send(packet);
		packet = convert.SDPToSLP(convert.requestBody,60,false,2);
		System.out.println("check state"+packet.getSTATE());
		test.send(packet);
		while(true){}
		
		
	}
	
	

}
