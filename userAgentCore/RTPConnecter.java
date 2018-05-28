package userAgentCore;

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

import multicastTree.MulticastNode;
import multicastTree.MulticastTree;
import signalPacket.RTPpacket;


/*		*
 * 	File:						useragent/RTPConnecter.java
 * 
 * 	Use:						this rtp class is use for transform data which can't use for communication
 *                             rtp 只須按照自己的樹節點去傳輸資料，樹節點的修改再sipcommunicator完成，所以無需listener來檢測有誰拜訪
 * 
 * 	Update Date: 	2015. 12. 24
 * */

public class RTPConnecter extends Thread implements ActionListener {
	
	//server
	DatagramPacket recv,send;
	InetAddress ClientIPAddr; //Client IP address
	int ClientPort;
	int RTPDestPort;
	int imgCount;
	public MulticastNode multiTree;
	//client
	DatagramSocket clientSendRTPSocket;
	DatagramPacket recvClient,sendClient;
	InetAddress ServerIPAddr;
	int RTPServerPort;
	int clientTCPListenPort;
    int RTP_RCV_PORT = 25000;
    public MulticastNode clientNode;
	//common
	DatagramSocket RTPSocket;
	private Socket RTSPSocket;
	static int MJPEG_TYPE = 26;
	static BufferedReader RTSPBufferedReader;
	static BufferedWriter RTSPBufferedWriter;
	Timer timer;
	
	boolean serClientPort;
	

	static int FRAME_PERIOD = 100;
	
	public RTPConnecter(){//server create one to one
	
		timer = new Timer(FRAME_PERIOD, this);
	    timer.setInitialDelay(0);
	    timer.setCoalesce(true);
	    imgCount=0;
	    //Initiate TCP connection with the client for the RTSP session
		try {
			serClientPort = true;
			RTPSocket = new DatagramSocket();//  create UDP Socket to send image data
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int getClientPort() {return ClientPort;}

	public RTPConnecter(String serverIP,int receivePort){//client create
		try {

			//Set input and output stream filters:
			this.ClientPort = receivePort;
			serClientPort = false;
			RTPSocket = new DatagramSocket(receivePort);// this is use to receive rtp data.
			clientSendRTPSocket = new DatagramSocket();//this is use to send rtp data
		    clientNode = new MulticastNode(null,String.valueOf(RTPSocket.getInetAddress()),-1,receivePort);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	boolean test;
	
	public void setTree(MulticastNode tree){
		this.multiTree = tree;
		
	}
	
	
	
	public void RTPSend(byte[] imageByte) throws IOException{
		 
				RTPpacket rtp_packet;
				MulticastNode child = null;
						imgCount++;	
						//put image
	    			rtp_packet = new RTPpacket(MJPEG_TYPE, imgCount, imgCount*FRAME_PERIOD, imageByte,imageByte.length,1);
	    			byte[] packet_bits = new byte[rtp_packet.getlength()];
					rtp_packet.getpacket(packet_bits);
					
					/*
					 * 透過群播樹傳資料
					 */
					
					if(serClientPort){
							//this is Server use multitree to send data for node
								for(int i=0;i<multiTree.childNode.size();i++)
								{
									child = multiTree.childNode.get(i);
									send = new DatagramPacket(packet_bits,packet_bits.length,InetAddress.getByName(child.getAddress()),child.getUdpPort());
									//System.out.println("send packet"+imageByte.length);
									RTPSocket.send(send);
								
								}	
					}
					else if(!serClientPort)
						{
						//this is Client use multitree to send data for node
							for(int i=0;i<clientNode.childNode.size();i++)
							{
								child = clientNode.childNode.get(i);
								send = new DatagramPacket(packet_bits,packet_bits.length,InetAddress.getByName(child.getAddress()),child.getUdpPort());
								//System.out.println("send packet"+imageByte.length);
								clientSendRTPSocket.send(send);
							}
						}
		
				}

	public Image RTPreceive() throws IOException{
		
			byte[] buf= new byte[64000];
			byte[] payload;
			RTPpacket rtp_packet;
			
		
			recvClient = new DatagramPacket(buf,buf.length);
			RTPSocket.receive(recvClient);
			rtp_packet = new RTPpacket(recvClient.getData(), recvClient.getLength());
			//print important header fields of the RTP packet received: 
			
			payload = new byte[rtp_packet.getpayload_length()];
			rtp_packet.getpayload(payload);
			RTPSend(payload);
			//System.out.println("Got RTP packet with SeqNum # "+rtp_packet.getsequencenumber()+" TimeStamp "+rtp_packet.gettimestamp()+" ms, of type "+
		//rtp_packet.getpayloadtype()+",flag"+rtp_packet.getFlag()+" rec packet"+payload.length);
				return ImageIO.read(new ByteArrayInputStream(payload));

	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public MulticastNode getClientNode(){return clientNode;}
	//to listen the node from root or upper node

	
	
}
