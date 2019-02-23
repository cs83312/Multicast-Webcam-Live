package userAgentCore;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import multicastTree.MulticastNode;

public class JPanelToShowBinaryTree extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3441226962059910055L;
	//STEP1: 先設定root的位置
	//
	ArrayList<NodeTable> tree = new ArrayList<NodeTable>();
	
	public JPanelToShowBinaryTree(){
		JFrame jFrame = new JFrame();
        jFrame.add(this);
        jFrame.setSize(700, 700);
        jFrame.setVisible(true);
      
	}
	public JPanelToShowBinaryTree(String name){
		JFrame jFrame = new JFrame();
        jFrame.add(this);
        jFrame.setSize(700, 700);
        jFrame.setVisible(true);
        jFrame.setTitle(name);
	}
	public void insert(MulticastNode node,int lR){// -1=default 1=l 2=r
		
		if(node.getUdpPort()==-1){//is root
			tree.add(new NodeTable(node,300,100,false));
			
			if(node.childNode.size()<=2)
				insert(node.childNode.get(0),1);
			
			if(node.childNode.size()==2)
				insert(node.childNode.get(1),2);
			//draw
		}
		else
		{
			System.out.println("not root:"+node.getUdpPort());
			int parent = node.getParent().getTcpPort();//get width and height of parent
			for(int i=0;i<tree.size();i++)
			{
				if(tree.get(i).node.getTcpPort() == parent){
					if(lR == 1)//left height(base 50 +shift 20) width(base 25 +shift 10)
						tree.add(new NodeTable(node,tree.get(i).width-35,tree.get(i).height+70,false));
					else if(lR ==2)
						tree.add(new NodeTable(node,tree.get(i).width+35,tree.get(i).height+70,true));
					//draw
					
					break;
				}
			}
			if(node.childNode.size()==2||node.childNode.size()==1)
				insert(node.childNode.get(0),1);
			if(node.childNode.size()==2)
				insert(node.childNode.get(1),2);
			
		}
		
	}
	public void drawTree(Graphics2D g) {
		 for(int i=0;i<tree.size();i++)
		 {
	       NodeTable node= tree.get(i);
			// g.drawOval((int)node.width,(int) node.height, 20, 20);
	       g.setColor(Color.cyan);
	       if(node.node.getUdpPort()!=-1)
	       if(node.LR)//right leaf
	    	   g.drawLine((int)node.width-15,(int) node.height-35,(int)node.width+30,(int)node.height+30);
	       else 
	    	   g.drawLine((int)node.width+55, (int)node.height-35,(int)node.width+30,(int)node.height+30);
			
	       g.fillOval((int)node.width,(int) node.height,40, 40);
	       g.drawString("ADD: "+node.node.getAddress(), (int)node.width-25, (int) node.height-20);
	       g.drawString("TCP: "+String.valueOf(node.node.getTcpPort()), (int)node.width-5, (int) node.height-10);
		   g.drawString("UDP: "+String.valueOf(node.node.getUdpPort()), (int)node.width-5, (int) node.height);
		 
		 } 
	}
	 @Override
	    public void paintComponent(Graphics g) {
		 super.paintComponent(g);
		 
		 // Draw Tree Here
		 drawTree((Graphics2D)g);
		
	    }

	 
 public  class NodeTable{
		public MulticastNode node;
		public double width;
		public double height;
		public boolean LR; 
        public NodeTable(MulticastNode node,double width,double height,boolean LR)	{
        	this.node = node;
        	this.width = width;
        	this.height = height;
        	this.LR = LR;
        }
	}

}
