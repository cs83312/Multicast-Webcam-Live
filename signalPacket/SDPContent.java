package signalPacket;

import java.text.ParseException;
import java.util.Random;
import java.util.Vector;

import javax.sdp.SdpException;
import javax.sdp.SdpFactory;
import javax.sdp.SessionDescription;
import javax.sip.header.AllowHeader;
import javax.sip.header.ContentTypeHeader;

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
	SdpFactory sdp;
	
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
	ContentTypeHeader type;
	

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
		this.o.setSessId(sessionID);
		this.o.setSessVersion(sessionVersion);
		this.o.setNettype(netType);
		this.o.setAddressType(addrType);
		this.o.setAddress(address);
	}
	public void setS(String sessionName) {this.s.setSessionName(sessionName);}
	public void setI(String information) {this.i.setInformation(information);}
	public void setU(String url) {this.u.setURI(url);}
	public void setE(String e_mails) {this.e.add("e="+e_mails+"\n");}
	public void setP(String phones) {this.p.add("p="+phones+"\n");}
	public void setM(String serviceType,int transportPort,String transportType){
		this.m.add("m="+serviceType+" "+String.valueOf(transportPort)+" "+transportType+"\n");}
	public void setC(String networkType,String addressType,String address) throws SdpException {
		this.c.setNetworkType(networkType);
		this.c.setAddrType(addressType);
		this.c.setAddress(address);
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
