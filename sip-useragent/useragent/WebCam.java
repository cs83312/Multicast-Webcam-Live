package useragent;

import java.awt.Dimension;
import java.awt.Image;

import javax.swing.JPanel;

import com.github.sarxos.webcam.*;

public class WebCam {
	
	private Dimension ds = new Dimension(640,480);
	private Dimension cs = WebcamResolution.VGA.getSize();
	private Webcam webC=Webcam.getDefault();;
	private WebcamPanel wCamPanel= new WebcamPanel(webC,ds,false);
	
	public WebCam(){
		
	webC.setViewSize(cs);
	wCamPanel.setFillArea(true);
	}

	public Image startWebCam(){
		if(webC.isOpen()==false)
		webC.open();
		return webC.getImage();
	}


}
