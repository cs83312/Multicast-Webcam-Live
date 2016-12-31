package useragent;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

/*		*
 * 	File:						useragent/Main.java
 * 
 * 	Use:						The start point of useragent package. 
 * 								It is the client's (provider's and viewer's) interface.  
 * 
 * 	Update Date: 	2016. 5. 22
 * */

public class Main {
	/* Start of Constant  Variable*/
	private static int defaultMaxConnect = 2; 
	private static String userName; 
	private static int maxConnect; 
	private static String sendReceive;
	
	private static String genUserName(){
		// To generate the SIP user's name
		String username = "";
		Random ran = new Random(); 
		username = String.valueOf((ran.nextInt(100000000)));  
		return username; 
	}
	
	private static void setInfo(String user, int max){
		userName = user; 
		maxConnect = max; 
	}
	private static void setInfo(String user, int max,String sendRecv){
		userName = user; 
		maxConnect = max; 
		sendReceive = sendRecv;
	}
	private static void setInfo(){
		userName = genUserName(); 
		maxConnect = defaultMaxConnect; 
	}
	

	private static String readConfigFileToGetSIPServer(){
		try{
			BufferedReader br = new BufferedReader(new FileReader("config")); 
			String sipAddrs = br.readLine(); 
			String p2pAddrs = br.readLine();
			String tmp = br.readLine(); 
			br.close(); 
			if(tmp == null){
				return sipAddrs;  
			}
			else 
				return null;  
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null; 
		} catch (IOException e) {
			e.printStackTrace();
			return null; 
		} 	
	}
	
	public static void main(String[] args) throws IOException {
		FileSetting fileSetting = new FileSetting("usersetting");

		
		// write the setting file
		FrameSetting setting = new FrameSetting(); 
		Thread thread = new Thread(setting); 
		thread.start(); 
		
		while(setting.state != 1){
			System.out.print(""); 
		}
		
		// Read the setting file ( userName, maxConnect )
		if (fileSetting.readFile()  != false){
			setInfo(fileSetting.getUserName(), fileSetting.getMaxConnect()); 
		}
		else {
			setInfo();  
		}
				
			String addrs = readConfigFileToGetSIPServer(); 
			String sipAddrs[] = addrs.split(":");  
		  
	
			SIPCommunicator sip = new SIPCommunicator(sipAddrs[0], Integer.parseInt(sipAddrs[1]));
			sip.setUserName(userName); 
			sip.register(); 
					
		
		// 閮餃��嚗神�鞈��
	/*	fileSetting.setMaxConnect(maxConnect); 
		fileSetting.setUserName(userName); 
		fileSetting.writeFile(); 
*/
		// ���蝙���
		FrameMain ui = new FrameMain(userName, maxConnect);
		Thread mainThread = new Thread(ui); 
		mainThread.start(); 
		
	

		
	}

}
