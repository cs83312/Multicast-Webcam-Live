package useragent;

/*		*
 * 	File:						useragent/FrameSetting.java
 * 
 * 	Use:						A window let user can look and change the properties.  
 * 
 * 	Update Date: 	2016. 6. 2
 * */

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class FrameSetting  extends JFrame implements ActionListener, Runnable {
	private String userName; 
	private int maxConnect; 
	public int state; 
	
	
	private JPanel panel; 
	
	private JTextField fieldUserName; 
	private JTextField fieldMaxConnect; 
	private JButton btnSubmit; 
	private JButton btnReset;
	private JButton btnExit;
	private JLabel labelInfo; 
	private JLabel labelUserName; 
	private JLabel labelMaxConnect; 
	
	private FileSetting fileSetting;  
	
	public FrameSetting() {
		
		fileSetting = new FileSetting("usersetting"); 
		
		panel = new JPanel(); 
		labelUserName = new JLabel(); 
		labelMaxConnect = new JLabel();
		labelInfo = new JLabel(); 
		fieldUserName = new JTextField(); 
		fieldMaxConnect = new JTextField(); 
		btnSubmit = new JButton(); 
		btnReset = new JButton(); 
		btnExit = new JButton(); 

		state = 0; 
		
		this.setSize(400, 300); 
		this.setDefaultCloseOperation(0); 
		this.setLocation(0, 0);
		this.setTitle("User Setting"); 
		

		
	}

	private boolean isDigit(String str){
		for (int i=0; i<str.length(); i++){
			if(!Character.isDigit(str.charAt(i)))
				return false; 
		}
		
		return true; 
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if ( e.getSource() == btnSubmit ){
			if(fieldUserName.getText().length() > 0){
				if(isDigit(fieldMaxConnect.getText())){
					
					fileSetting.setMaxConnect(Integer.parseInt(fieldMaxConnect.getText())); 
					fileSetting.setUserName(fieldUserName.getText()); 
					fileSetting.writeFile();
				
					
					state = 1; 
					this.setVisible(false);
				}
				else {
					labelInfo.setText("Max Connect 欄位僅可輸入數字。"); 					
				}
			}
			else{
				labelInfo.setText("請輸入一個字元以上的 User Name。"); 
			}
			
		}
		else if( e.getSource() == btnReset ){
			fieldUserName.setText(""); 
			fieldMaxConnect.setText("2"); 
		}
		else if( e.getSource() == btnExit ){
			this.dispose(); 
		}
		
	}

	@Override
	public void run() {
		panel.setLayout(new GridBagLayout()); 
		panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5)); 
		GridBagConstraints bags[] = new GridBagConstraints[8]; 
		
		
		labelUserName.setText("User Name: "); 
		labelMaxConnect.setText("Max Connection: "); 
		fieldMaxConnect.setText("2"); 
		btnSubmit.setText("Submit"); 
		btnReset.setText("Reset"); 
		btnExit.setText("Exit"); 
		
		bags[0] = new GridBagConstraints(0, 0, 3, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 1, 1); 
		bags[1] = new GridBagConstraints(0, 1, 3, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 1, 1);
		bags[2] = new GridBagConstraints(0, 2, 3, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 1, 1);
		bags[3] = new GridBagConstraints(0, 3, 3, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 1, 1);
		bags[4] = new GridBagConstraints(0, 4, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 1, 1);
		bags[5] = new GridBagConstraints(1, 4, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 1, 1);
		bags[7] = new GridBagConstraints(2, 4, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 1, 1);
		bags[6] = new GridBagConstraints(0, 5, 3, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 1, 1);
		
		btnSubmit.addActionListener(this);
		btnReset.addActionListener(this); 
		btnExit.addActionListener(this); 
		
		panel.add(labelUserName, bags[0]);
		panel.add(fieldUserName, bags[1]); 
		panel.add(labelMaxConnect, bags[2]); 
		panel.add(fieldMaxConnect, bags[3]); 
		panel.add(btnSubmit, bags[4]); 
		panel.add(btnReset, bags[5]);
		panel.add(labelInfo, bags[6]);
		panel.add(btnExit, bags[7]);
		
		this.add(panel); 
		
		this.setVisible(true); 
		
	}

}
