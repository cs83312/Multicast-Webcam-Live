package useragent;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class LiveCaptureReceiver extends JFrame implements ActionListener{

	Robot robot=null;
	Rectangle rec;
	ServerSocket serverSocket ;
	Socket socket=null;
	int count=0;
	static LiveCaptureReceiver frame;
	private JPanel contentPane;
	private Timer tM = new Timer(1,this);//1 EVERY MILLISECOND CALL ONCE,
	
	/**
	 * Create the frame.
	 */
	public LiveCaptureReceiver() {
		
		try {
			robot=new Robot();
			serverSocket = new ServerSocket(13085);
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 rec = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		 
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, (int)rec.getWidth(), (int)rec.getHeight());
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);	
	}
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new LiveCaptureReceiver();
					frame.addWindowListener(new WindowAdapter(){
						 public void windowClosing(WindowEvent e) {
							 System.exit(0);
						    }
					});
					
					frame.setVisible(true);
					frame.tM.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		update();
	}
	
	public void update(){
			
		try {
				System.out.println("set server"+count);
		        socket = serverSocket.accept();
		        DataInputStream din=new DataInputStream(socket.getInputStream());
                BufferedImage img=ImageIO.read(socket.getInputStream());
           
                Graphics2D g2d = (Graphics2D)frame.getGraphics();
                g2d.drawImage(img,0,0,this);
                  ImageIO.write(img,"jpg",new File(count+".jpg"));
                  count++;
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}         	
	}
}
