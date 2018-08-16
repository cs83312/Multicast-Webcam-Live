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
	public void insert(MulticastNode node){
		
		if(node.getUdpPort()==-1){//is root
			tree.add(new NodeTable(node,300,100,false));
			
			if(node.childNode.size()<=2)
				insert(node.childNode.get(0));
			
			if(node.childNode.size()==2)
				insert(node.childNode.get(1));
			//draw
		}
		else
		{
			MulticastNode parent = node.getParent();//get width and height of parent
			for(int i=0;i<tree.size();i++)
			{
				if(tree.get(i).node == parent){
					if(tree.get(i).node.childNode.get(0) == node)//left 
						tree.add(new NodeTable(node,tree.get(i).width-25,tree.get(i).height+50,false));
					else
						tree.add(new NodeTable(node,tree.get(i).width+25,tree.get(i).height+50,true));
					//draw
					
					break;
				}
			}
			if(node.childNode.size()==2||node.childNode.size()==1)
				insert(node.childNode.get(0));
			if(node.childNode.size()==2)
				insert(node.childNode.get(1));
			
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
	    	   g.drawLine((int)node.width-5,(int) node.height-25,(int)node.width+20,(int)node.height+20);
	       else 
	    	   g.drawLine((int)node.width+45, (int)node.height-25,(int)node.width+20,(int)node.height+20);
			
	       g.fillOval((int)node.width,(int) node.height,40, 40);
		   g.drawString(String.valueOf(node.node.getUdpPort()), (int)node.width, (int) node.height);
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
