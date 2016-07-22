package registrar;

/*		*
 * 	File:						registrar/Main.java
 * 
 * 	Use:						The start point of registrar package. 
 * 								It is a SIP registrar server. 
 * 
 * 	Update Date: 	2016. 5. 21
 * */


public class Main {

	public static void main(String[] args) {
		(new Server(13577)).start(); 
	}
}

