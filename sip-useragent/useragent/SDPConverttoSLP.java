package useragent;

import java.text.ParseException;
import java.util.Hashtable;
import java.util.Vector;

import javax.sip.InvalidArgumentException;
import javax.sip.PeerUnavailableException;
import javax.sip.SipFactory;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.header.ExtensionHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;

import gov.nist.javax.sip.header.ims.PAssertedIdentityHeader;
import gov.nist.javax.sip.header.ims.PPreferredIdentityHeader;
import signalPacket.SLPPacket;

public class SDPConverttoSLP {
	
	  String registerHeader = 
   		   "REGISTER sip:cs83312@134.208.3.13 ;transport=udp SIP/2.0\r\n"
  	                + "CSeq: 1 REGISTER\r\n"
  	                + "From: <sip:testcs83312@sip.linphone.org>;tag=a9yA2Ah30\r\n"
  	                + "To: 'cs83312' <sip:cs83312@134.208.3.13>\r\n"
  	                + "Call-ID: 24cYQaY1xG"
  	                + "Via: SIP/2.0/UDP 134.208.3.20:5061;branch=z9hG4bK.Ie2W-VJv~;rport\r\n"
  	                + "Max-Forwards: 70\r\n"
  	                + "Expires: 3600"
  	                + "Contact: <sip:02.002.00001.p25dr>\r\n"
  	                + "Content-Type: application/sdp;level=1\r\n"
  	                + "Content-Length: 0\r\n\r\n"; 
       String requestHeader = 
       		"INVITE sip:00001002000022@p25dr;user=TIA-P25-SU SIP/2.0\r\n"
               + "CSeq: 1 INVITE\r\n"
               + "From: <sip:0000100200000c@p25dr;user=TIA-P25-SU>;tag=841\r\n"
               + "To: <sip:00001002000022@p25dr;user=TIA-P25-SU>\r\n"
               + "Via: SIP/2.0/UDP 02.002.00001.p25dr;branch=z9hG4bKa10f04383e3d8e8dbf3f6d06f6bb6880\r\n"
               + "Max-Forwards: 70\r\n"
               + "Route: <sip:TIA-P25-U2UOrig@01.002.00001.p25dr;lr>,<sip:TIA-P25-U2UDest@03.002.00001.p25dr;lr>\r\n"
               + "Contact: <sip:02.002.00001.p25dr>\r\n"
               + "Timestamp: 1154567665687\r\n"
               + "Allow: REGISTER,INVITE,ACK,BYE,CANCEL\r\n"
               + "Accept: application/sdp ;level=1,application/x-tia-p25-issi\r\n"
               + "Call-ID: c6a12ddad0ddc1946d9f443c884a7768@127.0.0.1\r\n"
               + "Content-Type: application/sdp;level=1\r\n"
               + "P-Asserted-Identity: <sip:x>\r\n"
               + "P-Preferred-Identity: <sip:x>\r\n"
               + "Content-Length: 145\r\n\r\n";
       
       // If you get a request from a socket, you can use the jsip api to parse it.
      public static  String requestBody = 
       		"INVITE sip:00001002000022@p25dr;user=TIA-P25-SU SIP/2.0\r\n"
   	                + "CSeq: 1 INVITE\r\n"
   	                + "From: <sip:0000100200000c@p25dr;user=TIA-P25-SU>;tag=841\r\n"
   	                + "To: <sip:00001002000022@p25dr;user=TIA-P25-SU>\r\n"
   	                + "Via: SIP/2.0/UDP 02.002.00001.p25dr;branch=z9hG4bKa10f04383e3d8e8dbf3f6d06f6bb6880\r\n"
   	                + "Max-Forwards: 70\r\n"
   	                + "Route: <sip:TIA-P25-U2UOrig@01.002.00001.p25dr;lr>,<sip:TIA-P25-U2UDest@03.002.00001.p25dr;lr>\r\n"
   	                + "Contact: <sip:02.002.00001.p25dr>\r\n"
   	                + "Timestamp: 1154567665687\r\n"
   	                + "Allow: REGISTER,INVITE,ACK,BYE,CANCEL\r\n"
   	                + "Accept: application/sdp ;level=1,application/x-tia-p25-issi\r\n"
   	                + "Call-ID: c6a12ddad0ddc1946d9f443c884a7768@127.0.0.1\r\n"
   	                + "Content-Type: application/sdp;level=1\r\n"
   	                + "P-Asserted-Identity: <sip:x>\r\n"
   	                + "P-Preferred-Identity: <sip:x>\r\n"
   	                + "Content-Length: 145\r\n\r\n"
                +"v=0\r\n"
               + "o=- 30576 0 IN IP4 127.0.0.1\r\n"
               + "s=streamVideo-------\r\n"
               + "t=0 0\r\n"
               + "c=IN IP4 127.0.0.1\r\n"
               + "m=audio 12412 RTP/AVP 100\r\n"
               + "a=rtpmap:100 X-TIA-P25-IMBE/8000\r\n";
       
      public SDPConverttoSLP(){}
      
      
	@SuppressWarnings("deprecation")
	public  void main(String[] args) {
		 SipFactory sipFactory = null;
	        HeaderFactory headerFactory = null;
	        AddressFactory addressFactory = null;
	        MessageFactory messageFactory = null;

	        sipFactory = SipFactory.getInstance();
	        sipFactory.setPathName("gov.nist");

	        try {
				headerFactory = sipFactory.createHeaderFactory();
				 addressFactory = sipFactory.createAddressFactory();
			        messageFactory = sipFactory.createMessageFactory();
			      
			} catch (PeerUnavailableException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	     
	        
	        Request sipRequest = null;
	        
			try {
			/*	Address adds = addressFactory.createAddress("134.208.3.13");
				headerFactory.createHeaders("REGISTER sip:cs83312@134.208.3.13 ;transport=udp SIP/2.0");
				headerFactory.createCSeqHeader(3600, "Register");
				headerFactory.createFromHeader(adds, "<sip:testcs83312@sip.linphone.org>;tag=a9yA2Ah30");
			    headerFactory.createToHeader(adds, "'cs83312' <sip:cs83312@134.208.3.13>");
				headerFactory.createCallIdHeader("24cYQaY1xG");
				headerFactory.createExpiresHeader(3600);
				headerFactory.createContactHeader(adds);
				headerFactory.createContentTypeHeader("application/sdp","level=1");
				headerFactory.createContentLengthHeader(0);*/
				
				System.out.println(headerFactory);
				sipRequest = messageFactory.createRequest(requestBody);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	        byte[] contentBytes = sipRequest.getRawContent();
	        String contentString = new String(contentBytes);
	        //SdpFactory sdpFactory = SdpFactory.getInstance();
	        //SessionDescription sd = sdpFactory
	        //      .createSessionDescription(contentString);
	       
	        PAssertedIdentityHeader h = (PAssertedIdentityHeader)sipRequest.getHeader(PAssertedIdentityHeader.NAME);
	       // System.out.println( h.getClass() );
	       // System.out.println( h instanceof ExtensionHeader );
	       // System.out.println( h instanceof PAssertedIdentityHeader );

	        PPreferredIdentityHeader h2 = (PPreferredIdentityHeader) sipRequest.getHeader(PPreferredIdentityHeader.NAME);
	        //System.out.println( h2.getClass() );
	    //    System.out.println( h2 instanceof ExtensionHeader );
	       // System.out.println( h2 instanceof PPreferredIdentityHeader );
	        System.out.println("Parsed SIPRequest is :\n" + sipRequest.toString());
	       SLPPacket temp;
	       temp = SDPToSLP(sipRequest.toString(),60,false,0);
	}
	
	public  SLPPacket SDPToSLP(String sdpMesg,int port,boolean UAorSA,int STATE){
		int count=0;
		SLPPacket slp ;
		Hashtable abstype = new Hashtable();
		Hashtable attri = new Hashtable();
		Vector summary = new Vector();
		while(sdpMesg.split("\n").length >count){
			String temp = sdpMesg.split("\n")[count];
			String symbol = temp.split("=")[0];
			if(symbol.equals("i")||symbol.equals("o")||symbol.equals("u")||symbol.equals("e")||symbol.equals("p")
					||symbol.equals("t")||symbol.equals("b")||symbol.equals("z")||symbol.equals("k")||
				symbol.equals("r")||symbol.equals("m")||symbol.equals("a"))
				
				attri.put(symbol, temp.split("=")[1]);
			
			else if(symbol.equals("s")||symbol.equals("c"))
				{
				abstype.put(symbol,temp.split("=")[1]);

				}
			count++;
		}
		slp = new SLPPacket(abstype.get("s").toString(),abstype.get("c").toString().split(" ")[2],attri,port,UAorSA,STATE);
		summary.add(abstype);
		summary.add(attri);
		//System.out.println("SUMMARY GET ABS"+abstype.get("s"));
		//System.out.println("SUMMARY GET ATTR"+summary.get(1));
		return slp;
		
	}
	public static String SLPConvertToSDP(SLPPacket packet){
	
		
		
		return " ";
		
	}

}
