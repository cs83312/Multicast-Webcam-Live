package tracking;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;


/*		*
 * 	File:						tracking/SearchingTree.java
 * 
 * 	Use:						The P2P network searching tree record.  
 * 
 * 	Update Date: 	2016.  6. 2
 * */

public class SearchingTree {

	private class Node {
		public String URI; 
		public int maxConnect;
		public Node parentNode;  // the index of Arraylist
		public int layer; 
		public ArrayList<Node> listChildren;
		
		
		public Node(String uri, int max, Node parent, int lay){
			URI = uri; 
			maxConnect = max; 
			parentNode = parent; 
			layer = lay; 
			listChildren = new ArrayList<Node>(); 
		}
		
		public void addChildren(Node node){
			this.listChildren.add(node); 
		}
	};
	
	private String streamID;
	ArrayList<Node> tree;
	
	public static String byteToHex(byte b) {
		String[] h={"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f"};
		int i = b;
		if (i < 0) {
			i += 256;
		}
		return h[i/16] + h[i%16];
	}
	
	private String md5(String str){
		String md5 = null;
	    try {
	      MessageDigest md = MessageDigest.getInstance("MD5");
	      byte[] barr=md.digest(str.getBytes());  //將 byte 陣列加密
	      StringBuffer sb=new StringBuffer();  //將 byte 陣列轉成 16 進制
	      for (int i=0; i < barr.length; i++) {
	    	  sb.append(byteToHex(barr[i]));}
	      	String hex=sb.toString();
	      	md5=hex.toUpperCase(); //一律轉成大寫
	      }
	    catch(Exception e) {
	    	e.printStackTrace();
	    }
	    return md5;
	}
	
	
	
	// Create new Tree
	public SearchingTree(){
		tree = new ArrayList<Node>();
		streamID = md5("ANTLAB" + (new Date()).toString());
	}
	
	public String getStreamID(){
		return streamID; 
	}
	
	private Node BFS(){
		Queue<Node> q = new LinkedList<Node>();
		Node tmp = null; 
		Node layerTmp = null; // Store same layer's and the most idle node 
		
		q.offer(tree.get(0)); 
		
		while(q.size()!=0){
			// 上一層全數檢查完畢，且存在可 forward 之節點
			if (layerTmp != null) {
				if(q.peek().layer!=layerTmp.layer){
					break;
				}
			}
			tmp = q.poll();
			
			if(tmp.listChildren.size() < tmp.maxConnect){
				if(layerTmp == null){
					layerTmp = tmp; 
				}
				else if(tmp.listChildren.size() < layerTmp.listChildren.size()){
					layerTmp = tmp; 
				}
			}
			
			for(int i=0; i<tmp.listChildren.size(); i++){
				q.offer(tmp.listChildren.get(i)); 
			}
			
		}	
		return layerTmp; 
	}
	
	public String add(String URI, int maxConnect){
		String parentID = ""; 
		
		if(tree.size() == 0){
			// root
			tree.add(new Node(URI, maxConnect, null, 0));
			parentID = null; 
		}
		else{
			// not root
			Node parent = BFS();
			Node newNode = new Node(URI, maxConnect, parent, (parent.layer+1)); 
			tree.add(newNode);
			(tree.get(tree.indexOf(parent))).addChildren(newNode); 
			parentID = parent.URI; 
		}
		// return parent's URI
		return parentID; 
	}
	
	private Node findNode(String URI){
		Node tmp = null; 
		for(int i=0; i<tree.size(); i++){
			if((tree.get(i)).URI == URI){
				tmp = tree.get(i); 
				break; 
			}
		}
		return tmp; 
	}
	
	public boolean remove(String URI){
		// TODO: remove the node
		Node tmp = findNode(URI); 
		if(tree.indexOf(tmp) == 0){
			// provider
			return false; 
		}
		else{
			// viewer
			// TODO: remove from tree
			
			//remove from array list
			tree.remove(tmp); 
			return true; 
		}
	}
	
}
