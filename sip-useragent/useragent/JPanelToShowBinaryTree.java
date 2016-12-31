package useragent;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

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
	public void insert(Node node){
		
		if(node.getUdpPort()==-1){//is root
			tree.add(new NodeTable(node,300,100));
			
			if(node.childNode.size()<=2)
				insert(node.childNode.get(0));
			if(node.childNode.size()==2)
				insert(node.childNode.get(1));
			//draw
		}
		else
		{
			Node parent = node.getParent();
			for(int i=0;i<tree.size();i++)
			{
				if(tree.get(i).node == parent){
					if(tree.get(i).node.childNode.get(0) == node)//left 
						tree.add(new NodeTable(node,tree.get(i).width-25,tree.get(i).height+50));
					else
						tree.add(new NodeTable(node,tree.get(i).width+25,tree.get(i).height+50));
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
			 g.fillOval((int)node.width,(int) node.height,40, 40);
		 } 
	}
	 @Override
	    public void paintComponent(Graphics g) {
		 super.paintComponent(g);
		 
		 // Draw Tree Here
		 drawTree((Graphics2D)g);
		
	    }

	 
 public  class NodeTable{
		public Node node;
		public double width;
		public double height;
        public NodeTable(Node node,double width,double height)	{
        	this.node = node;
        	this.width = width;
        	this.height = height;
        }
	}

}
