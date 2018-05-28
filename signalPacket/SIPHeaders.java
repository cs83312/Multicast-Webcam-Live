package signalPacket;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.sdp.SessionDescription;
import javax.sip.InvalidArgumentException;
import javax.sip.ListeningPoint;
import javax.sip.ObjectInUseException;
import javax.sip.PeerUnavailableException;
import javax.sip.SipFactory;
import javax.sip.SipListener;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.TransportNotSupportedException;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.URI;
import javax.sip.header.AllowHeader;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import javax.swing.JOptionPane;

import org.apache.log4j.PropertyConfigurator;

import gov.nist.javax.sip.header.Allow;
import gov.nist.javax.sip.header.CSeq;
import gov.nist.javax.sip.header.ContentType;
import gov.nist.javax.sip.header.From;
import gov.nist.javax.sip.header.MaxForwards;
import gov.nist.javax.sip.header.To;
import gov.nist.javax.sip.header.Via;
import gov.nist.javax.sip.message.MessageFactoryImpl;

public class SIPHeaders {
	
	SipFactory sipFactory;          // Used to access the SIP API.
	SipStack sipStack;              // The SIP stack.
	SipProvider sipProvider;        // Used to send SIP messages.
	MessageFactory messageFactory;  // Used to create SIP message factory.
	HeaderFactory headerFactory;    // Used to create SIP headers.
	AddressFactory addressFactory;  // Used to create SIP URIs.
	ListeningPoint listeningPoint;  // SIP listening IP address/port.
	Properties properties;          // Other properties.

	// Objects keeping local configuration.
	          // The local IP address.
	         // The local port.
	        // The local protocol (UDP).
	int tag = (new Random()).nextInt(); // The local tag.
	// The contact address.
	ContactHeader contactHeader;    // The contact header.
	AllowHeader allowHeader;
	public SIPHeaders(SipProvider sipProvider){
		try {
		    // Get the local IP address.
		    // Create the SIP factory and set the path name.
		    this.sipFactory =SipFactory.getInstance();
		    this.sipFactory.setPathName("gov.nist");
		    // Create the SIP message factory.
		    this.sipProvider = sipProvider;
		    this.messageFactory = this.sipFactory.createMessageFactory();
		    // Create the SIP header factory.
		    this.headerFactory = this.sipFactory.createHeaderFactory();
		    // Create the SIP address factory.
		    this.addressFactory = this.sipFactory.createAddressFactory();
		    // Create the contact address used for all SIP messages.
		    // Create the contact header used for all SIP messages.
		    

		    // Display the local IP address and port in the text area.
		  
		}
		catch(Exception e) {
		    // If an error occurs, display an error message box and exit.
		    System.out.println(e.getMessage());
		    System.exit(-1);
		}
	}
	
	public Request createReqPacket(String fromAddr,int fromPort,String ToSIPUri,String toIp,int toIpport,String protocol,String method){
		
		Request request = null;
		try {
		    // Get the destination address from the text field.
		    Address addressTo = this.addressFactory.createAddress(ToSIPUri);
		    // Create the request URI for the SIP message.
		    URI requestURI = addressTo.getURI();

		    // Create the SIP message headers.
		    // The "Via" headers.
		    ArrayList viaHeaders = new ArrayList();
		    Address contactAddress = this.addressFactory.createAddress("sip:" + fromAddr + ":" + fromPort);
		    
		    ViaHeader viaHeader = this.headerFactory.createViaHeader(fromAddr,fromPort, protocol, null);
		    viaHeaders.add(viaHeader);
		    MaxForwardsHeader maxForwardsHeader = this.headerFactory.createMaxForwardsHeader(70);
		    CallIdHeader callIdHeader = this.sipProvider.getNewCallId();
		    CSeqHeader cSeqHeader = this.headerFactory.createCSeqHeader(1L,method);
		    FromHeader fromHeader = this.headerFactory.createFromHeader(contactAddress, String.valueOf(this.tag));
		    ToHeader toHeader = this.headerFactory.createToHeader(addressTo, null);
		     request = this.messageFactory.createRequest(
		        requestURI,
		        method,
		        callIdHeader,
		        cSeqHeader,
		        fromHeader,
		        toHeader,
		        viaHeaders,
		        maxForwardsHeader);
		    // Add the "Contact" header to the request.
		     this.contactHeader = this.headerFactory.createContactHeader(contactAddress);
		    request.addHeader(contactHeader);
		    this.allowHeader = this.headerFactory.createAllowHeader("INVITE, ACK, CANCEL, OPTIONS, BYE, REFER, NOTIFY");
		    request.addHeader(allowHeader);
		    // Send the request statelessly through the SIP provider.

		    // Display the message in the text area.
		}
		catch(Exception e) {
		    // If an error occurred, display the error.
		    e.printStackTrace();
		}
		return request;
	}
    public Request createReqPacket(String fromAddr,int fromPort,String ToSIPUri,String toIp,int toIpport,String protocol,String method,SessionDescription sdp){
		
		Request request = null;
		try {
		    // Get the destination address from the text field.
		    Address addressTo = this.addressFactory.createAddress(ToSIPUri);
		    // Create the request URI for the SIP message.
		    URI requestURI = addressTo.getURI();

		    // Create the SIP message headers.
		    // The "Via" headers.
		    ArrayList viaHeaders = new ArrayList();
		    Address contactAddress = this.addressFactory.createAddress("sip:" + fromAddr + ":" + fromPort);
		    Address fromaddr = this.addressFactory.createAddress("sip:" + fromAddr );
		    ViaHeader viaHeader = this.headerFactory.createViaHeader(fromAddr,fromPort, protocol, null);
		    viaHeader.setBranch("z9hG4bK776asdhds");
		    viaHeaders.add(viaHeader);
		    MaxForwardsHeader maxForwardsHeader = this.headerFactory.createMaxForwardsHeader(70);
		    CallIdHeader callIdHeader = this.sipProvider.getNewCallId();
		    CSeqHeader cSeqHeader = this.headerFactory.createCSeqHeader(1L,method);
		    FromHeader fromHeader = this.headerFactory.createFromHeader(contactAddress, String.valueOf(this.tag));
		    ToHeader toHeader = this.headerFactory.createToHeader(addressTo, null);
		    ContentType contentType = new ContentType();
		    contentType.setContentType("application","sdp");
		    request = this.messageFactory.createRequest(
		        requestURI,
		        method,
		        callIdHeader,
		        cSeqHeader,
		        fromHeader,
		        toHeader,
		        viaHeaders,
		        maxForwardsHeader,contentType,sdp);
		    // Add the "Contact" header to the request.
		     this.contactHeader = this.headerFactory.createContactHeader(contactAddress);
		    request.addHeader(contactHeader);
		    this.allowHeader = this.headerFactory.createAllowHeader("INVITE, ACK, CANCEL, OPTIONS, BYE, REFER, NOTIFY");
		    request.addHeader(allowHeader);
		    // Send the request statelessly through the SIP provider.

		    // Display the message in the text area.
		}
		catch(Exception e) {
		    // If an error occurred, display the error.
		    e.printStackTrace();
		}
		return request;
	}
	
public Request createAck(String fromAddr,int fromPort,String ToSIPUri,String toIp,int toIpport,String protocol, CallIdHeader callIdHeader,String method){
		
		Request request = null;
		try {
		    // Get the destination address from the text field.
		    Address addressTo = this.addressFactory.createAddress(ToSIPUri);
		    // Create the request URI for the SIP message.
		    URI requestURI = addressTo.getURI();

		    // Create the SIP message headers.
		    // The "Via" headers.
		    ArrayList viaHeaders = new ArrayList();
		    Address contactAddress = this.addressFactory.createAddress("sip:" + fromAddr + ":" + fromPort);
		    Address fromaddr = this.addressFactory.createAddress("sip:" + fromAddr );
		    ViaHeader viaHeader = this.headerFactory.createViaHeader(fromAddr,fromPort, protocol, null);
		    viaHeader.setBranch("z9hG4bK776asdhds");
		    viaHeaders.add(viaHeader);
		    MaxForwardsHeader maxForwardsHeader = this.headerFactory.createMaxForwardsHeader(70);
		    CSeqHeader cSeqHeader = this.headerFactory.createCSeqHeader(1L,method);
		    FromHeader fromHeader = this.headerFactory.createFromHeader(contactAddress, String.valueOf(this.tag));
		    ToHeader toHeader = this.headerFactory.createToHeader(addressTo, null);
		    request = this.messageFactory.createRequest(
		        requestURI,
		        method,
		        callIdHeader,
		        cSeqHeader,
		        fromHeader,
		        toHeader,
		        viaHeaders,
		        maxForwardsHeader);
		    // Add the "Contact" header to the request.
		     this.contactHeader = this.headerFactory.createContactHeader(contactAddress);
		    request.addHeader(contactHeader);
		    // Send the request statelessly through the SIP provider.

		    // Display the message in the text area.
		}
		catch(Exception e) {
		    // If an error occurred, display the error.
		    e.printStackTrace();
		}
		return request;
	}
    
    public Response createResPacket(
    		int status,
    		CallIdHeader callidheader,
    		CSeq cseq,
    		From from, 
			To to,
			List viaList,
			MaxForwards maxForwards) throws ParseException{
    	
    	
      MessageFactoryImpl  message = new MessageFactoryImpl();
		Response response;
		response =message.createResponse(status,
				callidheader,
				cseq,
				from, 
				to,
				viaList,
				maxForwards);
		
		return  response;
    }
    

    
    public static void main(String[] args) throws PeerUnavailableException, 
	TransportNotSupportedException, InvalidArgumentException, 
	ObjectInUseException{
		
		String log4jConfPath = "C:\\Users\\chanfa\\Desktop\\src\\UserAgent\\log4j.properties";
		PropertyConfigurator.configure(log4jConfPath);
		Properties pro =new Properties();
		SipFactory factory = SipFactory.getInstance();
		pro.setProperty("javax.sip.IP_ADDRESS","134.208.3.13");
		pro.setProperty("javax.sip.STACK_NAME","testStack");
		
		SipStack stack = factory.createSipStack(pro);
		ListeningPoint listener = stack.createListeningPoint("134.208.3.13", 20601, "tcp");
		SipProvider provide = stack.createSipProvider(listener);
		//SIPHeader header = new SIPHeader(stack,provide,2001);
		System.out.println("stop here?");
	//	header.createrPacket("sip:chanfa@134.208.3.13:20601");
	}
	
	
	
}
