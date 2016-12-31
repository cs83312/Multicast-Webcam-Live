package useragent;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;


public class MulticastTest {
	
	public Node root;
	public MulticastTest(String addr,int port){//server tree create
		
		//root is video provider(uploader) ,
		//which doesn't need udp port because it doesn't download 
		root = new Node(addr,port,-1);
	}
	
	public void addNode(Node root,Node client){
		
		if(root.childNode.size()< 2){
			//root.childNode.get(0) is left tree,root.childNode.get(1) is right tree
			Node e = client;
			e.setParent(root);
			root.childNode.add(e);
			if(root.getUdpPort()!=-1)//if node is not leaf of root ,then redirection
				redirection(e);
		}
		else{
			//!! here need modify to balance tree
			//find  Suitable location
			findGoodLocation(root);
			//always find node in left of tree
			addNode(root.childNode.get(0),client);
			
			
			//redirection to leaf 
		}
	}
	
	private Node findGoodLocation(Node root){
		
		return null;
	}
	
	
	
	public void showNode(Node root){
		if(root.childNode.size()!=0){
			if(root.childNode.get(0)!=null)
			System.out.println(root.childNode.get(0).getTcpPort());
			if(root.childNode.get(1)!=null)
				System.out.println(root.childNode.get(1).getTcpPort());
			
			showNode(root.childNode.get(0));
			if(root.childNode.get(1)!=null)
			showNode(root.childNode.get(1));
			
		}
	}
	
	public void redirection(Node node){
		Socket dirServer;
		try {
			System.out.println("redirection in multicast tree"+node.getParent().getAddress());
			InetAddress ServerIpAddr = InetAddress.getByName(node.getParent().getAddress());
			dirServer = new Socket(ServerIpAddr,node.getParent().getTcpPort());
			OutputStream os = dirServer.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(os);
			node.setDir(1);
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
	
	
	
}
