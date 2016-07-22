package multichat;

import java.io.Serializable;

public class PacketBuffer implements Serializable{

	private static final long serialVersionUID = 1L;
	private String name;
	private String date;
	private String content;
	public PacketBuffer(String n,String d,String content)
	{
		this.name = n;
		this.date = d;
		this.content = content;
	}
	public String getName() {
		return name;
	}
	public String getDate() {
		return date;
	}
	public String getContent() {
		return content;
	}
	

}
