package multicastTree;

import java.io.Serializable;
import java.util.ArrayList;

import javax.sip.address.Address;

public class MulticastNode implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1591438125853567039L;
	Address sipURI;
	int redirection;// if redirection ==1 then is redirected from root
	String address;// ¦Û¤vªºaddress
	int udpPort;
	int tcpPort;
	MulticastNode parent;
	MulticastNode rootProvider;
	public ArrayList<MulticastNode> childNode;//to save node
	
	/*
	 * three parameter is include ¡iaddress ¡B sipTcpPort ¡B rtpUdpPort¡j
	 */
	
	public MulticastNode(Address sipURI,String addr,int tcpPort,int udpPort){
		this.sipURI = sipURI;
		this.address = addr;
		this.tcpPort = tcpPort;
		this.udpPort = udpPort;
		this.parent = null;	
		this.childNode = new ArrayList<MulticastNode>();
		redirection=0;
	}
	public Address getSipURI(){return sipURI;}
	public void setRootProvider(String addr,int port){this.rootProvider = new MulticastNode(null,addr,port,0);}
	public MulticastNode getRootProvider(){return this.rootProvider;}
	public void setParent(String addr,int port){this.parent = new MulticastNode(null,addr,port,0);}
	public MulticastNode getParent(){return this.parent;}
	public String getAddress() {return address;}
	public int getTcpPort() {return tcpPort;}
	public int getUdpPort() {return udpPort;}
	public void setDir(int dir){this.redirection = dir;}
	public int getDir(){return this.redirection;}
	
}