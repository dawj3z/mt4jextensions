package org.mt4jext.mttree.sample;

import java.util.Random;

import org.mt4j.MTApplication;
import org.mt4jext.mttree.CollapsibleNode;
import org.mt4jext.mttree.Node;
import org.mt4jext.mttree.Tree;
import org.mt4jext.mttree.TreeScene;


/**
 * @author Ewoud van Pareren
 * 
 * A sample application using MT Tree.
 * This adds a random amount of (collapsible) nodes to the tree.
 */
public class MTSample extends MTApplication {
	
	/**
	 * Some serial thing, not sure why Eclipse keeps whining about it...
	 */
	private static final long serialVersionUID = 466267884415016997L;

	static TreeScene treeScene;
	
	static Random rand = new Random();
	
	@Override
	public void startUp() {
		// TODO Auto-generated method stub
		treeScene = new TreeScene(this, "Tree Scene");
		addScene(treeScene);
		// Construct simple tree
		Tree basic = new Tree();
		basic.setRoot(new CollapsibleNode());
		randomAdd(basic.getRoot(), 25);
		basic.setTreeScene(treeScene);
	}
	
	/**
	 * Add random Nodes.
	 * 
	 * This adds random nodes to the given node, both direct children
	 * and recursively as indirect children.
	 * 
	 * @param in the Node to add random Nodes to
	 * @param addTo the amount of random Nodes to add
	 */
	private static void randomAdd(Node in, int addTo) {
		if (addTo <= 0) return;
		
		int children = in.countChildren(true);
		if (children == 0) {
			// Add new child
			in.addChild(new CollapsibleNode());
			randomAdd(in, addTo-1);
		} else {
			// Add new child or go into existing child
			if (rand.nextInt(children+5) < children+4) {
				int amountInto = Math.min(rand.nextInt(addTo*2), addTo);
				
				randomAdd(in.getChild(rand.nextInt(children)), amountInto);
				randomAdd(in, addTo-amountInto);
			} else {
				in.addChild(new CollapsibleNode());
				randomAdd(in, addTo-1);
			}
		}
	}
	
	
	public static void main(String[] args) {
		initialize();
	}
	
	
}
