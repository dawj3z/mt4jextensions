package org.mt4jext.mttree;

import org.mt4j.components.visibleComponents.shapes.MTLine;
import org.mt4j.util.math.Vertex;

/**
 * @author Ewoud van Pareren
 * 
 * A simple edge class.
 * 
 */
public class Edge {
	
	/*
	 * The 'parent' node of this edge.
	 */
	Node parent; // The Node above this edge
	Node child; // The Node below this edge
	
	MTLine component;
	
	
	/**
	 * Edge constructor.
	 * 
	 * 
	 * @param parent the 'parent' node
	 * @param child the 'child' node
	 */
	public Edge(Node parent, Node child) {
		this.parent = parent;
		this.child = child;
	}

	// Stays with child
	protected void setParent(Node parent) {
		this.parent = parent;
	}
	
	/**
	 * Get the parent, aka the Node "above" this edge.
	 * 
	 * @return the parent Node
	 */
	public Node getParent() {
		return parent;
	}

	/**
	 * Get the child, aka the Node "below" this edge.
	 * 
	 * @return the child Node
	 */
	public Node getChild() {
		return child;
	}

	/**
	 * Set the Edge component.
	 * 
	 * Default version.
	 */
	public void setComponent() {
		if ((getTree() == null) || (getTree().getTreeScene() == null)) {
			throw new RuntimeException("Cannot set line component: no tree or treescene to use");
		}
		setComponent(getTree().getTreeScene().makeDefaultLine());
	}
	/**
	 * Set the Edge component.
	 * 
	 * Custom version.
	 * 
	 * @param in the MTLine to use as component
	 */
	public void setComponent(MTLine in) {
		if (((getTree() == null) || (getTree().getTreeScene() == null)) && (in != null))
			throw new RuntimeException("Cannot set line component: no tree or treescene to use");
		
		if (component != null) {
			getTree().getTreeScene().removeChild(component);
			component.destroy();
		}
		
		component = in;
		
		if (getTree() != null) getTree().invalidate();
	}
	
	/**
	 * Get the current component.
	 * 
	 * @return the current component, may be null if no component is set
	 */
	public MTLine getComponent() {
		return component;
	}
	
	
	/**
	 * This updates the lines.
	 * Note that this is called from update, and from any movement of connected nodes.
	 * 
	 * Any application that moves Node components outside of the framework should call
	 * this method to update the lines.
	 */
	public void updateLines() {
		if (parent != null && parent.getComponent() != null &&
				child != null && child.getComponent() != null && component != null) {
			Vertex[] newLine = new Vertex[2];
			newLine[0] = new Vertex(parent.getComponent().getCenterPointGlobal());
			newLine[1] = new Vertex(child.getComponent().getCenterPointGlobal());
			component.setVertices(newLine);
			
		}
	}
	
	/**
	 * A handle to the Tree.
	 * 
	 * @return the Tree this Edge is a part of, or null if this Edge is not part of any Tree
	 */
	Tree getTree() {
		return parent.getTree();
	}
	
	
}
