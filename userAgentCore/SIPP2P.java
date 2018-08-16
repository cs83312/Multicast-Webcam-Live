package userAgentCore;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Vector;

import javax.sdp.EMail;
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
import javax.sip.TransactionTerminatedEvent;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.AllowHeader;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentLengthHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.EventHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.ProxyAuthenticateHeader;
import javax.sip.header.ReferToHeader;
import javax.sip.header.SubscriptionStateHeader;
import javax.sip.header.ToHeader;
import javax.sip.message.Message;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

import gov.nist.javax.sdp.fields.Email;
import gov.nist.javax.sip.SipStackExt;
import gov.nist.javax.sip.SipStackImpl;
import gov.nist.javax.sip.clientauthutils.AuthenticationHelper;
import gov.nist.javax.sip.header.CallID;
import gov.nist.javax.sip.header.ContentType;
import gov.nist.javax.sip.header.Event;
import gov.nist.javax.sip.header.From;
import gov.nist.javax.sip.header.ReferTo;
import gov.nist.javax.sip.header.To;
import multicastTree.MulticastNode;
import multicastTree.MulticastTree;
import signalPacket.SIPHeaders;
import signalPacket.SIPPacket;
import useragent.AccountManagerImpl;

public class SIPP2P implements SipListener {
	
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
    private int port = 5062;
    private int UDPPort;
    private String protocol = "TCP";
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
    public boolean isServer;
    private Map<String,SessionDescription> mediaList = new HashMap<String,SessionDescription>();
    RTPConnecter rtpTrans;
    
    //UI 
    private JTextArea stateLabel;
    private int testCounter=0;
    
    //test
    String tags;
    
    
	public SIPP2P(String addr,int port,String userID,JTextArea transState,boolean isServer){  //server
		this.userID = userID;
		this.isServer = isServer;
		this.ip = addr;
		stateLabel = transState;
		initComponents();
		
	}
	
	public SIPP2P(String addr,int port,String userID,JTextArea transState){ //client
		this.userID = userID;
		isServer = false;
		this.ip = addr;
		this.port = port;
		stateLabel = transState;
		initComponents();
	}
    

	private void initComponents(){
		try {
			System.out.println("start initial component");
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
	public void invite(String toSipUID,
				InetSocketAddress fromClientTalk,
				InetSocketAddress fromClientDataTransmission) throws SdpException{
			
			SIPHeaders header = new SIPHeaders(this.sipProvider);
			Request request ;
			stateLabel.setText(stateLabel.getText()+"fromClientTalk"+fromClientTalk.getAddress().toString().replaceAll("/",""));
			SIPPacket sdp = new SIPPacket();
			
			request = header.createReqPacket(
					//this.userID+"@"+
					fromClientTalk.getAddress().toString().replaceAll("/",""), 
					fromClientTalk.getPort(),
					toSipUID,
					toSipUID.split(":")[1], 
					Integer.valueOf(toSipUID.split(":")[2]), 
					"tcp",
					Request.INVITE,	
			sdp.SDPCreate("live Steam",null,fromClientDataTransmission.getPort())
			);
			
			
			/* 
			 * save mutlicast root
			 * use for client not server 
			 */
			if(rtpTrans.clientNode!=null&&this.rtpTrans.clientNode.getRootProvider()==null)
			{
				rtpTrans.clientNode.setParent(toSipUID.split(":")[1],Integer.valueOf(toSipUID.split(":")[2]));
				rtpTrans.clientNode.setRootProvider(toSipUID.split(":")[1],Integer.valueOf(toSipUID.split(":")[2]));
			
				System.out.println("READY send invite\n"+request.toString());
			}
			
			try {this.sipProvider.sendRequest(request);} 
			catch (SipException e) {e.printStackTrace();}
			
			
		}
	public void invite(String fromURI,String toURI,SessionDescription SDP){
		
		SIPHeaders header = new SIPHeaders(this.sipProvider);
		Request request ;	
		
		request = header.createReqPacket(
				fromURI.split(":")[1], 
				Integer.valueOf(fromURI.split(":")[2]),
				toURI,
				toURI.split(":")[1], 
				Integer.valueOf(toURI.split(":")[2]), 
				"TCP",
				Request.INVITE,	
				SDP);

		try {
			this.sipProvider.sendRequest(request);
			System.out.println("finish");
		} 
		catch (SipException e) {e.printStackTrace();}
		
	}
	
	public void PauseInvite(){
		Request request ;
		SIPHeaders header = new SIPHeaders(this.sipProvider);
		SIPPacket sdp = new SIPPacket();
		
		String rootIP = rtpTrans.clientNode.getRootProvider().getAddress().toString();
		int parentPort = rtpTrans.clientNode.getRootProvider().getTcpPort();
		
		try {
			request = header.createReqPacket(
					//this.userID+"@"+
					this.ip, 
					this.port,
					"sip:"+rootIP+":"+String.valueOf(parentPort),
					rootIP, 
					parentPort, 
					"tcp",
					Request.INVITE,	
			sdp.SDPCreate("Pausing Stream",null,rtpTrans.clientNode.getUdpPort())
			);
			this.sipProvider.sendRequest(request);
			
		} catch (SdpException | SipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public  synchronized void processRequest(RequestEvent req) {
		// TODO Auto-generated method stub
		Request getRequest = req.getRequest();
		String requestType = getRequest.getMethod().toString();
		
		/* System.out.println("\n\nRequest TYPE " +requestType
        + " \nreceived at " + sipStack.getStackName()
        + "\n with server transaction id " + serverTransactionId
        + "\n packet "+getRequest.toString());*/
		 
		 System.out.println("request type***********/"+requestType+"\n");
		switch(requestType){
				case Request.INVITE:
					processInvite(req);
					 stateLabel.setText(stateLabel.getText()+"Request.INVITE\n"+getRequest.toString()+"\n");
					break;
				case Request.ACK:
					processAck(req);
					 stateLabel.setText(stateLabel.getText()+"Request.ACK\n"+getRequest.toString()+"\n");
					break;
				case Request.BYE:
					stateLabel.setText(stateLabel.getText()+"Request.BYE\n"+getRequest.toString()+"\n");
					processBye(req);
					break;
				case Request.REFER:
					System.out.println("Request.REFER\n"+getRequest.toString()+"\n");
					
					try {
						processRefer(req);
					} catch (ParseException | SipException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case Request.NOTIFY:
					stateLabel.setText(stateLabel.getText()+"Request.NOTIFY\n"+getRequest.toString()+"\n");
					processNotify(req);
					break;
				default:
					stateLabel.setText(stateLabel.getText()+"Other Request\n"+getRequest.toString()+"\n");
					break;
		
		
		}
		
		
	}
	public void processInvite(RequestEvent requestEvent) {
			
       SipProvider sipProvider = (SipProvider) requestEvent.getSource();
       Request request = requestEvent.getRequest();
       From from = (From)request.getHeader("From");
       
       SessionDescription sdp = getSDPFromSIP(request);
     
       //check sdp
       try {
    	   System.out.println("processInvite!"+request+"\n"
    			   +sdp.getSessionName().getValue());
    	   
		if(sdp.getSessionName().getValue().matches("Pausing Stream") ==true){
			
			MulticastNode removeNode,endNode;
		   	System.out.println("Pausing Stream");
		   	//refer B(refer to C)
		   	
		   	//step1 search B
		   	
		   	
		   	tree.showNode(tree.root); 
 		    removeNode = tree.searchSpecNode(tree.root,
 		    		   from.getAddress().getURI().toString().split(":")[1],
 		    		  Integer.valueOf(from.getAddress().getURI().toString().split(":")[2]));
 		    
 		   //step1-1 search terminal node called c
 		   endNode = tree.searchEndNode(tree.root);
 		   //step1-2 replace remove node to end node
 		   tree.replaceNode(tree.root, endNode, removeNode,false);
 		   
 		 	//step1-3 get leaf node
 		  SIPPacket SDP = new SIPPacket();
 		  String[] saveLeafNode = new String[3];
 		  
 		  if(removeNode!=null){

 			  //if node is not root leaf then put parent express: e=p:parent@ip:port
 			  if(removeNode.getParent().getTcpPort()!=5062)
 				  saveLeafNode[2] = "p_@"+removeNode.getParent().getAddress()+":"+
 			             String.valueOf(removeNode.getParent().getTcpPort());
 			  
 			  
 			  
 			  for(int count=0;count<removeNode.childNode.size();count++){
 				 saveLeafNode[count] = String.valueOf(count)+"@"+removeNode.childNode.get(count).getAddress()+":"+
 			 			  String.valueOf(removeNode.childNode.get(count).getTcpPort());	
 			  }
 				  
 		
 			 
 			 //put leaf node to sdp (email)
 			try {
				SessionDescription newSDP = SDP.SDPCreate("Node Transform", saveLeafNode, 0);
				System.out.println("trans node"+endNode.getAddress()+":"+endNode.getTcpPort());
				invite("sip:"+this.ip+":"+String.valueOf(5062),
						"sip:"+endNode.getAddress()+":"+endNode.getTcpPort(),
						newSDP);
			} catch (SdpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}}
 		  
		   }
		else if(sdp.getSessionName().getValue().matches("Node Transform")== true){
			 
			// get e_mail
			Vector<EMail> e;
			
			
			e = sdp.getEmails(true);
			SIPPacket SDP = new SIPPacket();
			
			/*
			 * 
			 * do default response
			 * 
			 */
			
			//send bye for parent node
			//暫時用invite 去中斷連線
			
			try {
				SessionDescription newSDP = SDP.SDPCreate("Leave Node", null, 0);
				invite("sip:"+this.ip+":"+String.valueOf(this.port),
						"sip:"+rtpTrans.clientNode.getParent().getAddress()+":"+String.valueOf(rtpTrans.clientNode.getParent().getTcpPort()),
						newSDP);
			} catch (SdpException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	
			// step 2 send invite to {B,C}
			String s= "Replace Stream";
			for(EMail node: e){
	
				String ips = node.toString().split("@")[1].split(":")[0];
				String ports = node.toString().split("@")[1].split(":")[1].replaceAll("\r|\n", "");
				System.out.println(ports);
				String toURI = "sip:"+ips+":"+ports;
				//防止自己傳給自己
				if(!ports.matches(String.valueOf(this.port))){
					try {
						SessionDescription newSDP = SDP.SDPCreate(s, null, 0);
						invite("sip:"+this.ip+":"+String.valueOf(this.port),
								toURI,
								newSDP);
						System.out.println("debug2");
					} catch (SdpException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			
			
			}
		else if(sdp.getSessionName().getValue().matches("Replace Stream")== true){
			//step 3
			//修改父結點
			System.out.println(from);
			/*this.node.setParent(from.getAddress().getURI().toString().split(":")[1],
					Integer.valueOf(from.getAddress().getURI().toString().split(":")[2]));*/
			
			
			//response ok include rtp sdp data
			
			try {
				ContentType contentType= new ContentType();
			    contentType.setContentType("application","sdp");
			    SIPPacket SDP = new SIPPacket();
			    SessionDescription newSDP = SDP.SDPCreate("Replcae Stream", null, rtpTrans.getClientPort());
				Response ok = 
				messageFactory.createResponse(Response.OK,request,contentType,newSDP);
				sipProvider.sendResponse(ok);
				
				
				
				
			} catch (ParseException | SdpException | SipException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		}
		else if(sdp.getSessionName().getValue().matches("Leave Node")== true){
			System.out.println("in the "+this.port+"leave node"+from);
			int removePort = Integer.valueOf(from.getAddress().getURI().toString().split(":")[2]);
			// search node which is leave node
			if(rtpTrans.clientNode.childNode.size()!=0){
				
			
			for(MulticastNode n:rtpTrans.clientNode.childNode){
				
				System.out.println("after delete"+n);
				
				
			}
			for(MulticastNode n:rtpTrans.clientNode.childNode){
				
				if(n.getTcpPort() == removePort){
					rtpTrans.clientNode.childNode.remove(n);
					break;
				}
				
				
			}
			}
			else{
				System.out.println("zero child node");
			}
			
			Response ok;
			try {
				ContentType contentType= new ContentType();
			    contentType.setContentType("application","sdp");
			    SIPPacket SDP = new SIPPacket();
			    SessionDescription newSDP = SDP.SDPCreate("Leave Node", null, rtpTrans.getClientPort());
				ok = messageFactory.createResponse(Response.OK,request,contentType,newSDP);
				sipProvider.sendResponse(ok);
			} catch (ParseException | SipException | SdpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			
		}
		else if(sdp.getSessionName().getValue().matches("live Steam")== true){
		        try {
		         // System.out.println("dialog______\n"+requestEvent.getDialog());
		            /*
		             * put sdp to map
		             */		         
		           if(sdp!=null){
		        	   CallID call = (CallID) request.getHeader("Call-ID");
		               mediaList.put(call.getCallId(),sdp);
		           }   
		            // reliable provisional response. Use the API here!
		            Response tryingResponse = messageFactory.createResponse(Response.TRYING, request);
		            sipProvider.sendResponse(tryingResponse);
		            
		            Address contactAddress = this.addressFactory.createAddress("sip:"+userID+"@" + "134.208.3.13"+ ":" + 5062);
		            ContactHeader contact = this.headerFactory.createContactHeader(contactAddress);   
		            Response ok = messageFactory.createResponse(Response.OK,request);
		            ok.addHeader(contact);
		            sipProvider.sendResponse(ok);
		            
		            tags = from.getTag();
		            
		        } catch (Exception ex) {
		            ex.printStackTrace();
		           // junit.framework.TestCase.fail("Exit JVM");
		        }
		   }
	} catch (SdpParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}  
  
   }
	public void processRefer(RequestEvent requestEvent) throws ParseException, SipException{
		
		SipProvider sipProvider = (SipProvider) requestEvent.getSource();
       Request refer = requestEvent.getRequest();
    
       // Check that it has a Refer-To, if not bad request    防呆
       ReferToHeader refTo = (ReferToHeader) refer.getHeader( ReferToHeader.NAME );
     
       
       if (refTo==null) {
           Response bad = messageFactory.createResponse(Response.BAD_REQUEST, refer);
           bad.setReasonPhrase( "Missing Refer-To" );
           sipProvider.sendResponse( bad );
           
           return;
       }
       
    // Always create a ServerTransaction, best as early as possible in the code
       Response responseAccept = null;
	
    // Check if it is an initial SUBSCRIBE or a refresh / unsubscribe
       String toTag = Integer.toHexString( (int) (Math.random() * Integer.MAX_VALUE) );
       responseAccept = messageFactory.createResponse(202, refer);
       ToHeader toHeader = (ToHeader) responseAccept.getHeader(ToHeader.NAME);
       
       // should have matched
       if (toHeader.getTag()!=null) {
           System.err.println( "####ERROR: To-tag!=null but no dialog match! My dialog=" + dialog.getState() );
       }
       toHeader.setTag(toTag); // Application is supposed to set.
     

       // Both 2xx response to SUBSCRIBE and NOTIFY need a Contact

       responseAccept.addHeader(this.contactHeader);
      //send accept
       sipProvider.sendResponse(responseAccept);

      // sendNotify(sipProvider,refer);
       
       //build invite to node 
       
       int getLocalRTPPort = 0 ;
       if(rtpTrans==null)
    	   System.err.println("doesn't allocate rtp api\n");
       else
       {
	    	   getLocalRTPPort = rtpTrans.getClientPort();
	    	 
		       InetSocketAddress from = new InetSocketAddress(this.ip,this.port);
		       InetSocketAddress fromRTP = new InetSocketAddress(this.ip,getLocalRTPPort);
		      
		       try {
		    	   
		    	   /*
		    	    *  *** important override the node of parent
		    	    */
		    	   if(rtpTrans.clientNode.getParent()!=null){
		    		   rtpTrans.clientNode.setParent(refTo.getAddress().getURI().toString().split(":")[1],
			    				  Integer.valueOf(refTo.getAddress().getURI().toString().split(":")[2]));
			    	  }  	   
		    	  
		    	   System.out.println("TEST get refer-to"+refTo.getAddress().getURI().toString()+"\n"+
		    			   from+"\n"+
		    			   fromRTP+"\n");
		    	
		    	  
				this.invite(refTo.getAddress().getURI().toString(),
						   from,
						   fromRTP);
		       }catch (SdpException e) {
		   		// TODO Auto-generated catch block
		   		e.printStackTrace();
		   	}
       } 
       
       
	}
	
	
	public void sendNotify(SipProvider provider,Request refer){
		
		
		Request notify;
		 
		SIPHeaders header = new SIPHeaders(provider);
		From from = (From)refer.getHeader("From");
		 
		To to = new To();
		to.setAddress(from.getAddress());
		CallID callID = (CallID)refer.getHeader("Call-ID");
		
		 System.out.println("NOTIFY parameter\n"+
	               "fromaddr "+ this.ip+":"+this.port+"\n"+
	               "toaddr "+to.getAddress().getURI()+"\n"+
	               "port "+to.getAddress().getURI().toString().split(":")[2]+"\n"+
	               "call id"+callID+"\n");
		 
		notify= header.createAck(
      		   this.ip,
      		   this.port,  	            		   
      		   to.getAddress().getURI().toString(),
      		   to.getAddress().getURI().toString().split(":")[1], 
      		   Integer.valueOf(to.getAddress().getURI().toString().split(":")[2]), 
      		   protocol, 
      		 callID,
      		   Request.NOTIFY);
		 ContentTypeHeader ct ;
        
		try {
			EventHeader referEvent = headerFactory.createEventHeader("refer");
			long id = ((CSeqHeader) refer.getHeader("CSeq")).getSeqNumber();
	        notify.addHeader(referEvent);
			ct = headerFactory.createContentTypeHeader("message","sipfrag");;
			 ct.setParameter( "version", "2.0" );
	         notify.setContent( "SIP/2.0 " + 100 + ' ' + "Trying", ct );
	         
			SubscriptionStateHeader sstate = headerFactory.createSubscriptionStateHeader(SubscriptionStateHeader.ACTIVE);
			notify.addHeader(sstate);
			
			AllowHeader allowHeader = this.headerFactory.createAllowHeader("INVITE, ACK, CANCEL, OPTIONS, BYE, REFER, NOTIFY");
			notify.addHeader(allowHeader);
	         
	         
	         System.out.println("send notify\n"+notify);
			provider.sendRequest(notify);
		} catch (SipException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}
	public void sendBye(String fromURI,String toURI){
		
		SIPHeaders header = new SIPHeaders(this.sipProvider);
		Request request ;	
		
		request = header.createReqPacket(
				fromURI.split(":")[1], 
				Integer.valueOf(fromURI.split(":")[2]),tags,
				toURI,
				toURI.split(":")[1], 
				Integer.valueOf(toURI.split(":")[2]), 
				"TCP",
				Request.BYE);
		
		
		
		try {
			System.out.println("sending... "+tags+"\n"+request);
			this.sipProvider.sendRequest(request);
			System.out.println("finish");
		} 
		catch (SipException e) {e.printStackTrace();}
	
	}
    public void processNotify(RequestEvent requestEvent){
	   SipProvider provider = (SipProvider) requestEvent.getSource();
      Request notify = requestEvent.getRequest();
      try {
        /*  if (serverTransaction == null) {
              
       	   serverTransaction = provider.getNewServerTransaction(notify);
          }
          Dialog dialog = serverTransaction.getDialog();*/

          if (dialog != null) {
              logger.info("Dialog State = " + dialog.getState());
          }
          Response response = messageFactory.createResponse(200, notify);
          // SHOULD add a Contact
          ContactHeader contact = (ContactHeader) contactHeader.clone();
          ((SipURI)contact.getAddress().getURI()).setParameter( "transport", "TCP" );
          response.addHeader( contact );
        //  logger.info("Transaction State = " + serverTransaction.getState());
          //serverTransaction.sendResponse(response);
          provider.sendResponse(response);
          
      } catch (Exception ex) {
          ex.printStackTrace();
          logger.error("Unexpected exception",ex);
         

      }
  }
    public void processAck(RequestEvent requestEvent){
		 try {
	        //    System.out.println("shootme: got an ACK! start rtp stream");
	         //   System.out.println("Dialog State = " + dialog.getState());
	          //  System.out.println("Dialog State = " + requestEvent.getRequest().toString());
	            /*
	             * match the same client them start rtp transform
	             */
	            CallID call = (CallID) requestEvent.getRequest().getHeader("Call-ID");
	            From from = (From)requestEvent.getRequest().getHeader("From");
	            
	            SessionDescription sdp = mediaList.get(call.getCallId());
	            
	            if(sdp==null)
	            	stateLabel.setText(stateLabel.getText()+"doesn't get sdp\n");
	            else{
		            	
		            	if(isServer){
			            	MulticastNode notRootLeaf,newNode;
			            	 String media = sdp.getMediaDescriptions(true).toString();
			            	String addr = sdp.getOrigin().getAddress().toString();
			            	From sipAddr = (From)requestEvent.getRequest().getHeader("From");	
			            	//int tcpPort = Integer.valueOf(sipAddr.toString().split(":")[3].split(">")[0]);
			            	int udpPort = Integer.valueOf(media.split(" ")[1]);
			            	
			            	newNode =  new MulticastNode(from.getAddress(),
			            			addr,
			            			Integer.valueOf(from.getAddress().getURI().toString().split(":")[2]),
			            			udpPort);
			            		
			            	notRootLeaf = tree.addNode(tree.root,newNode);
			            
			            	/*
			            	 * 如果不是root leaf ,通知client去辦訪target data sender
			            	 */
				            	if(notRootLeaf!=null){
		
				            		
				            		Request refer;
				            		
				            		SIPHeaders header = new SIPHeaders(sipProvider);
				            		To to = new To();
				            		to.setAddress(from.getAddress());
				 	              
				 	              refer = header.createAck(
				 	            		   this.ip,
				 	            		   this.port,  	            		   
				 	            		   to.getAddress().getURI().toString(),
				 	            		   to.getAddress().getURI().toString().split(":")[1], 
				 	            		   Integer.valueOf(to.getAddress().getURI().toString().split(":")[2]), 
				 	            		   protocol, 
				 	            		  call,
				 	            		   Request.REFER);
				            		ReferTo referTo = new ReferTo();
				            		referTo.setAddress(notRootLeaf.getSipURI());
				            		refer.addHeader(referTo);
				            		AllowHeader allowHeader = this.headerFactory.createAllowHeader("INVITE, ACK, CANCEL, OPTIONS, BYE, REFER, NOTIFY");
				            		refer.addHeader(allowHeader);
				                    EventHeader event = this.headerFactory.createEventHeader("Refer");
				                    refer.addHeader(event);
				                     System.out.println("Refer Dialog =\n " + refer);
				                     
				                     this.sipProvider.sendRequest(refer);	         	
				            	}
				            	else{   //then bye
				            		//Request request = requestEvent.getDialog().createRequest("BYE");
				            	//	ClientTransaction transaction = this.sipProvider.getNewClientTransaction(request);
				            	//	requestEvent.getDialog().sendRequest(transaction);
				            		
				            	}
			            	
			            }
		            	else if(!isServer){
		            		if(sdp.getSessionName().getValue().matches("Replace Node")== true){
		            			
		            			//修改父結點
		            			rtpTrans.clientNode.setParent(from.getAddress().getURI().toString().split(":")[1],
		            					Integer.valueOf(from.getAddress().getURI().toString().split(":")[2]));
		            		}
		            		else {
		            		System.out.println(from.getAddress());
		            		 String media = sdp.getMediaDescriptions(true).toString();
		            		 int RTPPort = Integer.valueOf(media.split(" ")[1]);
		            		 String addr = sdp.getOrigin().getAddress().toString();
		            		 
		            		 MulticastNode newNode;
		            		 newNode=  new MulticastNode(from.getAddress(),addr,
		            				 Integer.valueOf(from.getAddress().getURI().toString().split(":")[2])
		            				 ,RTPPort);
		            		 
		            		 rtpTrans.clientNode.childNode.add(newNode);
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
   public void processBye(RequestEvent requestEvent){
	   From from = (From)requestEvent.getRequest().getHeader("From");
		
		System.out.println("get bye from: "+from.getAddress());
		
		
	}
	
	public synchronized void processResponse(ResponseEvent res) {
       Response response = (Response) res.getResponse();
      
       CSeqHeader cseq = (CSeqHeader) response.getHeader(CSeqHeader.NAME);
       System.out.println("response type------\n"+response);
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
	        	  stateLabel.setText(stateLabel.getText()+"Response.OK"
	        	  		+ "\n"+response.toString()+"\n");
	        	  processOk(res);
	        	  break;  
	          case Response.RINGING:
	        	  stateLabel.setText(stateLabel.getText()+"Response.RINGING\n"
	        	  		+ ""+response.toString()+"\n");
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
		SipProvider sipProvider =(SipProvider)res.getSource();
		SessionDescription sdp=null;
       CSeqHeader cseq = (CSeqHeader) response.getHeader(CSeqHeader.NAME);
       ContentType type = (ContentType)response.getHeader(ContentType.NAME);
       
       if(type!=null){
            sdp = getSDPFromSIP(response);
      System.out.println("get replace stream ok "+sdp);
       }
      
      try {
    	  if(sdp!=null){
				if(sdp.getSessionName().getValue().matches("Replcae Stream")== true){
					//提取udp port
					try {
						String media = sdp.getMediaDescriptions(true).toString();
						int udpPort = Integer.valueOf(media.split(" ")[1]);
		            	To to = (To)response.getHeader("To");
		            	int fromTcpPort =Integer.valueOf(to.getAddress().getURI().toString().split(":")[2]);
		            	String fromIP =to.getAddress().getURI().toString().split(":")[1]; 
						
						  MulticastNode newNode =  new MulticastNode(to.getAddress(),
								  fromIP,
		            			fromTcpPort,
		            			udpPort);  
						
					rtpTrans.clientNode.childNode.add(newNode);
						  
					} catch (SdpException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					  
				  }
				
    	  } 
    	  else{
			if (cseq.getMethod().equals(Request.INVITE)) {
			               Request ackRequest;
			               SIPHeaders header = new SIPHeaders(sipProvider);
			               To to = (To)response.getHeader("To");
			               From from = (From)response.getHeader("From");
			               CallID callID = (CallID)response.getHeader("Call-ID");
			               /*System.out.println("test parameter\n"+
			    	               "fromaddr"+from.getAddress().getURI()+"\n"+
			    	               "toaddr"+to.getAddress().getURI()+"\n");*/
			               
			               ackRequest = header.createAck(
			            		   this.ip,
			            		  this.port, 
			            		   to.getAddress().getURI().toString(),
			            		   to.getAddress().getURI().toString().split(":")[1], 
			            		   Integer.valueOf(to.getAddress().getURI().toString().split(":")[2]), 
			            		   protocol, 
			            		   callID,
			            		   Request.ACK);
			               
							try {
								sipProvider.sendRequest(ackRequest);
							} catch (SipException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			           }
    	  }
	} catch (NumberFormatException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (SdpParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 

	}

	
	@Override
	public void processDialogTerminated(DialogTerminatedEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processIOException(IOExceptionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
   
   @Override
	public void processTimeout(TimeoutEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void processTransactionTerminated(TransactionTerminatedEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	private void addToBinaryTree(){
		
	}
	
	private SessionDescription getSDPFromSIP(Message request){
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
	public RTPConnecter getRtpTrans() {return rtpTrans;}
	public void setRtpTrans(RTPConnecter rtpTrans) {this.rtpTrans = rtpTrans;}
	public void setUDPPort(int udp){this.UDPPort = udp;}
    
}
