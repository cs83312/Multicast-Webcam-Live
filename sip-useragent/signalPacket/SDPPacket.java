package signalPacket;

public class SDPPacket {
	
	
	
	
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
	public String invite(String sourceIP,String destIP,String userName){
		
		String requestBody = 
        		"INVITE sip:cs83312@"+destIP+" SIP/2.0\r\n"
    	                + "CSeq: 1 INVITE\r\n"
    	                + "From: <sip:"+userName+"@sip.linphone.org>;tag=a9yA2Ah30\r\n"
    	                + "To: 'cs83312' <sip:cs83312@"+destIP+">\r\n"
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
		                + "o="+userName+"893 4020 IN IP4 "+sourceIP+"\r\n"
		                + "s=LiveVideo\r\n"
		                + "t=0 0\r\n"
		                + "c=IN IP4 "+sourceIP+"\r\n"
		                +" m=audio 7078 RTP/AVP 111 110 0 8 101"
		                +" a=rtpmap:111 speex/16000"
		                +" a=fmtp:111 vbr=on"
		                +" a=rtpmap:110 speex/8000"
		                +" a=fmtp:110 vbr=on"
		                +" a=rtpmap:101 telephone-event/8000"
		                +" a=fmtp:101 0-15";
		
		return requestBody;
		
	}
	

}
