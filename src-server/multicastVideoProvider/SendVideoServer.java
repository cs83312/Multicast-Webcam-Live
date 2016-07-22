package multicastVideoProvider;


public class SendVideoServer {

	@SuppressWarnings("null")
	public static void main(String args[]){
		
		MulticastTree obj = new MulticastTree();
	
		obj.preOrder(obj.getHead());
	}
	
}
