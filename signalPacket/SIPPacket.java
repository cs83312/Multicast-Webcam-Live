package signalPacket;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.sdp.SdpException;
import javax.sdp.SdpFactory;
import javax.sdp.SessionDescription;
import javax.sip.InvalidArgumentException;
import javax.sip.address.*;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.ToHeader;
import javax.sip.message.Request;
import javax.sdp.*;
import gov.nist.javax.sdp.SessionDescriptionImpl;
import gov.nist.javax.sdp.fields.OriginField;
import gov.nist.javax.sip.address.AddressFactoryImpl;
import gov.nist.javax.sip.address.AddressImpl;
import gov.nist.javax.sip.address.GenericURI;
import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.header.*;
import gov.nist.javax.sip.message.MessageFactoryImpl;
import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPResponse;
import gov.nist.javax.sdp.fields.*;

public class SIPPacket implements Serializable{
	
	public Request request = null;
	public SIPResponse response = null;
	
	public SIPPacket(){//default invite data  
		SessionDescription sdpPacket;
		try {
			response = new SIPResponse();
			sdpPacket = SDPCreate();	
			} 
		catch (SdpException e) {e.printStackTrace();}

	}
	public void showTest(){
		System.out.println("hello i got sip packet from sipcomm listener");
	}
	public SIPPacket(InetSocketAddress toRootTalk,InetSocketAddress fromClientTalk,InetSocketAddress fromClientDataTransmission){//default invite data  
		
		SessionDescription sdpPacket;
		try {
			
			//build header
			sdpPacket = SDPCreate();
			SIPRequestCreate(toRootTalk,fromClientTalk,fromClientDataTransmission,sdpPacket);
			
			
			} 
		catch (ParseException e) {e.printStackTrace();} 
		catch (InvalidArgumentException e) {e.printStackTrace();}
		catch (SdpException e) {e.printStackTrace();}
		
		//System.out.println("Customize sip header field......\n"+request);
		//System.out.println("Customize sdp field\n"+sdpPacket);
		
	}
	
	public String register(String regServerAddr,String userName){
		
		 String requestBody = 
				 "REGISTER sip:"+userName+"@"+regServerAddr+" ;transport=udp SIP/2.0\r\n"
		   	                + "CSeq: 100 REGISTER\r\n"
		   	                + "From: <sip:testcs83312@sip.linphone.org>;tag=a9yA2Ah30\r\n"
		   	                + "To: 'cs83312' <sip:cs83312@134.208.3.13>\r\n"
		   	                + "Call-ID: 24cYQaY1xG"
		   	                + "Via: SIP/2.0/UDP 134.208.3.20:5061;branch=z9hG4bK.Ie2W-VJv~;rport\r\n"
		   	                + "Max-Forwards: 70\r\n"
		   	                + "Expires: 3600"
		   	                + "Contact: <sip:02.002.00001.p25dr>\r\n"
		   	                + "Content-Type: application/sdp;level=1\r\n"
		   	                + "Content-Length: 0\r\n\r\n"; 
		 return requestBody;
	}

	public SessionDescription SDPCreate() throws SdpException{
		Random ran = new Random();
		SDPContent sdp = new SDPContent();
		sdp.setV(1);
		sdp.setO("chanfa", ran.nextInt(8888888),0,"in","IP4","134.208.3.13");
		sdp.setS("music live");
		sdp.setI("this live is base on p2p stream");
        sdp.setU("134.208.3.13");
		sdp.setC("IN","IP4","134.208.3.13");
        sdp.setE("chanfa@gmail.com");	
        sdp.setP("0938089377");
		return sdp.getSessionDescription();
		}
	
	
	public Request SIPRequestCreate(InetSocketAddress toRootTalk,InetSocketAddress fromClientTalk,
			InetSocketAddress fromClientDataTransmission,SessionDescription SDPPacket )
		throws ParseException, InvalidArgumentException{
		MessageFactoryImpl  message = new MessageFactoryImpl();
		SIPHeaderPacket sip = new SIPHeaderPacket();
		
		//set  sip header
		sip.setUri("134.208.3.13");
		sip.setCallidheader("f81d4fae-7dec-11d0-a765-00a0c91e6bf6@test.foo.bar.com");
		sip.setCseq("CSeq", 312, "invite");
		sip.setFrom("chanfa_call","sip:thisisfrom@"+fromClientTalk.getAddress().toString().replace("/","")+":"+Integer.toString(fromClientTalk.getPort()),"thisistest");
        sip.setTo("sip:thisisto@"+toRootTalk.getAddress().toString().replaceAll("/","")+":"+Integer.toString(toRootTalk.getPort()));	
	    sip.setVia("SIP/2.0/UDP","1contentType34.208.3.13","z9hG4bKkjshdyff",8080);
        sip.setMaxForwards(70);
        sip.setContentType("application/sdp");
		//end set sip header
        
		//set sdp content
		//end set sdp content
		try {
			Vector<String> mediaInf = new Vector<String>();
			String temp = "video "+String.valueOf(fromClientDataTransmission.getPort())+" RTP/AVP ";
			mediaInf.add(temp);
			SDPPacket.setMediaDescriptions(mediaInf);	
		} catch (SdpException e) {e.printStackTrace();}
		
		
		//the last two parameter is join sdp data in sip message,so this request is use for "invite" 
		 request= message.createRequest(sip.getUri(),"invite", ( CallIdHeader)sip.getCallidheader(),(CSeqHeader)sip.getCseq(),
				(FromHeader)sip.getFrom(),(ToHeader)sip.getTo(),sip.getViaList(), (MaxForwardsHeader)sip.getMaxForwards(),sip.getContentType(),SDPPacket);
		
		return request;
	}
	/*
	 * it's override is use to refer
	 * 
	 */
	public Request SIPRequestCreate(SIPHeaderPacket sipHeader,SessionDescription SDPPacket )
		throws ParseException, InvalidArgumentException{
		MessageFactoryImpl  message = new MessageFactoryImpl();

		
		
		//the last two parameter is join sdp data in sip message,so this request is use for "invite" 
		 request= message.createRequest(sipHeader.getUri(),"invite", ( CallIdHeader)sipHeader.getCallidheader(),(CSeqHeader)sipHeader.getCseq(),
				(FromHeader)sipHeader.getFrom(),(ToHeader)sipHeader.getTo(),sipHeader.getViaList(), (MaxForwardsHeader)sipHeader.getMaxForwards(),sipHeader.getContentType(),null);
		
		return request;
	}
	
	
	
	public static void main(String args[]){
		
		SIPPacket sip = new SIPPacket();
		System.out.println(sip.request);
		
		
	}


}
