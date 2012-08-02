package org.mt4jext.mttree;

/**
 * @author Ewoud van Pareren
 * 
 * This interface is to be used by Tree to perform actions
 * on the nodes within a tree.
 *
 */
public interface NodeProcessor {
	
	/**
	 * Process the given node.
	 * 
	 * Return true if the iteration system should continue processing
	 * this node's children.
	 * 
	 * 
	 * @param node the node to process
	 * @return true if you want to process its children, false if not 
	 */
	public boolean processNode(Node node);
	
}
