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
	int RTPDestPort;
	int imgCount;
	//client
	DatagramSocket clientSendRTPSocket;
	DatagramPacket recvClient,sendClient;
	InetAddress ServerIPAddr;
	int RTPServerPort;
	int clientTCPListenPort;
    int RTP_RCV_PORT = 25000;
    Node clientNode;
	//common
	DatagramSocket RTPSocket;
	private Socket RTSPSocket;
	static int MJPEG_TYPE = 26;
	static BufferedReader RTSPBufferedReader;
	static BufferedWriter RTSPBufferedWriter;
	Timer timer;
	MulticastTest multiLine;
	boolean serClientPort;
	

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
			serClientPort = true;
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
			RTPServerPort =8554;
			ServerIPAddr = InetAddress.getByName("127.0.0.1");
			RTSPSocket = new Socket(ServerIPAddr,RTPServerPort);
			
			clientTCPListenPort = searchCanUsePort();
			//Set input and output stream filters:
			RTSPBufferedReader = new BufferedReader(new InputStreamReader(RTSPSocket.getInputStream()) );
			RTSPBufferedWriter = new BufferedWriter(new OutputStreamWriter(RTSPSocket.getOutputStream()) );
		    serClientPort = false;
			RTPSocket = new DatagramSocket();// 設定接收的 UDP Socket.
			clientSendRTPSocket = new DatagramSocket();//set sender udp Socket.s
			RTSPRequest("Invite");
			RTPListener();
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
	    				if(serClientPort)
	    				listenSocket = new ServerSocket(RTSPport);
	    				else
	    				{
	    					listenSocket = new ServerSocket(clientTCPListenPort);
	    					System.out.println("clientListening...");
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
	    			}}};
	    
	    			t.start();
	    
	}
	public void RTPSend(byte[] imageByte) throws IOException{
		 
				RTPpacket rtp_packet;
				Node child = null;
						imgCount++;	
	    			rtp_packet = new RTPpacket(MJPEG_TYPE, imgCount, imgCount*FRAME_PERIOD, imageByte,imageByte.length,1);
	    			byte[] packet_bits = new byte[rtp_packet.getlength()];
					rtp_packet.getpacket(packet_bits);
					
					if(serClientPort){
						if(multiLine.root.childNode.size()!=0)
							child = multiLine.root.childNode.get(0);
					}
					else if(!serClientPort)
						if(this.clientNode.childNode.size()!=0)
						child = this.clientNode.childNode.get(0);
					
					if(child!=null){
							send = new DatagramPacket(packet_bits,packet_bits.length,InetAddress.getByName(child.getAddress()),child.getUdpPort());
							//System.out.println("send packet"+imageByte.length);
							if(serClientPort)
							RTPSocket.send(send);
							else
							clientSendRTPSocket.send(send);
					}
					
					}

	public Image RTPreceive() throws IOException{
		
			byte[] buf= new byte[60000];
			byte[] payload;
			RTPpacket rtp_packet;
			
			
			recvClient = new DatagramPacket(buf,buf.length);
			RTPSocket.receive(recvClient);
			
			rtp_packet = new RTPpacket(recvClient.getData(), recvClient.getLength());
			//print important header fields of the RTP packet received: 
			
			payload = new byte[rtp_packet.getpayload_length()];
			rtp_packet.getpayload(payload);
			RTPSend(payload);
			System.out.println("Got RTP packet with SeqNum # "+rtp_packet.getsequencenumber()+" TimeStamp "+rtp_packet.gettimestamp()+" ms, of type "+
			rtp_packet.getpayloadtype()+",flag"+rtp_packet.getFlag()+" rec packet"+payload.length);
				return ImageIO.read(new ByteArrayInputStream(payload));

	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public int searchCanUsePort() throws IOException {
	    for (int port =2000 ;port<65535;port++) {
	        try {
	        	ServerSocket sS = new ServerSocket(port);
	        	sS.close();
	            return port;
	        } catch (IOException ex) {
	            continue; // try next port
	        }
	    }
	    // if the program gets here, no port in the range was found
	    throw new IOException("no free port found");
	}
	
	
	
	public void RTSPRequest(String type) {		
		if(type.equals("Invite")){
			try{
				clientNode = new Node(RTSPSocket.getLocalSocketAddress().toString().split("/")[1].split(":")[0],clientTCPListenPort,RTPSocket.getLocalPort());
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
				
				if (from!=null){
					
					if(from.getDir()==1){
						System.out.println("redirection node has been receive");
						clientNode.childNode.add(from);
					}
					else{
						System.out.println("new node");
						multiLine.addNode(multiLine.root,from);
						}
				}
				
				//multiLine.showNode(multiLine.root);
				ois.close();
	       }catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("is not serialization");
		}
	}
	
	public Node getClientNode(){return clientNode;}
	
}
