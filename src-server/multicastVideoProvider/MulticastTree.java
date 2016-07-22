package multicastVideoProvider;

import java.lang.*;
import java.math.BigInteger;

public class MulticastTree {

	public Node head;
	
	public MulticastTree(){}
	
	//insert node
	 public void insertNode(String ip,int port){
		 
		 //is head?
		 if(head ==null){
			 head = new Node(ip,port);
			 return;
		 }
		 //insert node
		 Node current = head;
		 
		 while(true){
			 
			 if(current.ip.compareTo(ip)==0)
			 {
				 System.out.println(current.ip +"and "+ip);
				// return;
			 }
			 if(compare(current,ip,port)==true)//左子樹
			 {
				 if(current.left == null){
					current.left = new Node(ip,port);
					 return;
				 }
				 else
					current = current.left;
			 }
			 else if(compare(current,ip,port)==false)//右子樹
			 {
				 if(current.right == null){
					 current.right = new Node(ip,port);
					 return;
				 }
				 else
					 current = current.right;
			 }
		 }	
	 }
	//remove node
	
	//search node
	 public void preOrder(Node bsTree)
	 {
		 if(bsTree!=null)
		 {
			 System.out.println("ip: "+bsTree.ip+"port: "+bsTree.port);
			 preOrder(bsTree.left);
			 System.out.println("preend ");
			 preOrder(bsTree.right);
			 System.out.println("end end ");
		 }
	 }
	 
	 public boolean compare(Node parent,String ip,int port){
		 
		 String pValue = ipStringToInt(parent.ip);	 
		 String cValue = ipStringToInt(ip);
		 if(pValue.compareTo(cValue)==1)
			 return true;
		 else if(pValue.compareTo(cValue)==-1)
			 return false;
		 else
		 {
			if(parent.port > port)
				return true;
			else 
				return false;
		 }}
	 
	 public String  ipStringToInt(String ip)
	 {
		 String[] cSplit = ip.split("\\.");
		 String s = "";
		 for(int i=0;i<cSplit.length;i++)
			s+=cSplit[i];
		// BigInteger  big = new BigInteger(s);
		 return  s;
		 
	 }
	 public Node getHead(){
		 return head;
	 }
	 
}
class Node{
	
	String ip;
	int port;
	Node right,left;
	
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Node getRight() {
		return right;
	}

	public void setRight(Node right) {
		this.right = right;
	}

	public String getIp() {
		return ip;
	}

	public Node getLeft() {
		return left;
	}
	public Node(String ip,int port){
		this.ip = ip;
		this.port = port;
		this.left = this.right = null;
	}

	
	
}
