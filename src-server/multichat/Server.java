package multichat;

import java.awt.BorderLayout;

import multicastVideoProvider.*;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.GridBagLayout;

import javax.swing.JButton;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JTextField;
import javax.swing.JLabel;

import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.awt.event.ActionEvent;

import javax.swing.JEditorPane;

import java.awt.Color;

public class Server extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private static ArrayList<Socket> ipBuf = new  ArrayList();
	private JButton stopServer;
	private ServerSocket serverSock;
	private ServerThread serverT;
	private JEditorPane contenttext;
	private JEditorPane inserttable;
	private JEditorPane iplist;
	private MulticastTree  multicastTree;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Server frame = new Server();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Server() {
		setTitle("Server");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0, 0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, 1.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JLabel lblNewLabel = new JLabel("boardcast");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		contentPane.add(lblNewLabel, gbc_lblNewLabel);
		
		JLabel lblIplist = new JLabel("ip_list");
		GridBagConstraints gbc_lblIplist = new GridBagConstraints();
		gbc_lblIplist.insets = new Insets(0, 0, 5, 0);
		gbc_lblIplist.gridx = 4;
		gbc_lblIplist.gridy = 0;
		contentPane.add(lblIplist, gbc_lblIplist);
		
		contenttext = new JEditorPane();
		contenttext.setEditable(false);
		GridBagConstraints gbc_contenttext = new GridBagConstraints();
		gbc_contenttext.gridwidth = 4;
		gbc_contenttext.gridheight = 6;
		gbc_contenttext.insets = new Insets(0, 0, 5, 5);
		gbc_contenttext.fill = GridBagConstraints.BOTH;
		gbc_contenttext.gridx = 0;
		gbc_contenttext.gridy = 1;
		contentPane.add(contenttext, gbc_contenttext);
		
		JButton btnNewButton = new JButton("send");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Date i = new Date();
				
				String date = String.valueOf(i.getHours())+":"+String.valueOf(i.getMinutes())+":"+String.valueOf(i.getSeconds());
				PacketBuffer pack = new PacketBuffer("Server",date,inserttable.getText().toString());
				contenttext.setText(contenttext.getText()+put(pack));
				boardCast(pack,null);
				
			}
		});
		
		stopServer = new JButton("stopserver(un-made)");
		stopServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				
			}
		});
		
		iplist = new JEditorPane();
		iplist.setBackground(Color.LIGHT_GRAY);
		iplist.setEditable(false);
		GridBagConstraints gbc_iplist = new GridBagConstraints();
		gbc_iplist.gridheight = 6;
		gbc_iplist.insets = new Insets(0, 0, 5, 0);
		gbc_iplist.fill = GridBagConstraints.BOTH;
		gbc_iplist.gridx = 4;
		gbc_iplist.gridy = 1;
		contentPane.add(iplist, gbc_iplist);
		GridBagConstraints gbc_stopServer = new GridBagConstraints();
		gbc_stopServer.fill = GridBagConstraints.BOTH;
		gbc_stopServer.insets = new Insets(0, 0, 5, 5);
		gbc_stopServer.gridx = 0;
		gbc_stopServer.gridy = 7;
		contentPane.add(stopServer, gbc_stopServer);
		
		inserttable = new JEditorPane();
		GridBagConstraints gbc_inserttable = new GridBagConstraints();
		gbc_inserttable.gridheight = 2;
		gbc_inserttable.gridwidth = 4;
		gbc_inserttable.fill = GridBagConstraints.BOTH;
		gbc_inserttable.gridx = 1;
		gbc_inserttable.gridy = 7;
		contentPane.add(inserttable, gbc_inserttable);
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.fill = GridBagConstraints.BOTH;
		gbc_btnNewButton.insets = new Insets(0, 0, 0, 5);
		gbc_btnNewButton.gridx = 0;
		gbc_btnNewButton.gridy = 8;
		contentPane.add(btnNewButton, gbc_btnNewButton);
		multicastTree = new MulticastTree();
	serverIni();
		
	}
	
	public void serverIni()
	{
		 
		try{
			if(serverSock==null)
				
			    serverSock = new ServerSocket(8800);  
			  serverT = new ServerThread(serverSock);
			    serverT.start(); 
			    
			  }catch(Exception ex){System.out.println("�s������");}
	}
	
	

 class ServerThread extends Thread{   
	
		  BufferedReader reader;  
		  ServerSocket sock;    
		 
		  public ServerThread(ServerSocket cSocket)
		  {
		   sock = cSocket;
		   
		  }
		 
		  public void run(){
				  while(true){
					   
				    Socket cSocket;
					try {
						cSocket = sock.accept();
						addIpList(cSocket);
						multicastTree.insertNode(cSocket.getInetAddress().toString(),cSocket.getPort());

                        Thread process = new Thread(new IpListProcessThread(cSocket));
                        process.start();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				   }
			   
			  }
		  
 		}
 		public StringBuilder put(PacketBuffer object)
 		{
 			StringBuilder temp = new StringBuilder();
			temp.append(object.getName()+" ");
			temp.append(object.getDate()+" ");
			temp.append(object.getContent()+"\n");
			return temp;
 		}
 		public void boardCast(PacketBuffer object,Socket sock)
 		{
 			
 		for(Socket soc:ipBuf)
 		{
 			if(sock==null||soc.getInetAddress()!=sock.getInetAddress() && soc.getPort()!=sock.getPort())
 			try {
				ObjectOutputStream toClient =new ObjectOutputStream(soc.getOutputStream());
				toClient.writeObject(object);
				toClient.flush();
				System.out.println(soc.getLocalSocketAddress().toString()+soc.getPort());
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
 		}	
 		}
 		
 		
 		class IpListProcessThread extends Thread{
 			
 		     private ObjectInputStream inputFromClient;
 		    Socket cSocket;

 			public IpListProcessThread(Socket cSoc)
 			
 			{	this.cSocket = cSoc;
 				
 			}
 		     
 			public void run()
 			 {
 			  while(true)
 			  {
 					try {
 					 
 					    inputFromClient =new ObjectInputStream(cSocket.getInputStream());
 						PacketBuffer object = (PacketBuffer)inputFromClient.readObject();
 						contenttext.setText(contenttext.getText()+put(object));
 						boardCast(object,cSocket);
 						Thread.sleep(100);
 					}catch (ClassNotFoundException e) {
 							// TODO Auto-generated catch block
 							System.out.println(e);
 						}
 					    
 					 catch (IOException e) {
 						// TODO Auto-generated catch block
 						 System.out.println(e+"?");
 						 ipBuf.remove(cSocket);
 						 iplist.setText("");

 						 addIpList(null);
 						 break;
 					    } 
 					catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					   }
 					    
 			  } 
 			 }
 			
 			
 		}
 		void addIpList(Socket cSocket)
		  {
			  String ip=" ";
			  if(cSocket!=null)
			  {
				  if(ipBuf.size()==0)
				  {
					  ip +=cSocket.getInetAddress().toString()+":"+cSocket.getPort()+"\n";
					  ipBuf.add(cSocket);
			    	iplist.setText(ip+"\n");
				  }
				  else  
				  {
					  ipBuf.add(cSocket);
					  for(int count=0;count<ipBuf.size();count++)
					    ip +=ipBuf.get(count).getInetAddress()+":"+ipBuf.get(count).getPort()+"\n";
			    		iplist.setText(ip+"\n");		  }}
			  else
			  {
				  for(int count=0;count<ipBuf.size();count++)
			    	ip +=ipBuf.get(count).getInetAddress()+":"+ipBuf.get(count).getPort()+"\n";
			    
			    		iplist.setText(ip+"\n");
			    }
			  
			  for(int count=0;count<ipBuf.size();count++)
				  System.out.println(ipBuf.get(count).getInetAddress().toString());
		  }
	
		  
 		

}
