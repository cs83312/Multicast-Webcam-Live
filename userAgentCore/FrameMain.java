package userAgentCore;

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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.net.InetSocketAddress;

import javax.imageio.ImageIO;
import javax.sdp.SdpException;
import javax.swing.*;

import multicastTree.MulticastNode;
import multicastTree.MulticastTree;
import signalPacket.PacketMonitor;
import somethingtest.canUsePort;
import threadMethod.ClientMonitor;
import useragent.ConfigureNetwork;
import useragent.FrameSetting;
import useragent.PlayerPanel;
import useragent.WebCam;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;



public class FrameMain extends JFrame implements ActionListener, Runnable{
	
	private PlayerPanel playerPanel; 
	
	private String URI; 
	private String passWord; 
	private String parentURI; 
	private FrameMain frame;
	private SIPP2P sipP2P;
	public int state;
	private int SIPTcpPort;
	private int RTPPort;
	private InetSocketAddress TalktoRoot =null;
	private InetSocketAddress TalkfromClient=null;
	private InetSocketAddress TransmissionfromClientData =null;
	private boolean portIsSet;
	 RTPConnecter rtpRecv = null;
	MulticastTree tree;
	
	RTPConnecter RTPServer =null;
	private JTextField clientTcpPort;
	private JTextField clientUdpPort;
	private JTextField userName;
	private JTextField FromField;
	private JTextField ToField;
	public JTextArea transStateField;
	private JTextField FromIPAddr;
	private JTextField ToIPAddr;
	
	PacketMonitor monitor;
	private JTextField ChildNode;
	private JTextField balanceURI;
	private JTextField balanceTargetURI;
	
public FrameMain(String username,int passWord){	
		
		//this.passWord = passWord;
		frame = this;
		this.setSize(1280, 720); 
		this.setLocation(0, 0); 
		this.setTitle("SIP User Agent"); 
		this.setMenuBar(setMainMenuBar());
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 126, 74, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{14, 29, 0, 0, 0, 0, 0, 0, 26, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		
		JLabel lblNewLabel_1 = new JLabel("New label");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.gridheight = 17;
		gbc_lblNewLabel_1.gridwidth = 16;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 0;
		getContentPane().add(lblNewLabel_1, gbc_lblNewLabel_1);
		
		JLabel lblNewLabel = new JLabel("Local Ip and port");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(5, 5, 5, 5);
		gbc_lblNewLabel.fill = GridBagConstraints.BOTH;
		gbc_lblNewLabel.gridx = 18;
		gbc_lblNewLabel.gridy = 0;
		getContentPane().add(lblNewLabel, gbc_lblNewLabel);
		
		 final JLabel transState = new JLabel("TransmitState");
		transState.setEnabled(false);
		GridBagConstraints gbc_transState = new GridBagConstraints();
		gbc_transState.weightx = 0.1;
		gbc_transState.insets = new Insets(0, 0, 5, 5);
		gbc_transState.gridx = 19;
		gbc_transState.gridy = 0;
		getContentPane().add(transState, gbc_transState);
		
		JLabel lblNewLabel_3 = new JLabel("usename");
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_3.gridx = 17;
		gbc_lblNewLabel_3.gridy = 1;
		getContentPane().add(lblNewLabel_3, gbc_lblNewLabel_3);
		
		userName = new JTextField();
		userName.setEditable(false);
		userName.setText(username);
		GridBagConstraints gbc_userName = new GridBagConstraints();
		gbc_userName.fill = GridBagConstraints.HORIZONTAL;
		gbc_userName.insets = new Insets(0, 0, 5, 5);
		gbc_userName.gridx = 18;
		gbc_userName.gridy = 1;
		getContentPane().add(userName, gbc_userName);
		userName.setColumns(10);
		
		transStateField = new JTextArea(15,40);
		JScrollPane scrollPane = new JScrollPane(transStateField,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	    transStateField.setLineWrap(true);
	    transStateField.setWrapStyleWord(true);
		GridBagConstraints gbc_scrollBar = new GridBagConstraints();
		gbc_scrollBar.gridwidth = 10;
		gbc_scrollBar.gridheight = 16;
		gbc_scrollBar.insets = new Insets(0, 0, 5, 0);
		gbc_scrollBar.gridx = 19;
		gbc_scrollBar.gridy = 1;
		getContentPane().add(scrollPane, gbc_scrollBar);
		
		JLabel lblNewLabel_2 = new JLabel("FromIP");
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 17;
		gbc_lblNewLabel_2.gridy = 2;
		getContentPane().add(lblNewLabel_2, gbc_lblNewLabel_2);
		
		FromIPAddr = new JTextField();
		FromIPAddr.setText("134.208.3.13");
		GridBagConstraints gbc_FromIPAddr = new GridBagConstraints();
		gbc_FromIPAddr.insets = new Insets(0, 0, 5, 5);
		gbc_FromIPAddr.fill = GridBagConstraints.HORIZONTAL;
		gbc_FromIPAddr.gridx = 18;
		gbc_FromIPAddr.gridy = 2;
		getContentPane().add(FromIPAddr, gbc_FromIPAddr);
		FromIPAddr.setColumns(10);
		
		JLabel lblNewLabel_4 = new JLabel("ToIP");
		GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
		gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_4.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_4.gridx = 17;
		gbc_lblNewLabel_4.gridy = 3;
		getContentPane().add(lblNewLabel_4, gbc_lblNewLabel_4);
		
		ToIPAddr = new JTextField();
		ToIPAddr.setText("134.208.3.13");
		GridBagConstraints gbc_ToIPAddr = new GridBagConstraints();
		gbc_ToIPAddr.insets = new Insets(0, 0, 5, 5);
		gbc_ToIPAddr.fill = GridBagConstraints.HORIZONTAL;
		gbc_ToIPAddr.gridx = 18;
		gbc_ToIPAddr.gridy = 3;
		getContentPane().add(ToIPAddr, gbc_ToIPAddr);
		ToIPAddr.setColumns(10);
		
		JLabel lblNewLabel_24 = new JLabel("client TCP port");
		lblNewLabel_24.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblNewLabel_24 = new GridBagConstraints();
		gbc_lblNewLabel_24.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_24.fill = GridBagConstraints.BOTH;
		gbc_lblNewLabel_24.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_24.gridx = 17;
		gbc_lblNewLabel_24.gridy = 4;
		getContentPane().add(lblNewLabel_24, gbc_lblNewLabel_24);
		
		clientTcpPort = new JTextField();
		clientTcpPort.setEditable(false);
		GridBagConstraints gbc_clientTcpPort = new GridBagConstraints();
		gbc_clientTcpPort.fill = GridBagConstraints.HORIZONTAL;
		gbc_clientTcpPort.insets = new Insets(0, 0, 5, 5);
		gbc_clientTcpPort.gridx = 18;
		gbc_clientTcpPort.gridy = 4;
		getContentPane().add(clientTcpPort, gbc_clientTcpPort);
		clientTcpPort.setColumns(10);
		
		JLabel lblNewLabel_25 = new JLabel("client udp port");
		lblNewLabel_25.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblNewLabel_25 = new GridBagConstraints();
		gbc_lblNewLabel_25.fill = GridBagConstraints.BOTH;
		gbc_lblNewLabel_25.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_25.gridx = 17;
		gbc_lblNewLabel_25.gridy = 5;
		getContentPane().add(lblNewLabel_25, gbc_lblNewLabel_25);
		
		clientUdpPort = new JTextField();
		clientUdpPort.setEditable(false);
		GridBagConstraints gbc_clientUdpPort = new GridBagConstraints();
		gbc_clientUdpPort.fill = GridBagConstraints.HORIZONTAL;
		gbc_clientUdpPort.insets = new Insets(0, 0, 5, 5);
		gbc_clientUdpPort.gridx = 18;
		gbc_clientUdpPort.gridy = 5;
		getContentPane().add(clientUdpPort, gbc_clientUdpPort);
		clientUdpPort.setColumns(10);
		
		JLabel From = new JLabel("From");
		From.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_From = new GridBagConstraints();
		gbc_From.insets = new Insets(0, 0, 5, 5);
		gbc_From.gridx = 17;
		gbc_From.gridy = 6;
		getContentPane().add(From, gbc_From);
		
		FromField = new JTextField();
		FromField.setEditable(false);
		FromField.setColumns(10);
		GridBagConstraints gbc_FromField = new GridBagConstraints();
		gbc_FromField.fill = GridBagConstraints.HORIZONTAL;
		gbc_FromField.insets = new Insets(0, 0, 5, 5);
		gbc_FromField.gridx = 18;
		gbc_FromField.gridy = 6;
		getContentPane().add(FromField, gbc_FromField);
		
		JLabel lblNewLabel_20 = new JLabel("");
		GridBagConstraints gbc_lblNewLabel_20 = new GridBagConstraints();
		gbc_lblNewLabel_20.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_20.gridx = 16;
		gbc_lblNewLabel_20.gridy = 7;
		getContentPane().add(lblNewLabel_20, gbc_lblNewLabel_20);
		
		JLabel To = new JLabel("To");
		To.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_To = new GridBagConstraints();
		gbc_To.insets = new Insets(0, 0, 5, 5);
		gbc_To.gridx = 17;
		gbc_To.gridy = 8;
		getContentPane().add(To, gbc_To);
		
		ToField = new JTextField();
		ToField.setText("sip:Alice@127.0.0.1:5062");
		ToField.setColumns(10);
		GridBagConstraints gbc_ToField = new GridBagConstraints();
		gbc_ToField.insets = new Insets(0, 0, 5, 5);
		gbc_ToField.fill = GridBagConstraints.HORIZONTAL;
		gbc_ToField.gridx = 18;
		gbc_ToField.gridy = 8;
		getContentPane().add(ToField, gbc_ToField);
		
		JButton btnReigster = new JButton("reigster");
		btnReigster.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
					//set From,To and FromRTPPort
				//	setConfigure();
					//-----------------------------------------------------
			
					  //cycle_regist
					Thread regThread = new Thread(){
						@Override
						public void run(){
							while(true){
								try {
									//startSIP.register(TalktoRoot,TalkfromClient,TransmissionfromClientData);
									Thread.sleep(20000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								}
						}				
					};
					regThread.start();
					
				  
					
					
				
			}
		});
		btnReigster.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		
		JButton btnRefer = new JButton("configure_netowrk");
		btnRefer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ConfigureNetwork networkPanel = new ConfigureNetwork();
				SIPTcpPort = Integer.valueOf(networkPanel.sipTcpPort());
				RTPPort = Integer.valueOf(networkPanel.rtpudpport());
			}
		});
		GridBagConstraints gbc_btnRefer = new GridBagConstraints();
		gbc_btnRefer.insets = new Insets(0, 0, 5, 5);
		gbc_btnRefer.gridx = 18;
		gbc_btnRefer.gridy = 10;
		getContentPane().add(btnRefer, gbc_btnRefer);
		GridBagConstraints gbc_btnReigster = new GridBagConstraints();
		gbc_btnReigster.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnReigster.insets = new Insets(0, 0, 5, 5);
		gbc_btnReigster.gridx = 18;
		gbc_btnReigster.gridy = 11;
		getContentPane().add(btnReigster, gbc_btnReigster);
		
		JButton btnInvite = new JButton("invite");
		btnInvite.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					canUsePort use = new canUsePort();
					//set From,To and FromRTPPort
					if(!portIsSet){
					
					TalkfromClient = new InetSocketAddress(FromIPAddr.getText(),use.searchCanUsePort());
					TransmissionfromClientData = new InetSocketAddress(FromIPAddr.getText(),use.searchCanUsePort()+1);
					
					clientTcpPort.setText(String.valueOf(TalkfromClient.getPort()));
					clientUdpPort.setText(String.valueOf(TransmissionfromClientData.getPort()));
					FromField.setText("SIP:"+userName.getText()+"@"+TalkfromClient.getHostString()+":"+clientTcpPort.getText());
					
				//	startSIP = new SIPComUseJain(FromIPAddr.getText(),TalkfromClient.getPort(),userName.getText(),transStateField);
					portIsSet=true;
					}
					TalktoRoot = new InetSocketAddress(ToIPAddr.getText(),5060);
					//-----------------------------------------------------
					
					//create sip
					
					//invite
					//startSIP.invite(ToField.getText(),TalkfromClient,TransmissionfromClientData);
				   // startSIP.setNode(new MulticastNode("",FromIPAddr.getText(),TalkfromClient.getPort(),TransmissionfromClientData.getPort()));
				
				} catch (IOException  e1) {e1.printStackTrace();}
			}
			
		});
		
		GridBagConstraints gbc_btnInvite = new GridBagConstraints();
		gbc_btnInvite.fill = GridBagConstraints.BOTH;
		gbc_btnInvite.insets = new Insets(0, 0, 5, 5);
		gbc_btnInvite.gridx = 17;
		gbc_btnInvite.gridy = 12;
		getContentPane().add(btnInvite, gbc_btnInvite);
		
		JButton btnCreateprovider = new JButton("createProvider");
		GridBagConstraints gbc_btnCreateprovider = new GridBagConstraints();
		gbc_btnCreateprovider.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnCreateprovider.insets = new Insets(0, 0, 5, 5);
		gbc_btnCreateprovider.gridx = 18;
		gbc_btnCreateprovider.gridy = 12;
		getContentPane().add(btnCreateprovider, gbc_btnCreateprovider);
		
		JButton btnLeave = new JButton("Leave");
		btnLeave.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				
				
				
			}
		});
		
		JButton btnPausingStream = new JButton("Pausing Stream");
		btnPausingStream.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		
				btnPausingStream.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						System.out.println("Pausing Stream");
						if(sipP2P!=null){					
							sipP2P.PauseInvite();			
					}
					}
				});
				GridBagConstraints gbc_btnPausingStream = new GridBagConstraints();
				gbc_btnPausingStream.insets = new Insets(0, 0, 5, 5);
				gbc_btnPausingStream.gridx = 17;
				gbc_btnPausingStream.gridy = 13;
				getContentPane().add(btnPausingStream, gbc_btnPausingStream);
		GridBagConstraints gbc_btnLeave = new GridBagConstraints();
		gbc_btnLeave.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnLeave.insets = new Insets(0, 0, 5, 5);
		gbc_btnLeave.gridx = 18;
		gbc_btnLeave.gridy = 13;
		getContentPane().add(btnLeave, gbc_btnLeave);
		
		JButton btnConnecttoprovider = new JButton("RootSetBalance");
		btnConnecttoprovider.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				System.out.println("FrameMain: balanceTree");
				if(sipP2P!=null){	
					if(!balanceURI.getText().isEmpty() &&!balanceTargetURI.getText().isEmpty()){
						String uri = balanceURI.getText();
						String specAddr = uri.split("@")[1].split(":")[0];
						int specPort = Integer.valueOf(uri.split("@")[1].split(":")[1]);
						System.out.println(specAddr+"and "+specPort);
						sipP2P.balanceRefer(specAddr, specPort,balanceTargetURI.getText());
						
					}
							
			}
				
			}
		});
		GridBagConstraints gbc_btnConnecttoprovider = new GridBagConstraints();
		gbc_btnConnecttoprovider.fill = GridBagConstraints.VERTICAL;
		gbc_btnConnecttoprovider.gridheight = 2;
		gbc_btnConnecttoprovider.insets = new Insets(0, 0, 5, 5);
		gbc_btnConnecttoprovider.gridx = 17;
		gbc_btnConnecttoprovider.gridy = 14;
		getContentPane().add(btnConnecttoprovider, gbc_btnConnecttoprovider);
		
		balanceURI = new JTextField();
		balanceURI.setText("sip:a@134.208.3.13:port");
		GridBagConstraints gbc_balanceURI = new GridBagConstraints();
		gbc_balanceURI.insets = new Insets(0, 0, 5, 5);
		gbc_balanceURI.fill = GridBagConstraints.HORIZONTAL;
		gbc_balanceURI.gridx = 18;
		gbc_balanceURI.gridy = 14;
		getContentPane().add(balanceURI, gbc_balanceURI);
		balanceURI.setColumns(10);
		
		balanceTargetURI = new JTextField();
		balanceTargetURI.setText("sip:b@134.208.3.13:port");
		GridBagConstraints gbc_balanceTargetURI = new GridBagConstraints();
		gbc_balanceTargetURI.insets = new Insets(0, 0, 5, 5);
		gbc_balanceTargetURI.fill = GridBagConstraints.HORIZONTAL;
		gbc_balanceTargetURI.gridx = 18;
		gbc_balanceTargetURI.gridy = 15;
		getContentPane().add(balanceTargetURI, gbc_balanceTargetURI);
		balanceTargetURI.setColumns(10);
		
		ChildNode = new JTextField();
		ChildNode.setText("child node");
		GridBagConstraints gbc_ChildNode = new GridBagConstraints();
		gbc_ChildNode.gridheight = 5;
		gbc_ChildNode.gridwidth = 6;
		gbc_ChildNode.insets = new Insets(0, 0, 5, 5);
		gbc_ChildNode.fill = GridBagConstraints.BOTH;
		gbc_ChildNode.gridx = 20;
		gbc_ChildNode.gridy = 17;
		getContentPane().add(ChildNode, gbc_ChildNode);
		ChildNode.setColumns(10);
		
		JLabel lblNewLabel_5 = new JLabel("upload");
		GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
		gbc_lblNewLabel_5.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_5.gridx = 17;
		gbc_lblNewLabel_5.gridy = 23;
		getContentPane().add(lblNewLabel_5, gbc_lblNewLabel_5);
		
		JLabel lblNewLabel_6 = new JLabel("download");
		GridBagConstraints gbc_lblNewLabel_6 = new GridBagConstraints();
		gbc_lblNewLabel_6.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel_6.gridx = 17;
		gbc_lblNewLabel_6.gridy = 24;
		getContentPane().add(lblNewLabel_6, gbc_lblNewLabel_6);
		
		/*playerPanel = new PlayerPanel(); 
		this.add(playerPanel); 
		*/
		this.setVisible(true); 
		this.addWindowListener(new WindowAdapter() 
		{
		    public void windowClosing(WindowEvent event) {System.out.println("exit");System.exit(0);}
		});
		
	}
	
	
	
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
					
				

					setConfigure(true);
					//set tree
					 tree =  new MulticastTree("127.0.0.1",5062);
					 //憒��酉���(statefull)
					 if(sipP2P!=null){
						 sipP2P.isServer = true;
						 
					 }
					 else
						 sipP2P = new SIPP2P(FromIPAddr.getText(),5062,userName.getText(),transStateField,true);
					 
					 sipP2P.setTree(tree);
					final Graphics2D g2d = (Graphics2D)frame.getGraphics();
					final WebCam cam = new WebCam();

					
					final Thread t = new Thread(){
						@Override
						public void run(){
							RTPServer = new RTPConnecter();
							
							RTPServer.setTree(tree.root);
							
							sipP2P.setRtpTrans(RTPServer);
							
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
									RTPServer.RTPSend(buffer);
									Thread.sleep(20);
									
								} catch (IOException | InterruptedException e) {
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
					
			
									
					monitor = new PacketMonitor();
					canUsePort use = new canUsePort();
					 
					 
					try {
						
						//set From,To and FromRTPPort
						setConfigure(false);
						
						sipP2P = new SIPP2P(FromIPAddr.getText(),TalkfromClient.getPort(),userName.getText(),transStateField);
						System.out.println(TalkfromClient.getPort());
						
						 /*
						   * create  rtp 
						   */
							rtpRecv =new RTPConnecter(FromIPAddr.getText(),TransmissionfromClientData.getPort());
							sipP2P.setRtpTrans(rtpRecv);
						
						//Monitor
						ClientMonitor clientMT = new ClientMonitor(sipP2P,ChildNode);
						clientMT.start();
						sipP2P.setMonitor(clientMT);//invite end to end delay
						rtpRecv.setMonitor(clientMT);//packet lost
						
						//invite
						sipP2P.invite(ToField.getText(),TalkfromClient,TransmissionfromClientData);

					 
						
					
					} catch (SdpException e1) {e1.printStackTrace();}
					
					final RTPConnecter clientRTP = rtpRecv;
					
				
					final Graphics2D g2d = (Graphics2D)frame.getGraphics();
					Thread recv = new Thread(){
						
						@Override
						public void run(){
																	
							while(true){
								
								try {
									g2d.drawImage(clientRTP.RTPreceive(),0,0,null);								
									Thread.sleep(20);
								} catch (InterruptedException | IOException e) {e.printStackTrace();}
							
								
							
								
									
							}
							
						}
						
					};
					recv.start();
				
				}
			}
			
		);
		menuStream.add(menuItemOpen); 
		
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
	
		// Other
		Menu menuOther = new Menu(); 
		menuOther.setLabel("Other"); 
		menuBar.add(menuOther); 	
		
  		// Other > Help - To display the help pages.
		MenuItem menuItemHelp = new MenuItem("Help"); 
		menuItemHelp.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e){
					// TODO: ����頛魂�蕭嚙踐��謘橘蕭嚙�
				}
			}
		);			
		menuOther.add(menuItemHelp); 				
	
		menuOther.addSeparator(); 
		
  		// Other > Configuration - To display the configuration page.
		MenuItem menuItemConfig = new MenuItem("multicast tree"); 
		menuItemConfig.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e){
					
					FrameSetting frameSetting =  new FrameSetting(); 
					JPanelToShowBinaryTree tree = new JPanelToShowBinaryTree();
					
					FrameMain.this.tree.showNode(FrameMain.this.tree.root);
					
					tree.insert(FrameMain.this.tree.root,-1);
					
				//	RTPServer.multiLine.showNode(RTPServer.multiLine.root);
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

	private void setConfigure(boolean isServer){
		
		canUsePort use = new canUsePort();
		
		//set From,To and FromRTPPort
		if(!portIsSet){
			
		
		try {
			if(isServer)
				TalkfromClient = new InetSocketAddress(FromIPAddr.getText(),5062);
			else
			TalkfromClient = new InetSocketAddress(FromIPAddr.getText(),use.searchCanUsePort());
			
			TransmissionfromClientData = new InetSocketAddress(FromIPAddr.getText(),use.searchCanUsePort()+1);
			portIsSet=true;
			
			TalktoRoot = new InetSocketAddress(ToIPAddr.getText(),5062);
			clientTcpPort.setText(String.valueOf(TalkfromClient.getPort()));
			clientUdpPort.setText(String.valueOf(TransmissionfromClientData.getPort()));
			FromField.setText("sip:"+userName.getText()+"@"+TalkfromClient.getHostString()+":"+clientTcpPort.getText());
			ToField.setText("sip:Alice@"+TalktoRoot.getHostString()+":"+TalktoRoot.getPort());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		
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
		//System.out.println("thread runing");
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
