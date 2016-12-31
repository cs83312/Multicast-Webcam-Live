package useragent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/*		*
 * 	File:						useragent/FileSetting.java
 * 
 * 	Use:						To Process the setting file. 
 * 
 * 	Update Date: 	2016. 3. 6
 * */

public class FileSetting {
	
	private String fileName; 
	private String userName; 
	private String sendReceive;
	
	public String getSendReceive() {
		return sendReceive;
	}

	public void setSendReceive(String sendReceive) {
		this.sendReceive = sendReceive;
	}

	private int maxConnect; 
	
	public void setUserName(String user){
		userName = user; 
	}
	
	public void setMaxConnect(int max){
		maxConnect = max; 
	}
	
	public String getUserName(){
		return userName; 
	}
	
	public int getMaxConnect(){
		return maxConnect; 
	}
	
	
	// [In] 
	// [Out] Boolean: readFile success or not 
	// [Use] To read the file and store the 
	public boolean readFile() {
		// read the setting file
		try {
			FileReader file = new FileReader(fileName); 
			BufferedReader fr = new BufferedReader(file); 
			
			String str[] = new String[3]; 
			
			str[0] = fr.readLine(); 
			str[1] = fr.readLine(); 
			str[2] = fr.readLine(); 
			System.out.print("str[2]"+str[2]);
			if(str[2] == null && str[1] != null && str[0] != null) {
				userName = str[0]; 
				maxConnect = Integer.parseInt(str[1]);
				System.out.println(userName + '\n' + maxConnect ); 
				file.close();
				return true; 
			}	
			file.close(); 
			return false; 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false; 
		} catch (IOException e) {
			e.printStackTrace();
			return false; 
		} 	
	}

	
	@SuppressWarnings("resource")
	// [In] 
	// [Out] Boolean: writeFile success or not 
	// [Use] To write the variables into file
	public boolean writeFile(){
		// TODO: write into the setting file
		// Have some problem that cannot write into file
		try{
			FileWriter file = new FileWriter(fileName); 
			BufferedWriter fw = new BufferedWriter(file); 
			fw.write(userName);
			fw.newLine(); 
			fw.write(String.valueOf(maxConnect)); 
			fw.newLine();
			fw.close(); 
			file.close(); 
		} catch (IOException e) {
			e.printStackTrace();
			return false; 
		}
		
		return false; 
	}
	public boolean writeFileInSendRec(){
		
		try{
			FileWriter file = new FileWriter(fileName); 
			BufferedWriter fw = new BufferedWriter(file); 
			fw.write(userName);
			fw.newLine(); 
			fw.write(String.valueOf(maxConnect)); 
			fw.newLine();
			fw.write(sendReceive);
			
			fw.close(); 
			file.close(); 
		} catch (IOException e) {
			e.printStackTrace();
			return false; 
		}
		
		return false; 
	}
	public FileSetting(String name){
		fileName = name; 
		userName = ""; 
		maxConnect = 0; 
	}
	public FileSetting(String name, String user, int max){
		fileName = name; 
		userName = user; 
		maxConnect = max; 
		
	}
}
