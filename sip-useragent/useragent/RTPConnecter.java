package useragent;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.Timer;

import useragent.MulticastTest.Node;



/*		*
 * 	File:						useragent/RTPConnecter.java
 * 
 * 	Use:						A communicator to communicate with provider or forwarder by RTP. 
 * 
 * 	Update Date: 	2015. 12. 24
 * */

public class RTPConnecter implements ActionListener {
	
	//server
	DatagramPacket recv,send;
	InetAddress ClientIPAddr; //Client IP address
	int ClientPort;
	static BufferedReader RTSPBufferedReader;
	static BufferedWriter RTSPBufferedWriter;
	int RTPDestPort;
	int imgCount;
	//client
	DatagramPacket recvClient,sendClient;
	InetAddress ServerIPAddr;
	int RTPServerPort;
    int RTP_RCV_PORT = 25000;
	//common
	DatagramSocket RTPSocket;
	private Socket RTSPSocket;
	static int MJPEG_TYPE = 26;
	Timer timer;
	MulticastTest multiLine;

	static int FRAME_PERIOD = 100;
	
	public RTPConnecter(){//server create one to one
		
		timer = new Timer(FRAME_PERIOD, this);
	    timer.setInitialDelay(0);
	    timer.setCoalesce(true);
	    imgCount=0;
	    //multiLine =new MulticastTest("127.0.0.1",);
	    
	    
	    //Initiate TCP connection with the client for the RTSP session
		try {
			multiLine = new MulticastTest("127.0.0.1",8555);
			RTPListener();
			RTPSocket = new DatagramSocket();// 建立傳送的 UDP Socket。
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public RTPConnecter(String serverIP,int serverPort){//client create
		try {
			//set serverip
			Random ran = new Random();
			int udpPort = ran.nextInt(1000)+5000;
			RTPServerPort =8554;
			ServerIPAddr = InetAddress.getByName("127.0.0.1");
			RTSPSocket = new Socket(ServerIPAddr,RTPServerPort);
			//Set input and output stream filters:
			RTSPBufferedReader = new BufferedReader(new InputStreamReader(RTSPSocket.getInputStream()) );
			RTSPBufferedWriter = new BufferedWriter(new OutputStreamWriter(RTSPSocket.getOutputStream()) );
		    
			RTPSocket = new DatagramSocket();// 設定接收的 UDP Socket.
			RTSPRequest("Invite");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	boolean test;
	public void RTPListener(){
	    Thread t = new Thread(){
	    	@Override
	    	public void run(){
	    		ServerSocket listenSocket = null;
	    	    int RTSPport = 8554;// Integer.parseInt(argv[0]);
	    	    
	    		while(true){
	    			try {
	    				try {
	    					listenSocket = new ServerSocket(RTSPport);
	    				} catch (IOException e1) {
	    					// TODO Auto-generated catch block
	    					e1.printStackTrace();
	    				}
	    				
	    				RTSPSocket = listenSocket.accept();		
	    				ClientIPAddr = RTSPSocket.getInetAddress();
	    				ClientPort = RTSPSocket.getPort();
	    				listenSocket.close();
	    				System.out.println("client ip from"+ClientIPAddr+" and port:"+ClientPort);
	    				RTSPBufferedReader = new BufferedReader(new InputStreamReader(RTSPSocket.getInputStream()) );
	    				RTSPBufferedWriter = new BufferedWriter(new OutputStreamWriter(RTSPSocket.getOutputStream()) );
	    				parseRTSPRequest();
			    			} catch (IOException e) {
			    				// TODO Auto-generated catch block
			    				e.printStackTrace();
			    			} 
	    	    }
	    	}
	    };
	    t.start();
	    
	}
	public void RTPSend(String destIP,int destAddr,byte[] imageByte){
		 
	    try {
	    	imgCount++;
	    	
	    	
	    			RTPpacket rtp_packet;
	    			
	    				rtp_packet = new RTPpacket(MJPEG_TYPE, imgCount, imgCount*FRAME_PERIOD, imageByte,imageByte.length,1);
					
	    		
	    				byte[] packet_bits = new byte[rtp_packet.getlength()];
					rtp_packet.getpacket(packet_bits);
					send = new DatagramPacket(packet_bits,packet_bits.length,ClientIPAddr,5555);
					//213321System.out.println("send packet"+imageByte.length);
					RTPSocket.send(send);
			
			//System.out.println("rtpsend finish");
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		}	
	}

	public Image RTPreceive(){
		
		try {
			
			byte[] buf;
			buf = new byte[30000]; 
			recvClient = new DatagramPacket(buf,buf.length);
			RTPSocket.receive(recvClient);
			
			RTPpacket rtp_packet = new RTPpacket(recvClient.getData(), recvClient.getLength());
			//print important header fields of the RTP packet received: 
	
			byte[] payload = new byte[rtp_packet.getpayload_length()];
			rtp_packet.getpayload(payload);
			System.out.println("Got RTP packet with SeqNum # "+rtp_packet.getsequencenumber()+" TimeStamp "+rtp_packet.gettimestamp()+" ms, of type "+
			rtp_packet.getpayloadtype()+",flag"+rtp_packet.getFlag()+" rec packet"+payload.length);
				return ImageIO.read(new ByteArrayInputStream(payload));
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public void RTSPRequest(String type) {
		//type 1:root invite ->redirection
		//type 2:client request to root
		
		if(type.equals("Invite")){
			try{
				Node clientNode = new Node(RTSPSocket.getLocalSocketAddress().toString().split("/")[1],RTSPSocket.getLocalPort(),RTPSocket.getLocalPort());
				OutputStream os = RTSPSocket.getOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(os);
				oos.writeObject(clientNode);
				oos.flush();
				oos.close();
				os.close();
			RTSPSocket.close();
			}catch(IOException e){
				System.out.println("writer is null");
			}
		}
		
	}
	
	public void parseRTSPRequest() throws IOException{

		 try{
				ObjectInputStream ois = new ObjectInputStream(RTSPSocket.getInputStream());
				Node from = (Node)ois.readObject();
				if (from!=null){System.out.println("parseRTSPRequest recv:"+from.getUdpPort()+"and "+from.getTcpPort());}
				multiLine.addNode(multiLine.root,from);
				ois.close();
	       }catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("is not serialization");
		}
		
	
	}
	
	

}
