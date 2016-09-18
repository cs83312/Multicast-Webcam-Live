package useragent;

/*		*
 * 	File:						useragent/FrameMain.java
 * 
 * 	Use:						The main Frame of useragent. 
 * 
 * 	Update Date: 	2016. 6. 2
 * */

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;
import javax.swing.*;



public class FrameMain extends JFrame implements ActionListener, Runnable{
	
	private PlayerPanel playerPanel; 
	
	private String URI; 
	private int maxConnect; 
	private String parentURI; 
	private FrameMain frame;
	public int state;
	

	private MenuBar setMainMenuBar(){
		
		MenuBar menuBar = new MenuBar(); 
		menuBar.setFont(new Font("sans", Font.BOLD, 16)); 
		
		// Stream
		Menu menuStream = new Menu(); 
		menuStream.setLabel("Stream"); 
		menuBar.add(menuStream); 
		
		// Stream > Create - Create a new real-time media streaming & create the seed.
		MenuItem menuItemCreate = new MenuItem("Create"); 
		menuItemCreate.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e){
					
					FileSetting sendFile = new FileSetting("usersetting","SENDRECV TRUE");
					if(sendFile.readFile()==true)
					sendFile.writeFileInSendRec();
					state=1;
					
					sendFile.readFile();
					System.out.println(sendFile.getSendReceive());
					String sipServer = readConfigFileToGetSIPServer(); 
					String sipServerIP[] = sipServer.split(":");
					String sipParentIP = null; 
					String sipMyIP = null;
					System.out.println(sipServerIP[0] + Integer.parseInt(sipServerIP[1]));
					SIPCommunicator sendRecvCom = new SIPCommunicator(sipServerIP[0], Integer.parseInt(sipServerIP[1]),"SENDRECV TRUE");
					try {
						sendRecvCom.senderReceiver();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					
					
					
					// TODO: 開啟擷取影片之 Device 
					
					final Graphics2D g2d = (Graphics2D)frame.getGraphics();
					final WebCam cam = new WebCam();

					
					final Thread t = new Thread(){
						@Override
						public void run(){
							RTPConnecter RTPServer = new RTPConnecter();
							System.out.println("client has come");
							//ImageCodec imgCodec = new ImageCodec();
							while(true){
								
								BufferedImage img = (BufferedImage) cam.startWebCam();
								// way two 
								   ByteArrayOutputStream baos = new ByteArrayOutputStream();        
								   try {
									ImageIO.write(img, "jpg", baos);
									//byte[] buf = imgCodec.encodeImage(img);
									baos.flush();
									 byte[] buffer = baos.toByteArray();
									 
									 g2d.drawImage(img,0,0,null);
							
									 
									 
									 
								//	RTPServer.RTPSend("0",0,buffer);
									
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} 
								  
								
							}
					}
						
					};
					t.setDaemon(true);
					t.start();
					
					
					
					
					//********** call vlc to be stream server **********  
					/*try {
						Runtime rt = Runtime.getRuntime();
						Process proc = rt.exec(new String[]{"bash", "-c", "/usr/bin/cvlc screen:// :input-slave=alsa:// :screen-fps=10 :screen-follow-mouse :sout='#transcode{acodec=mpga, vcodec=h264, vb=1024, scale=0.6, venc=x264{scenecut=100, bframes=0, keyint=10}} :std{access=http, mux=ts, dst=:8080}'"}); 
					}
					catch (Throwable t){
						t.printStackTrace(); 
					}*/
					 //---*************************************-/
					  
					
					
					
					
					
					
				}
			}
		); 
		menuStream.add(menuItemCreate); 
		
		menuStream.addSeparator(); 
		
  		// Stream > Open - Open a seed to watch the stream.
		MenuItem menuItemOpen = new MenuItem("Open"); 
		menuItemOpen.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e){		
					FileSetting sendFile = new FileSetting("usersetting","SENDRECV FALSE");
					if(sendFile.readFile()==true)
					sendFile.writeFileInSendRec();
					state=1;
					System.out.println("sendrecv set false");
					// 選擇 Seed 後取得來源之 URI 
					String parent = null;
							//onOpenFileAndGetParentURI();
					
					final RTPConnecter clientRecv =new RTPConnecter("127.0.0.1",1234);
					final Graphics2D g2d = (Graphics2D)frame.getGraphics();
					Thread recv = new Thread(){
						
						@Override
						public void run(){
							while(true){
							//	g2d.drawImage(clientRecv.RTPreceive(),0,0,null);
								
								
							//	System.out.println("client receiving");
							}
							
						}
						
					};
					recv.start();
					
					
					
					/*if(parent == null){
						JFrame qq = new JFrame(); 
						qq.setSize(360, 60); 
						qq.setLocation(400, 400); 
						JTextField tf = new JTextField(); 
						tf.setText("這場直播可能太熱門了，已經加不進去了\n你可以稍等再嘗試加入！"); 
						tf.setEditable(false); 
						qq.add(tf, BorderLayout.CENTER); 
						qq.setVisible(true); 
					}*/
					
					
					// TODO: 取得 URI 之 連線資訊
					 
					/*String sipServer = readConfigFileToGetSIPServer(); 
					String sipServerIP[] = sipServer.split(":");
					String sipParentIP = null; 
					String sipMyIP = null;
					
					SIPCommunicator sip = new SIPCommunicator(sipServerIP[0], Integer.parseInt(sipServerIP[1]));
					try{
						
						System.out.println("parent = " + parent); 
						sipParentIP = sip.invite(parent.split("@")[0]);
						sipParentIP = (sipParentIP.split(" ")[1]).split(":")[0]; 
					}
					catch(IOException io){
						io.printStackTrace(); 
					}*/
					
					//********** use mplayer to playback **********  
					
					
					
					// TODO: 以 SIP 方式建立連線，並取得 RTP 串流資訊
					//playerPanel.onReceiveRTP();
				}
			}
			
		);
		menuStream.add(menuItemOpen); 
		
		
		// Stream > Close - Close the stream which is watching.
		/*MenuItem menuItemClose = new MenuItem("Close"); 
		menuItemClose.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e){
					// TODO: 傳送 BYE 訊息給轉播端（SIP）
					
					// TODO: 離開 P2P 網路
				}
			}
		);
		menuStream.add(menuItemClose); */
	
		menuStream.addSeparator(); 
	
  		// Stream > Exit - To exit software.			
		final MenuItem menuItemExit = new MenuItem("Exit"); 
		menuItemExit.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e){
					onExit(); 
				}
			}
		); 
		menuStream.add(menuItemExit); 
	/*
		// Control
		Menu menuControl = new Menu(); 
		menuControl.setLabel("Control"); 
		menuBar.add(menuControl); 			
			
		// Control > Play - To play the stream if the seed was read. (When open a seed will auto to play it. )
		MenuItem menuItemPlay = new MenuItem("Play"); 
		menuItemPlay.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e){
					// TODO: Action Event
				}
			}
		);			
		menuControl.add(menuItemPlay); 
		
  		// Control > Pause - To pause the stream if the streaming is player. (But the receive & forwarding will continue. )
		MenuItem menuItemPause = new MenuItem("Pause"); 
		menuItemPause.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e){
					// TODO: Action Event
				}
			}
		);			
		menuControl.add(menuItemPause); 			
		
  		// Control > Stop - To stop connection. (The receive & forwarding will stop. )
		MenuItem menuItemStop = new MenuItem("Stop"); 
		menuItemStop.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e){
					// TODO: Action Event
				}
			}
		);			
		menuControl.add(menuItemStop); 			
		*/
		// Other
		Menu menuOther = new Menu(); 
		menuOther.setLabel("Other"); 
		menuBar.add(menuOther); 	
		
  		// Other > Help - To display the help pages.
		MenuItem menuItemHelp = new MenuItem("Help"); 
		menuItemHelp.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e){
					// TODO: 顯示使用教學視窗
				}
			}
		);			
		menuOther.add(menuItemHelp); 				
	
		menuOther.addSeparator(); 
		
  		// Other > Configuration - To display the configuration page.
		MenuItem menuItemConfig = new MenuItem("Configuration"); 
		menuItemConfig.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e){
					// TODO: 打開更改設定之視窗
					FrameSetting frameSetting =  new FrameSetting(); 
				}
			}
		);			
		menuOther.add(menuItemConfig); 				
		menuOther.addSeparator();
		
	  	// Other > About - To display 
		MenuItem menuItemAbout = new MenuItem("About"); 
		menuItemAbout.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e){
					// TODO: Action Event
				}
			}
		);			
		menuOther.add(menuItemAbout); 		 
	
		return menuBar; 

	}
	
	private void onExit(){
		this.dispose();
	}

	private String readConfigFileToGetSIPServer(){
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
	
	private String readConfigFileToGetP2PServer(){
		try{
			BufferedReader br = new BufferedReader(new FileReader("config")); 
			String sipAddrs = br.readLine(); 
			String p2pAddrs = br.readLine();
			String tmp = br.readLine(); 
			br.close(); 
			if(tmp == null){
				return p2pAddrs;  
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
	
	/*private String readP2PFileToGetURI(String fileName) {
		// read the P2P file
		try { 
			BufferedReader br = new BufferedReader(new FileReader(fileName)); 
			String p2pIP = br.readLine(); 
			String p2pID = br.readLine();
			String tmp = br.readLine(); 
			br.close(); 
			
			
			if(tmp == null){
				// parse data
				String addrs[] = p2pIP.split(":"); // IP:port 
				
				// 與 P2P Server 建立連線，取得轉播 URI
				P2PTracker tracker; 
				int prt = Integer.parseInt(addrs[1]); 
				tracker = new P2PTracker(addrs[0], prt, URI, maxConnect, p2pID);  
				if(tracker.getState() == true){
					return tracker.getData(); 
				}
				else{
					return null; 
				}
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
	
	private String onOpenFileAndGetParentURI()
	{
		final JFileChooser chooser = new JFileChooser();
		if (chooser.showOpenDialog(FrameMain.this) ==JFileChooser.APPROVE_OPTION) {
			final String urlStr = URLUtils.createUrlStr(chooser.getSelectedFile());
			String urlFileName = urlStr.substring(7);  
			System.out.println("urlFileName = " + urlFileName); 
			
			parentURI = readP2PFileToGetURI(urlFileName);
			System.out.println("URI = " + URI); 
			
			return parentURI; 
		}
		return null;
	} */
	
	
	public FrameMain(String username, int max){	
		
		String sipServer = readConfigFileToGetSIPServer(); 
		String sipServerIP = sipServer.split(":")[0]; 
		
		URI = username + "@" + sipServerIP; 
		maxConnect = max; 
		state=-1;
		frame = this;
		this.setSize(1280, 720); 
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocation(0, 0); 
		this.setTitle("SIP User Agent"); 
		
		
		this.setMenuBar(setMainMenuBar()); 
		
		/*playerPanel = new PlayerPanel(); 
		this.add(playerPanel); 
		*/
		this.setVisible(true); 
		
	}
	
	private void close(){
		this.setVisible(false); 
	}
	
	private void open(){
		this.setVisible(true); 
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("thread runing");
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
