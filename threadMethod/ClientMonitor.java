package threadMethod;

import javax.swing.JTextField;

import multicastTree.MulticastNode;
import userAgentCore.SIPP2P;

public class ClientMonitor extends Thread{
	
	
	SIPP2P sipP2P;
	private JTextField childNode;
	
	int preFrame=0;
	int lossPacketNumber=0;
	int packetDelay=0;
	
	long preTime=0;
	long nowTime=0;
	long delayTime=0;
	static int FRAME_PERIOD;
	
	
	
	public ClientMonitor(SIPP2P sipP2P,JTextField childNode){
		this.sipP2P = sipP2P;
		this.childNode = childNode;
	}
	public void run()  
    {  
        while(true){
        	       	
        	try {
        			
        		setNodeState();
        		
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
	
	public void setNodeState(){
		
    		StringBuffer sBuffer = new StringBuffer("");
    		if(sipP2P.getRtpTrans().getClientNode().childNode.size()!=0)
				for(MulticastNode node:sipP2P.getRtpTrans().getClientNode().childNode)
					sBuffer.append(node.getAddress()+":"+node.getTcpPort()+"\r\n");
					
					childNode.setText(sBuffer.toString());	

		
    }

	
	
	public void packetLost(int seqNumber,long nowTime){
		
		if(seqNumber - preFrame > 1){
			
			if(seqNumber - preFrame>10){
			delayTime = nowTime - preTime;
			
			}
			
			lossPacketNumber++;
		}
		
		preFrame = seqNumber;
			
	}
	public void propagationDelay(long nowTime){
		
		preTime = nowTime;
	}
	
	public int getLossPacketNumber() {
		return lossPacketNumber;
	}
	public long getDelayTime() {
		return delayTime;
	}
	
	

}
