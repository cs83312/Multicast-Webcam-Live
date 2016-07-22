package registrar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/*		*
 * 	File:						registrar/DataFile.java
 * 
 * 	Use:						To Process the URI info
 * 
 * 	Update Date: 	2016. 6. 2
 * */

public class DataFile {

		
		private String fileName; 
		
		private class Data{
		
			private String username; 
			private String address; 
			private int port; 
			private String sendReceive;
			
			public void setSendRecv(String sendRecv){
				sendReceive = sendRecv;
			}
			
			public void setUserName(String user){
				username = user; 
			}
			
			public void setINet(String addr, int prt){
				address = addr; 
				port = prt; 
			}
			
			public String getSendRecv(){
				return sendReceive;
			}
			public String getUserName(){
				return username; 
			}
			
			public String getAddress(){
				return address; 
			}
			
			public int getPort(){
				return port; 
			}
			
			public String getINet(){
				return address + ":" + String.valueOf(port); 
			}
			
			public Data(String user, String inet){
				String tmp[];
				tmp = inet.split(":"); 
				setUserName(user); 
				setINet(tmp[0], Integer.parseInt(tmp[1])); 
			}
			
			public Data(String user, String addr, int prt){
				setUserName(user); 
				setINet(addr, prt); 
			}
			public Data(String user, String addr, int prt,String sendRecv){
				setUserName(user); 
				setINet(addr, prt); 
				setSendRecv(sendRecv);
			}
		}
		
		private ArrayList<Data> users = new ArrayList<Data>(); 
		
		// [In] 
		// [Out] Boolean: readFile success or not 
		// [Use] To read the file and store the 
		public boolean readFile() {
			// read the setting file
			try {
				FileReader file = new FileReader(fileName); 
				BufferedReader fr = new BufferedReader(file); 
				
				String str;
				String strs[]; 
				
				while((str = fr.readLine())!=null){
					// process the data into users information
					strs = str.split(" "); 
					users.add(new Data(strs[0], strs[1])); 
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
				 
				for(int i=0; i<users.size(); i++){
					fw.write(users.get(i).getUserName()); 
					fw.write(" "); 
					fw.write(users.get(i).getINet()); 
					fw.newLine(); 
				}
				fw.close(); 
				file.close(); 
			} catch (IOException e) {
				e.printStackTrace();
				return false; 
			}
			
			return false; 
		}
		
		public String findUser(String acc){ 
			for(int i=0; i<users.size(); i++){
				if(acc.equals(users.get(i).getUserName())){
					return users.get(i).getINet(); 
				}
			}
			return null; 
		}
		
		public void addData(String user, String inet){
			 
			for(int i=0; i<users.size(); i++){
				if( user.equals( users.get(i).getUserName()) ){
					String tmp[] = inet.split(":"); 
					users.get(i).setINet(tmp[0], Integer.parseInt(tmp[1]));
					writeFile(); 
					return;  
				}
			}			
			users.add(new Data(user, inet));
			writeFile(); 
		}
		
		public DataFile(String file){
			fileName = file; 
			readFile(); 
		}
		
	}
