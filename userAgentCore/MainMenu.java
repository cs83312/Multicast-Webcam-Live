package userAgentCore;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainMenu extends JFrame {

	private JPanel contentPane;
	private JTextField IDField;
	private JTextField PASSField;
	private JLabel password;
	private JButton LogIN;
	private final Action action = new SwingAction();



	/**
	 * Create the frame.
	 */
	public MainMenu() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 735, 560);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JLabel USERID = new JLabel("USERID");
		GridBagConstraints gbc_USERID = new GridBagConstraints();
		gbc_USERID.fill = GridBagConstraints.HORIZONTAL;
		gbc_USERID.insets = new Insets(0, 0, 5, 5);
		gbc_USERID.gridx = 6;
		gbc_USERID.gridy = 1;
		contentPane.add(USERID, gbc_USERID);
		
		IDField = new JTextField();
		GridBagConstraints gbc_IDField = new GridBagConstraints();
		gbc_IDField.insets = new Insets(0, 0, 5, 5);
		gbc_IDField.fill = GridBagConstraints.BOTH;
		gbc_IDField.gridx = 7;
		gbc_IDField.gridy = 1;
		contentPane.add(IDField, gbc_IDField);
		IDField.setColumns(10);
		
		password = new JLabel("password");
		GridBagConstraints gbc_password = new GridBagConstraints();
		gbc_password.fill = GridBagConstraints.HORIZONTAL;
		gbc_password.insets = new Insets(0, 0, 5, 5);
		gbc_password.gridx = 6;
		gbc_password.gridy = 3;
		contentPane.add(password, gbc_password);
		
		PASSField = new JTextField();
		PASSField.setColumns(10);
		GridBagConstraints gbc_PASSField = new GridBagConstraints();
		gbc_PASSField.insets = new Insets(0, 0, 5, 5);
		gbc_PASSField.fill = GridBagConstraints.HORIZONTAL;
		gbc_PASSField.gridx = 7;
		gbc_PASSField.gridy = 3;
		contentPane.add(PASSField, gbc_PASSField);
		
		LogIN = new JButton("LogIN");
		LogIN.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				LogIn();
				
			}
		});
		GridBagConstraints gbc_LogIN = new GridBagConstraints();
		gbc_LogIN.fill = GridBagConstraints.HORIZONTAL;
		gbc_LogIN.anchor = GridBagConstraints.BASELINE;
		gbc_LogIN.insets = new Insets(0, 0, 5, 5);
		gbc_LogIN.gridx = 7;
		gbc_LogIN.gridy = 5;
		contentPane.add(LogIN, gbc_LogIN);
		
	}
	
	void LogIn(){
		
		FrameMain ui = new FrameMain(IDField.getText(),PASSField.getText());
		Thread mainThread = new Thread(ui); 
		mainThread.start(); 
	}
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainMenu frame = new MainMenu();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		
	
		
		
		
		
	}
	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, "SwingAction");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
}
