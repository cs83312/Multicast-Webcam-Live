package useragent;

import java.io.Serializable;
import java.util.ArrayList;

public class Node implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1591438125853567039L;
	int redirection;// if redirection ==1 then is redirected from root
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
		redirection=0;
	}
	
	public void setRootProvider(String addr,int port){this.rootProvider = new Node(addr,port,0);}
	public void setParent(Node parent){this.parent = parent;}
	public Node getParent(){return this.parent;}
	public String getAddress() {return address;}
	public int getTcpPort() {return tcpPort;}
	public int getUdpPort() {return udpPort;}
	public void setDir(int dir){this.redirection = dir;}
	public int getDir(){return this.redirection;}
	
}