package signalPacket;

import java.io.Serializable;
import java.util.Hashtable;

public class SLPPacket implements Serializable{
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7197229392183535262L;
	String svcType;
	String addrSpec;
	Hashtable attr ;
	int port;
	boolean UAorSA;
	int STATE;//0 REG 1 REQ 2SEARCH 3DEL
	public SLPPacket(String svcType,String addrSpec,Hashtable attr ,int port,boolean STATUS,int STATE){
		this.svcType = svcType;
		this.addrSpec = addrSpec;
		this.attr = attr;
		this.port = port;
		this.UAorSA = STATUS;
		this.STATE = STATE;
	}

	public String getSvcType() {
		return svcType;
	}

	public boolean isUAorSA() {
		return UAorSA;
	}

	public String getAddrSpec() {
		return addrSpec;
	}

	public Hashtable getAttr() {
		return attr;
	}

	public int getPort() {
		return port;
	}

	public int getSTATE() {
		return STATE;
	}
	

}
