package userAgentCore;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.sdp.Media;
import javax.sdp.Origin;
import javax.sdp.SdpException;
import javax.sdp.SdpFactory;
import javax.sdp.SdpParseException;
import javax.sdp.SessionDescription;
import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.DialogState;
import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.InvalidArgumentException;
import javax.sip.ListeningPoint;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.SipFactory;
import javax.sip.SipListener;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionAlreadyExistsException;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.TransactionUnavailableException;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.address.URI;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentLengthHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.ProxyAuthenticateHeader;
import javax.sip.header.ReferToHeader;
import javax.sip.header.SubscriptionStateHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.header.WWWAuthenticateHeader;
import javax.sip.message.Message;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import org.apache.log4j.Logger;

import gov.nist.core.Debug;
import gov.nist.javax.sip.SipStackExt;
import gov.nist.javax.sip.SipStackImpl;
import gov.nist.javax.sip.clientauthutils.AccountManager;
import gov.nist.javax.sip.clientauthutils.AuthenticationHelper;
import gov.nist.javax.sip.clientauthutils.AuthenticationHelperImpl;
import gov.nist.javax.sip.clientauthutils.DigestServerAuthenticationHelper;
import gov.nist.javax.sip.clientauthutils.MessageDigestAlgorithm;
import gov.nist.javax.sip.clientauthutils.SecureAccountManager;
import gov.nist.javax.sip.clientauthutils.UserCredentialHash;
import gov.nist.javax.sip.clientauthutils.UserCredentials;
import gov.nist.javax.sip.header.CallID;
import gov.nist.javax.sip.header.ContentType;
import gov.nist.javax.sip.header.From;
import gov.nist.javax.sip.header.ReferTo;
import gov.nist.javax.sip.header.SIPHeader;
import gov.nist.javax.sip.header.To;
import multicastTree.MulticastNode;
import multicastTree.MulticastTree;
import signalPacket.SIPHeaders;
import signalPacket.SIPPacket;
import useragent.AccountManagerImpl;

public class SIPComUseJain implements SipListener{

	    private SipFactory sipFactory;
	    private SipStack sipStack;
	    private SipProvider sipProvider;
	    private MessageFactory messageFactory;
	    private HeaderFactory headerFactory;
	    private AddressFactory addressFactory;
	    private ListeningPoint listeningPoint;
	    private Properties properties;
	    long invco = 1;//cseq counter
	    
	    private String ip;
	    private int port = 5060;
	    private String protocol = "tcp";
	    private int tag = (new Random()).nextInt();
	    private Address contactAddress;
	    private ContactHeader contactHeader;
	    private String userID;
	    
	    
	    //source from https://github.com/RestComm/jain-sip/blob/master/src/examples/prack/Shootme.java
	    private Dialog dialog;
	    private String toTag;
	    protected ClientTransaction inviteTid;
	    private Request inviteRequest;
	    private static Logger logger = Logger.getLogger(SIPComUseJain.class);

	    //rtp @par
	    private MulticastTree tree;
	    private MulticastNode node;
	    public boolean isServer;
	    private Map<String,SessionDescription> mediaList = new HashMap<String,SessionDescription>();
	    RTPConnecter rtpTrans;
	    
	    //UI 
	    private JTextArea stateLabel;
	public SIPComUseJain(String addr,String userID,JTextArea transState){  //server
		this.userID = userID;
		isServer = true;
		this.ip = addr;
		stateLabel = transState;
		initComponents();
		
	}
	
	public SIPComUseJain(String addr,int port,String userID,JTextArea transState){ //client
		this.userID = userID;
		isServer = false;
		this.ip = addr;
		this.port = port;
		stateLabel = transState;
		initComponents();
	}
	private void initComponents(){
		try {
            this.sipFactory = SipFactory.getInstance();
            this.sipFactory.setPathName("gov.nist");
            this.properties = new Properties();
            this.properties.setProperty("javax.sip.STACK_NAME", "stack");
            this.sipStack = (SipStackImpl) this.sipFactory.createSipStack(this.properties);
            this.messageFactory = this.sipFactory.createMessageFactory();
            this.headerFactory = this.sipFactory.createHeaderFactory();
            this.addressFactory = this.sipFactory.createAddressFactory();
            this.listeningPoint = this.sipStack.createListeningPoint(this.ip, this.port, this.protocol);
            this.sipProvider = this.sipStack.createSipProvider(this.listeningPoint);
            this.sipProvider.addSipListener(this);

            this.contactAddress = this.addressFactory.createAddress("sip:" + this.ip + ":" + this.port);
            this.contactHeader = this.headerFactory.createContactHeader(contactAddress);

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_onOpen
	public void register(InetSocketAddress toRootTalk,
			InetSocketAddress fromClientTalk,
			InetSocketAddress fromClientDataTransmission) throws SdpException{
		
		SIPHeaders header = new SIPHeaders(this.sipProvider);
		Request request ;
		
		//fromClientTalk.getAddress().toString().replaceAll("/","")
		//toRootTalk.getAddress().toString().replaceAll("/","")+":"+5060
		SIPPacket sdp = new SIPPacket();
		request = header.createReqPacket(this.userID+"@"+fromClientTalk.getAddress().toString().replaceAll("/",""), 
				fromClientTalk.getPort(),
				"sip:"+this.userID+"@"+toRootTalk.getAddress().toString().replaceAll("/","")+":"+5060,
				toRootTalk.getAddress().toString().replaceAll("/",""), 
				5060, 
				"tcp",
				Request.REGISTER,
		sdp.SDPCreate("",null,fromClientDataTransmission.getPort()));
		ClientTransaction tran = null;
		
		try {
			//this.sipProvider.sendRequest(request);
			if(tran==null)
			tran = this.sipProvider.getNewClientTransaction(request);
			tran.sendRequest();
			dialog = tran.getDialog();
		} catch (SipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public void invite(String toSipUID,
				InetSocketAddress fromClientTalk,
				InetSocketAddress fromClientDataTransmission) throws SdpException{
			
			SIPHeaders header = new SIPHeaders(this.sipProvider);
			Request request ;
			stateLabel.setText(stateLabel.getText()+"fromClientTalk"+fromClientTalk.getAddress().toString().replaceAll("/",""));
			SIPPacket sdp = new SIPPacket();
			request = header.createReqPacket(
					this.userID+"@"+fromClientTalk.getAddress().toString().replaceAll("/",""), 
					fromClientTalk.getPort(),
					toSipUID,
					toSipUID.split("@")[1].split(":")[0], 
					Integer.valueOf(toSipUID.split("@")[1].split(":")[1]), 
					"tcp",
					Request.INVITE,
					
			sdp.SDPCreate("media stream",null,fromClientDataTransmission.getPort()));
			System.out.println("get content\n\n"+request.toString());
			ClientTransaction tran;
			
			try {
				//this.sipProvider.sendRequest(request);
				tran = this.sipProvider.getNewClientTransaction(request);
				tran.sendRequest();
				dialog = tran.getDialog();
			} catch (SipException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}

	@Override
	public void processRequest(RequestEvent req) {
		// TODO Auto-generated method stub
		Request getRequest = req.getRequest();
		String requestType = getRequest.getMethod().toString();
		 ServerTransaction serverTransactionId = req.getServerTransaction();
		/* System.out.println("\n\nRequest TYPE " +requestType
         + " \nreceived at " + sipStack.getStackName()
         + "\n with server transaction id " + serverTransactionId
         + "\n packet "+getRequest.toString());*/
		 
		 System.out.println("request type***********/"+requestType);
		switch(requestType){
				case Request.INVITE:
					processInvite(req, serverTransactionId);
					 stateLabel.setText(stateLabel.getText()+"Request.INVITE\n"+getRequest.toString()+"\n");
					break;
				case Request.ACK:
					processAck(req,serverTransactionId);
					 stateLabel.setText(stateLabel.getText()+"Request.ACK\n"+getRequest.toString()+"\n");
					break;
				case Request.BYE:
					stateLabel.setText(stateLabel.getText()+"Request.BYE\n"+getRequest.toString()+"\n");
					break;
				case Request.REFER:
					System.out.println("Request.REFER\n"+getRequest.toString()+"\n");
					
					try {
						processRefer(req,serverTransactionId);
					} catch (ParseException | SipException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case Request.NOTIFY:
					stateLabel.setText(stateLabel.getText()+"Request.NOTIFY\n"+getRequest.toString()+"\n");
					processNotify(req,serverTransactionId);
					break;
				default:
					stateLabel.setText(stateLabel.getText()+"Other Request\n"+getRequest.toString()+"\n");
					break;
		
		
		}
		
		
	}
	public void processInvite(RequestEvent requestEvent,
            ServerTransaction serverTransaction) {
		
		
        SipProvider sipProvider = (SipProvider) requestEvent.getSource();
        Request request = requestEvent.getRequest();
        System.out.println("processInvite"+request);
        if(request.getHeader("P-Asserted-Identity")!=null){
        	
        	
        }
        else{
	        try {
	            ServerTransaction st = requestEvent.getServerTransaction();     
	            /*
	             * put sdp to map
	             */
	           SessionDescription sdp = getSDPFromSIP(request);
	           if(sdp!=null){
	        	   CallID call = (CallID) request.getHeader("Call-ID");
	               mediaList.put(call.getCallId(),sdp);
	           }
	            if (st == null)
	               st = sipProvider.getNewServerTransaction(request);
	                
	            dialog = st.getDialog();
	            // reliable provisional response. Use the API here!
	            Response tryingResponse = messageFactory.createResponse(Response.TRYING, request);
	            st.sendResponse(tryingResponse); 
	           
	            Address contactAddress = this.addressFactory.createAddress("sip:"+userID+"@" + "134.208.3.20"+ ":" + 5060);
	            ContactHeader contact = this.headerFactory.createContactHeader(contactAddress);   
	            Response ok = messageFactory.createResponse(Response.OK,request);
	            ok.addHeader(contact);
	            st.sendResponse(ok);
	            
	
	        } catch (Exception ex) {
	            ex.printStackTrace();
	           // junit.framework.TestCase.fail("Exit JVM");
	        }
        }  
    }
	public void processRefer(RequestEvent requestEvent,ServerTransaction serverTransaction) throws ParseException, SipException{
		
		SipProvider sipProvider = (SipProvider) requestEvent.getSource();
        Request refer = requestEvent.getRequest();
        
        System.out.println("referee: got an REFER sending Accepted");
        System.out.println("referee:  " + refer.getMethod() );
        System.out.println("referee : dialog = " + requestEvent.getDialog());
        
        // Check that it has a Refer-To, if not bad request
        ReferToHeader refTo = (ReferToHeader) refer.getHeader( ReferToHeader.NAME );
        
        if (refTo==null) {
            Response bad = messageFactory.createResponse(Response.BAD_REQUEST, refer);
            bad.setReasonPhrase( "Missing Refer-To" );
            sipProvider.sendResponse( bad );
            return;
        }
     // Always create a ServerTransaction, best as early as possible in the code
        Response response = null;
        ServerTransaction st = requestEvent.getServerTransaction();
        if (st == null) {
            st = sipProvider.getNewServerTransaction(refer);
        }
		
     // Check if it is an initial SUBSCRIBE or a refresh / unsubscribe
        String toTag = Integer.toHexString( (int) (Math.random() * Integer.MAX_VALUE) );
        response = messageFactory.createResponse(202, refer);
        ToHeader toHeader = (ToHeader) response.getHeader(ToHeader.NAME);
        
        // should have matched
        if (toHeader.getTag()!=null) {
            System.err.println( "####ERROR: To-tag!=null but no dialog match! My dialog=" + dialog.getState() );
        }
        toHeader.setTag(toTag); // Application is supposed to set.

        this.dialog = st.getDialog();
        
     // REFER dialogs do not terminate on bye.
        this.dialog.terminateOnBye(false);
        if (dialog != null) {
            System.out.println("Dialog " + dialog);
            System.out.println("Dialog state " + dialog.getState());
            System.out.println( "local tag=" + dialog.getLocalTag() );
            System.out.println( "remote tag=" + dialog.getRemoteTag() );
        }
        
        // Both 2xx response to SUBSCRIBE and NOTIFY need a Contact
        Address address = addressFactory.createAddress("Referee <sip:127.0.0.1>");
      //  ((SipURI)address.getURI()).setPort( udpProvider.getListeningPoint("udp").getPort() );
        ContactHeader contactHeader = headerFactory.createContactHeader(address);
        response.addHeader(contactHeader);
        
        
	}
    public void processNotify(RequestEvent requestEvent,ServerTransaction serverTransaction){
	   SipProvider provider = (SipProvider) requestEvent.getSource();
       Request notify = requestEvent.getRequest();
       try {
           if (serverTransaction == null) {
               
        	   serverTransaction = provider.getNewServerTransaction(notify);
           }
           Dialog dialog = serverTransaction.getDialog();

           if (dialog != null) {
               logger.info("Dialog State = " + dialog.getState());
           }
           Response response = messageFactory.createResponse(200, notify);
           // SHOULD add a Contact
           ContactHeader contact = (ContactHeader) contactHeader.clone();
           ((SipURI)contact.getAddress().getURI()).setParameter( "transport", "TCP" );
           response.addHeader( contact );
           logger.info("Transaction State = " + serverTransaction.getState());
           serverTransaction.sendResponse(response);
           if (dialog != null ) {
               logger.info("Dialog State = " + dialog.getState());
           }
           SubscriptionStateHeader subscriptionState = (SubscriptionStateHeader) notify
                   .getHeader(SubscriptionStateHeader.NAME);

           // Subscription is terminated?
           String state = subscriptionState.getState();
           if (state.equalsIgnoreCase(SubscriptionStateHeader.TERMINATED)) {
               dialog.delete();
           } else {
               logger.info("Referer: state now " + state);
           }

       } catch (Exception ex) {
           ex.printStackTrace();
           logger.error("Unexpected exception",ex);
           junit.framework.TestCase.fail("Exit JVM");

       }
   }
    public void processAck(RequestEvent requestEvent,ServerTransaction serverTransaction){
		 try {
	        //    System.out.println("shootme: got an ACK! start rtp stream");
	         //   System.out.println("Dialog State = " + dialog.getState());
	          //  System.out.println("Dialog State = " + requestEvent.getRequest().toString());
	            /*
	             * match the same client them start rtp transform
	             */
	            CallID call = (CallID) requestEvent.getRequest().getHeader("Call-ID");
	            From from = (From)requestEvent.getRequest().getHeader("From");
	            System.out.println(from.getAddress());
	            SessionDescription sdp = mediaList.get(call.getCallId());
	            if(sdp==null)
	            	stateLabel.setText(stateLabel.getText()+"doesn't get sdp\n");
	            else{
	            	
	            	if(isServer){
	            		/*
	            		 * get node message
	            		 */
		            	MulticastNode notRootLeaf,newNode;
		            	 String media = sdp.getMediaDescriptions(true).toString();
		            	String addr = sdp.getOrigin().getAddress().toString();
		            	From sipAddr = (From)requestEvent.getRequest().getHeader("From");	
		            	//int tcpPort = Integer.valueOf(sipAddr.toString().split(":")[3].split(">")[0]);
		            	int udpPort = Integer.valueOf(media.split(" ")[1]);
		            	
		            	newNode =  new MulticastNode(from.getAddress(),addr,5060,udpPort);
		            	notRootLeaf = tree.addNode(tree.root,newNode);
		            	/*
		            	 * 如果不是root leaf ,通知client去辦訪target data sender
		            	 */
		            	if(notRootLeaf!=null){

		            		Request refer = requestEvent.getDialog().createRequest("REFER");
		            		ReferTo referTo = new ReferTo();
		            		referTo.setAddress(notRootLeaf.getSipURI());
		            		To to = new To();
		            		to.setAddress(from.getAddress());
		            		refer.setHeader(to);
		            		refer.addHeader(referTo);
		            		
		                     refer.addHeader(contactHeader);
		                     System.out.println("Refer Dialog =\n " + refer);
		                     
		               

		            		ClientTransaction subscribeTid = this.sipProvider.getNewClientTransaction(refer);
		            		subscribeTid.sendRequest();
		            		
		            	
		            	}
		            	else{   //then bye
		            		Request request = requestEvent.getDialog().createRequest("BYE");
		            		ClientTransaction transaction = this.sipProvider.getNewClientTransaction(request);
		            		requestEvent.getDialog().sendRequest(transaction);
		            		
		            	}
		            	
		            }
	          
	           
	            }
	            
	            
	            
	            
	            
	           // Dialog dialog = serverTransaction.getDialog();

	           /* SipProvider provider = (SipProvider) requestEvent.getSource();
	            Request byeRequest = dialog.createRequest(Request.BYE);
	            ClientTransaction ct = provider.getNewClientTransaction(byeRequest);
	            dialog.sendRequest(ct);*/
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }

	}
    public void processBye(RequestEvent requestEvent,ServerTransaction serverTransaction){
		
		
		
		
		
	}
	@Override
	public void processResponse(ResponseEvent res) {
        Response response = (Response) res.getResponse();
        ClientTransaction tid = res.getClientTransaction();
        CSeqHeader cseq = (CSeqHeader) response.getHeader(CSeqHeader.NAME);

        if (tid == null) {
            System.out.println("Stray response -- dropping ");
            return;
        }
       // System.out.println("transaction state is " + tid.getState());
       // System.out.println("Dialog = " + tid.getDialog());
       // System.out.println("Dialog State is " + tid.getDialog().getState());
      //  SipProvider provider = (SipProvider) res.getSource();

          switch(response.getStatusCode()){
              case Response.TRYING:
            	  stateLabel.setText(stateLabel.getText()+"Response.TRYING\n"+response.toString()+"\n");
            	  break;
              case Response.SESSION_PROGRESS:
            	  stateLabel.setText(stateLabel.getText()+"Response.SESSION_PROGRESS\n"+response.toString()+"\n");
            	  break;
              case Response.ACCEPTED:
            	  stateLabel.setText(stateLabel.getText()+"Response.ACCEPTED\n"+response.toString()+"\n");
            	  break;
	          case Response.OK:
	        	  stateLabel.setText(stateLabel.getText()+"Response.ACCEPTED\n"+response.toString()+"\n");
	        	  processOk(res);
	        	  break;  
	          case Response.RINGING:
	        	  stateLabel.setText(stateLabel.getText()+"Response.RINGING\n"
	        	  		+ ""+response.toString()+"\n");
            	  break;
	          case Response.PROXY_AUTHENTICATION_REQUIRED: 
	        	  processProxyAuthenReq(res);
	        	  break;
	          case Response.PAYMENT_REQUIRED:
	        	  stateLabel.setText(stateLabel.getText()+"Response.PAYMENT_REQUIRED\n"+response.toString()+"\n");
	              break;
	          default:
	        	  stateLabel.setText(stateLabel.getText()+"error status\n"+response.getStatusCode()+"\n");   
	        	  break;
          }
         
		
	}
	public void processOk(ResponseEvent res){
		Response response = (Response) res.getResponse();
        ClientTransaction tid = res.getClientTransaction();
        Dialog respDialog = res.getDialog();
        CSeqHeader cseq = (CSeqHeader) response.getHeader(CSeqHeader.NAME);
       
        try {
            if (cseq.getMethod().equals(Request.INVITE)||cseq.getMethod().equals(Request.REGISTER)) {
                Request ackRequest;	
				ackRequest = respDialog.createAck(cseq.getSeqNumber() );
				respDialog.sendAck(ackRequest);
            } 
            else if (cseq.getMethod().equals(Request.CANCEL)) {
                if (respDialog.getState() == DialogState.CONFIRMED) {
                    // oops cancel went in too late. Need to hang up the
                    // dialog.
                    System.out
                            .println("Sending BYE -- cancel went in too late !!");
                    Request byeRequest = dialog.createRequest(Request.BYE);
                    ClientTransaction ct = sipProvider
                            .getNewClientTransaction(byeRequest);
                    respDialog.sendRequest(ct);

                }
            }
          
        } catch (InvalidArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
            
		
		
	}
    public void processProxyAuthenReq(ResponseEvent res){
    	
    	Response response = (Response)res.getResponse();
    	
    	ProxyAuthenticateHeader proxy = (ProxyAuthenticateHeader)response.getHeader("Proxy-Authenticate");
    	ClientTransaction tid = res.getClientTransaction();
    	
    //	System.out.println(proxy.getRealm()+" ! "+proxy.getNonce()+" !! "+proxy.getAlgorithm());
    	//System.out.println("\nauthHeaders "+response.getHeaders(ProxyAuthenticateHeader.NAME));
    	try {
    		
    	AuthenticationHelper authenticationHelper = 
                ((SipStackExt) sipStack).getAuthenticationHelper(new AccountManagerImpl(this.userID,this.userID), headerFactory);
    	
			inviteTid = authenticationHelper.handleChallenge(response, tid, sipProvider, 5);
			inviteTid.sendRequest();
			  invco++;
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
	
	@Override
	public void processDialogTerminated(DialogTerminatedEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("error");
	}

	@Override
	public void processIOException(IOExceptionEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("error");
	}
    
    @Override
	public void processTimeout(TimeoutEvent arg0) {
		// TODO Auto-generated method stub
    	System.out.println("error");
	}
	@Override
	public void processTransactionTerminated(TransactionTerminatedEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("error");
	}
	
	private void addToBinaryTree(){
		
	}
	
	private SessionDescription getSDPFromSIP(Request request){
		ContentTypeHeader contentType = (ContentTypeHeader) request.getHeader(ContentTypeHeader.NAME);
    	ContentLengthHeader contentLen = (ContentLengthHeader) request.getHeader(ContentLengthHeader.NAME);
    	 
         
    	 try {
         if ( contentLen.getContentLength() > 0 && contentType.getContentSubType().equals("sdp") ){
             String charset = null;
             
             if (contentType != null)
                 charset = contentType.getParameter("charset");
             if (charset == null)
                 charset = "UTF-8"; // RFC 3261

             //Save the SDP content in a String
             byte[] rawContent = request.getRawContent();
             String sdpContent;
			
				sdpContent = new String(rawContent, charset);
			if(sdpContent==null)
				return null;

             //Use the static method of SdpFactory to parse the content
             SdpFactory sdpFactory = SdpFactory.getInstance();
             SessionDescription sessionDescription = sdpFactory.createSessionDescription(sdpContent);

             
             return sessionDescription;
         } else {
             System.out.println("It is not a SDP content");
         }
    	 } catch (UnsupportedEncodingException | SdpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
         return null;
	
	}
	public MulticastTree getTree() {return tree;}
	public void setTree(MulticastTree tree) {this.tree = tree;}
	public MulticastNode getNode() {return node;}
	public void setNode(MulticastNode node) {this.node = node;}
	public RTPConnecter getRtpTrans() {return rtpTrans;}
	public void setRtpTrans(RTPConnecter rtpTrans) {this.rtpTrans = rtpTrans;}
	
}
