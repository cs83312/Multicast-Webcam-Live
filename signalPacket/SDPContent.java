package signalPacket;

import java.util.Random;
import java.util.Vector;

import javax.sdp.SdpException;
import javax.sdp.SessionDescription;


import gov.nist.javax.sdp.SessionDescriptionImpl;
import gov.nist.javax.sdp.fields.ConnectionField;
import gov.nist.javax.sdp.fields.InformationField;
import gov.nist.javax.sdp.fields.MediaField;
import gov.nist.javax.sdp.fields.OriginField;
import gov.nist.javax.sdp.fields.ProtoVersionField;
import gov.nist.javax.sdp.fields.SessionNameField;
import gov.nist.javax.sdp.fields.URIField;

public class SDPContent {
	SessionDescriptionImpl sdi;
	ProtoVersionField v;
	OriginField o;
	Random ran;
	SessionNameField s;
	InformationField i;
	URIField u;
	Vector<String> m;
	ConnectionField c;
	Vector<String> e ;
	Vector<String> p ;

	public SDPContent(){
		this.sdi = new SessionDescriptionImpl();
		this.v = new ProtoVersionField() ;
		this.o = new OriginField() ;
		this.s = new SessionNameField();
		this.i = new InformationField();
		this.u = new URIField();
		this.c = new ConnectionField();
		this.e = new Vector<String>();
		this.p = new Vector<String>();	
		this.m = new Vector<String>();
		//common 
		this.ran = new Random();
	}	
	
	public void setV(int v) throws SdpException {this.v.setVersion(v);}
	public void setO(String username,int sessionID,int sessionVersion,String netType,String addrType,String address) throws SdpException {
		this.o.setUsername(username);
		this.o.setSessionId(sessionID);
		this.o.setSessionVersion(sessionVersion);
		this.o.setNettype(netType);
		this.o.setAddrtype(addrType);
		this.o.setAddress(address);
	}
	public void setS(String sessionName) {this.s.setSessionName(sessionName);}
	public void setI(String information) {this.i.setInformation(information);}
	public void setU(String url) {this.u.setURI(url);}
	public void setE(String e_mails) {this.e.add(e_mails);}
	public void setP(String phones) {this.p.add(phones);}
	public void setM(String serviceType,int transportPort,String transportType){m.add(serviceType+" "+String.valueOf(transportPort)+" "+transportType);}
	public void setC(String networkType,String addressType,String address) throws SdpException {
		this.c.setNetworkType("IN");
		this.c.setAddrType("IP4");
		this.c.setAddress("134.208.3.13");
	}
	
	public SessionDescription getSessionDescription() throws SdpException {
		sdi.setVersion(v);	
		sdi.setOrigin(o);
		sdi.setSessionName(s);
		sdi.setInfo(i);
		sdi.setURI(u);
		sdi.setConnection(c);
		sdi.setEmails(e);
		sdi.setPhones(p);
		sdi.setMediaDescriptions(m);

		return sdi;
		}
	
	
	
	

}
