package proxy;

/*		*
 * 	File:						proxy/Main.java
 * 
 * 	Use:						The start point of package proxy. 
 * 								It is a SIP Proxy Server. 
 * 
 * 	Update Date: 	2016. 5. 23
 * */

public class Main {

	public static void main(String[] args) {
		Proxy server = new Proxy(13575); 
		server.start(); 
	}
}
