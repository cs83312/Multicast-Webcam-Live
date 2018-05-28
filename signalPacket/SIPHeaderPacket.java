package signalPacket;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.sip.InvalidArgumentException;
import javax.sip.header.FromHeader;

import gov.nist.javax.sip.address.AddressFactoryImpl;
import gov.nist.javax.sip.address.AddressImpl;
import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.header.CSeq;
import gov.nist.javax.sip.header.CallID;
import gov.nist.javax.sip.header.ContentType;
import gov.nist.javax.sip.header.From;
import gov.nist.javax.sip.header.MaxForwards;
import gov.nist.javax.sip.header.ReferTo;
import gov.nist.javax.sip.header.To;
import gov.nist.javax.sip.header.Via;

public class SIPHeaderPacket {
	
	SipUri uri;
	CallID callidheader;
	CSeq cseq;
	From from;
	To to;
	List<Via> viaList;
	MaxForwards maxForwards;
	ContentType contentType;
	ReferTo refer;
	
    public SIPHeaderPacket(){
    	uri = new SipUri();
    	callidheader = new CallID();
    	cseq = new CSeq();
    	from = new From();
    	to = new To();
    	viaList = new ArrayList<Via>();
    	maxForwards = new MaxForwards();
    	contentType = new ContentType();
    	refer = new ReferTo();
    }
	
	public void setMaxForwards(int maxForwards) throws InvalidArgumentException {this.maxForwards.setMaxForwards(maxForwards);}
	public void setContentType(String contentType) throws ParseException {this.contentType.setContentType(contentType);}
	public void setUri(String uri) throws ParseException {this.uri.setHost("134.208.3.13");}
	public void setCallidheader(String callIDHeader) throws ParseException {this.callidheader.setCallId(callIDHeader);}
	
	public void setCseq(String headerName,int seqNumber,String method) throws InvalidArgumentException, ParseException {
		this.cseq.setHeaderName(headerName);
		this.cseq.setSeqNumber(seqNumber);
		this.cseq.setMethod(method);
	}
	public void setFrom(String sip_Address,String tag) throws ParseException {
		AddressFactoryImpl address = new AddressFactoryImpl();

		
		//this.from.setHeaderName(headerName);
		this.from.setAddress(address.createAddress(sip_Address));
		this.from.setTag(tag);
		
		
	}
	public void setTo(String sip_To) throws ParseException {
		AddressFactoryImpl address = new AddressFactoryImpl();
		
		this.to.setAddress(address.createAddress(sip_To));
	}
	public void setVia(String headerName,String host,String branch,int port) throws ParseException, InvalidArgumentException {
		Via via = new Via();
		via.setHeaderName(headerName);
		via.setHost(host);
		via.setBranch(branch);
		via.setPort(port);
		viaList.add(via);
	}
	public void setRefer(SipUri uri) {
		AddressImpl addr = new AddressImpl();
		
		
		addr.setAddess(uri);
		addr.setAddressType(1);
		this.refer.setAddress(addr);
		
	}
	
	

	public ReferTo getRefer() {return refer;}
	
	public SipUri getUri() {return uri;}

	public CallID getCallidheader() {return callidheader;}

	public CSeq getCseq() {return cseq;}

	public FromHeader getFrom() {return from;}

	public To getTo() {return to;}

	public List<Via> getViaList() {return viaList;}

	public MaxForwards getMaxForwards() {return maxForwards;}

	public ContentType getContentType() {return contentType;}
	
	

	
	
	

}
