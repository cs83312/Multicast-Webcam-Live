package multichat;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;

import javax.swing.BoundedRangeModel;
import javax.swing.JButton;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.awt.event.ActionEvent;
import javax.swing.JScrollBar;
import java.awt.event.AdjustmentListener;
import java.awt.event.AdjustmentEvent;
import javax.swing.JEditorPane;
import java.awt.Color;

public class client extends JFrame {

	private JPanel contentPane;
	private JTextField name;
	private JTextField ipaddress;
	private Socket client;
	private JScrollBar scrollBar;
	private JEditorPane content;
	private JEditorPane contenttext;
	private JEditorPane statelist;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					client frame = new client();
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
	public client() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 471, 318);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, 1.0, 1.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JLabel lblNewLabel = new JLabel("   \u66B1\u7A31");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.fill = GridBagConstraints.BOTH;
		gbc_lblNewLabel.gridwidth = 4;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		contentPane.add(lblNewLabel, gbc_lblNewLabel);
		
		name = new JTextField();
		name.setText("123123");
		GridBagConstraints gbc_name = new GridBagConstraints();
		gbc_name.gridwidth = 9;
		gbc_name.insets = new Insets(0, 0, 5, 5);
		gbc_name.fill = GridBagConstraints.HORIZONTAL;
		gbc_name.gridx = 4;
		gbc_name.gridy = 0;
		contentPane.add(name, gbc_name);
		name.setColumns(10);
		
		JButton btnNewButton_1 = new JButton("connect");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
			
				try {
					client = new Socket(ipaddress.getText().toString(),8800);
					statelist.setText(statelist.getText()+"sucess to server.."+"\n");
					ClientThread t = new ClientThread(client);
					t.start();
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
				
				
				
			
		});
		GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
		gbc_btnNewButton_1.fill = GridBagConstraints.BOTH;
		gbc_btnNewButton_1.gridheight = 2;
		gbc_btnNewButton_1.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton_1.gridx = 13;
		gbc_btnNewButton_1.gridy = 0;
		contentPane.add(btnNewButton_1, gbc_btnNewButton_1);
		
		statelist = new JEditorPane();
		statelist.setBackground(Color.LIGHT_GRAY);
		statelist.setForeground(Color.BLACK);
		statelist.setEditable(false);
		GridBagConstraints gbc_statelist = new GridBagConstraints();
		gbc_statelist.gridheight = 2;
		gbc_statelist.gridwidth = 5;
		gbc_statelist.insets = new Insets(0, 0, 5, 5);
		gbc_statelist.fill = GridBagConstraints.BOTH;
		gbc_statelist.gridx = 14;
		gbc_statelist.gridy = 0;
		contentPane.add(statelist, gbc_statelist);
		
		JLabel lblNewLabel_1 = new JLabel("server_ip");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.gridwidth = 4;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 1;
		contentPane.add(lblNewLabel_1, gbc_lblNewLabel_1);
		
		ipaddress = new JTextField();
		ipaddress.setText("134.208.3.13");
		GridBagConstraints gbc_ipaddress = new GridBagConstraints();
		gbc_ipaddress.gridwidth = 9;
		gbc_ipaddress.insets = new Insets(0, 0, 5, 5);
		gbc_ipaddress.fill = GridBagConstraints.HORIZONTAL;
		gbc_ipaddress.gridx = 4;
		gbc_ipaddress.gridy = 1;
		contentPane.add(ipaddress, gbc_ipaddress);
		ipaddress.setColumns(10);
		
		JButton btnNewButton = new JButton("send");
		btnNewButton.addActionListener(new ActionListener() {
			
	
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent arg0) {
				Date i = new Date();
				String date = String.valueOf(i.getHours())+":"+String.valueOf(i.getMinutes())+":"+String.valueOf(i.getSeconds());
				StringBuilder temp = new StringBuilder();
				temp.append(name.getText().toString()+" ");
				temp.append(date+" ");
				temp.append(content.getText().toString()+"\n");
				contenttext.setText(contenttext.getText().toString()+temp);
				
				try {
					ObjectOutputStream toServer =new ObjectOutputStream(client.getOutputStream());
					PacketBuffer std = new PacketBuffer(name.getText().toString(), date,content.getText().toString());
					toServer.writeObject(std);
					toServer.flush();
					
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		
		scrollBar = new JScrollBar();
		scrollBar.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				repaint();

	      
			}
		});
		
		contenttext = new JEditorPane();
		contenttext.setEditable(false);
		GridBagConstraints gbc_contenttext = new GridBagConstraints();
		gbc_contenttext.gridwidth = 19;
		gbc_contenttext.gridheight = 5;
		gbc_contenttext.insets = new Insets(0, 0, 5, 5);
		gbc_contenttext.fill = GridBagConstraints.BOTH;
		gbc_contenttext.gridx = 0;
		gbc_contenttext.gridy = 2;
		contentPane.add(contenttext, gbc_contenttext);
		GridBagConstraints gbc_scrollBar = new GridBagConstraints();
		gbc_scrollBar.anchor = GridBagConstraints.EAST;
		gbc_scrollBar.fill = GridBagConstraints.BOTH;
		gbc_scrollBar.gridheight = 5;
		gbc_scrollBar.insets = new Insets(0, 0, 5, 0);
		gbc_scrollBar.gridx = 19;
		gbc_scrollBar.gridy = 2;
		contentPane.add(scrollBar, gbc_scrollBar);
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.gridheight = 2;
		gbc_btnNewButton.gridwidth = 7;
		gbc_btnNewButton.fill = GridBagConstraints.BOTH;
		gbc_btnNewButton.insets = new Insets(0, 0, 0, 5);
		gbc_btnNewButton.gridx = 0;
		gbc_btnNewButton.gridy = 7;
		contentPane.add(btnNewButton, gbc_btnNewButton);
		
		content = new JEditorPane();
		GridBagConstraints gbc_content = new GridBagConstraints();
		gbc_content.gridheight = 2;
		gbc_content.gridwidth = 12;
		gbc_content.insets = new Insets(0, 0, 0, 5);
		gbc_content.fill = GridBagConstraints.BOTH;
		gbc_content.gridx = 7;
		gbc_content.gridy = 7;
		contentPane.add(content, gbc_content);
	}
	
	 class ClientThread extends Thread{  
		Socket soc;
		InputStreamReader isReader;
		public ClientThread(Socket soc)
		{
		this.soc = soc;	
		}
		
		public void run(){
				 while(true)
				  {
							try {
								ObjectInputStream inputFromClient =new ObjectInputStream(this.soc.getInputStream());
								PacketBuffer object = (PacketBuffer)inputFromClient.readObject();
								contenttext.setText(contenttext.getText()+object.getName()+" "+object.getDate()+":"+object.getContent()+"\n");
								Thread.sleep(100);
							} 
							 catch (ClassNotFoundException e) {
									e.printStackTrace();
								}		
							 catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							  catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
				  }
				 
				
			
		
		 	}
		 
	 
	 }

}
