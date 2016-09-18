package useragent;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import useragent.MulticastTest.Node;

public class MulticastTest {
	
	
	
	
	
	public Node root;
	public MulticastTest(String addr,int port){//server tree create
		root = new Node(addr,port,-1);
	}
	
	public void addNode(Node root,Node client){
		
		if(root.childNode.size()== 0){
			Node e = client;
			e.setParent(root);
			root.childNode.add(e);
			if(root.getUdpPort()!=-1)//redirection
				redirection(e);
		}
		else{
			addNode(root.childNode.get(0),client);
			//redirection to leaf 
		}
	}
	
	public void showNode(Node root){
		if(root.childNode.size()!=0){
			System.out.println(root.childNode.get(0).getTcpPort());
			showNode(root.childNode.get(0));
		}
	}
	
	public void redirection(Node node){
		Socket dirServer;
		try {
			InetAddress ServerIpAddr = InetAddress.getByName(node.getAddress());
			dirServer = new Socket(ServerIpAddr,node.getTcpPort());
			OutputStream os = dirServer.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(node);
			oos.flush();
			oos.close();
			os.close();
			dirServer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
	}
	
	
	public static class Node implements Serializable {
		String address;
		int udpPort;
		int tcpPort;
		Node parent;
		Node rootProvider;
		public ArrayList<Node> childNode;
		public Node(String addr,int tcpPort,int udpPort){
			this.address = addr;
			this.tcpPort = tcpPort;
			this.udpPort = udpPort;
			this.parent = null;	
			this.childNode = new ArrayList<Node>();
		}
		public void setRootProvider(String addr,int port){
				this.rootProvider = new Node(addr,port,0);
		}
		public void setParent(Node parent){
				this.parent = parent;
		}
		public String getAddress() {
			return address;
		}
		public int getTcpPort() {
			return tcpPort;
		}
		public int getUdpPort() {
			return udpPort;
		}
		
	}
}
