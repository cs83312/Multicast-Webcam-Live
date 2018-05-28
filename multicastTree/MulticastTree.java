package multicastTree;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.sip.address.Address;


public class MulticastTree {
	
	public MulticastNode root;
	public MulticastTree(String addr,int Tcpport){//server tree create
		
		/*
		 * root is video provider(uploader) ,which doesn't need udp port because it doesn't download 
		 */
		root = new MulticastNode(null,addr,Tcpport,-1);
	}
	
	public MulticastNode addNode(MulticastNode root,MulticastNode client){

		
		if(root.childNode.size()< 2){
			//root.childNode.get(0) is left tree,root.childNode.get(1) is right tree
			MulticastNode e = client;
			e.setParent(root.getAddress(),root.getTcpPort());
			root.childNode.add(e);
		
			if(root.getUdpPort()!=-1){//if node is not "leaf of root" ,then redirection
				return root;
			}
			
			return null;// the node is under the root
		}
		else{
			//!! here need modify to balance tree
			//find  Suitable location
			//findGoodLocation(root);
			//always find node in left of tree
			return addNode(root.childNode.get(0),client);
			//redirection to leaf 
		}
		
	}
	
	private MulticastNode findGoodLocation(MulticastNode root){
		 
		return null;
	}
	
	//use inorder search
    public MulticastNode searchEndNode(MulticastNode root){   	
    	// get first end node 
    	 if (root.childNode.size()==0) return root;
    	 
    	 if(root.childNode.size()==1)
    	 searchEndNode(root.childNode.get(1));
    	 
    	 if(root.childNode.size()==2){
    	 searchEndNode(root.childNode.get(1));
    	 searchEndNode(root.childNode.get(2));
    	 }
    	 
    	 //find no end node
    	 return null;
    }
    /*
     * 
     */
    public MulticastNode searchSpecNode(MulticastNode root,String ip,int port){
    	
    	if (root.childNode.size()==0) return root;
   	 
   	 if(root.childNode.size()==1){
   		 
   		 if(ip.matches(root.childNode.get(0).getAddress())  && port == root.childNode.get(0).getTcpPort())
   			 return root.childNode.get(0);
   			 searchEndNode(root.childNode.get(0));
   	 }
   	 
   	 
   	 if(root.childNode.size()==2){
   		if(ip.matches(root.childNode.get(0).getAddress()) && port == root.childNode.get(0).getTcpPort()) 
   			return root.childNode.get(0);
   	    if(ip.matches(root.childNode.get(1).getAddress()) && port == root.childNode.get(1).getTcpPort())
   	    	return root.childNode.get(1);
   	    
   		 searchEndNode(root.childNode.get(0));
   		 searchEndNode(root.childNode.get(1));
   	 }
  
   	 
   	 //find no end node
   	 return null;
    }
	public void showNode(MulticastNode root){
		
		if(root.childNode.size()==1){
			System.out.println(root.childNode.get(0).getTcpPort());
			showNode(root.childNode.get(0));
		}
		if(root.childNode.size()==2){
			System.out.println(root.childNode.get(0).getTcpPort());
				System.out.println(root.childNode.get(1).getTcpPort());
		
			showNode(root.childNode.get(0));
			showNode(root.childNode.get(1));
			
		}
		return;
	}

	
}
