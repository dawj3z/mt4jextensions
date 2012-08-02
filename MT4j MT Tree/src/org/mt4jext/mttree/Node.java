package org.mt4jext.mttree;

import java.util.ArrayList;

import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;

/**
 * @author Ewoud van Pareren
 * 
 * A simple node class for MT Tree.
 * 
 */
public class Node {
	
	private Edge edge;
	private Node parent;
	private int parentIndex = -1; // This node's index with its parent
	private ArrayList<Node> children;
	
	private boolean show;
	
	private Tree tree = null; // A reference to the global tree scene obj
	private MTRectangle component;
	private Object object;
	
	
	/**
	 * This constructor creates a simple node.
	 * 
	 */
	public Node() {
		children = new ArrayList<Node>();
		show = true;
		
	}
	
	/**
	 * Add a node as child.
	 * 
	 * This throws a RuntimeException if the given child already has a parent
	 * 
	 * @param child the child to add
	 * @return The index of the child node
	 */
	public int addChild(Node child) {
		if (child.parent == null) {
			children.add(child);
			if (child.edge == null) child.edge = new Edge(this, child);
			else child.edge.setParent(this);
			child.setTree(tree);
			child.parent = this;
			child.parentIndex = children.size()-1;
			invalidateCount();
			if (tree != null) tree.invalidate();
			return children.size()-1;
		} else throw new RuntimeException("Tried to add a child that already has a parent");
	}
		
	/**
	 * Add a node as child at the given index.
	 * 
	 * This throws a RuntimeException if the given child already has a parent.
	 * 
	 * @param child the child to add
	 * @param index the index to put the child node
	 * @return the index of the added child node
	 */
	public int addChild(Node child, int index) {
		if (child.parent == null) {
			children.add(index, child);
			if (child.edge == null) child.edge = new Edge(this, child);
			else child.edge.setParent(this);
			child.setTree(tree);
			child.parentIndex = index;
			child.parent = this;
			
			// Update the other children's parent indices
			for (int i = index+1; i < children.size(); i++) {
				children.get(i).parentIndex = i;
			}
			
			invalidateCount();
			if (tree != null) tree.invalidate();
			return index;
		} else throw new RuntimeException("Tried to add a child that already has a parent.");
	}
	
	/**
	 * Get the child at the given index.
	 * 
	 * @param index the index of the requested child
	 * @return the child at the specified index
	 */
	public Node getChild(int index) {
		return children.get(index);
	}
	
	/**
	 * Get an array containing all the (direct) children.
	 * 
	 * This is equivalent to getChildren(true).
	 * 
	 * @return an array containing all the children
	 */
	public Node[] getChildren() {
		return children.toArray(new Node[0]);
	}
	
	/**
	 * Get an array containing all the (direct) children.
	 * 
	 * If includeHidden is false, only children that are not hidden
	 * (that is, only children where isShow evaluates to true)
	 * are returned.
	 * 
	 * @param includeHidden specify whether to include hidden nodes or not
	 * @return an array containing the children
	 */
	public Node[] getChildren(boolean includeHidden) {
		if (includeHidden) return getChildren();
		ArrayList<Node> returnThis = new ArrayList<Node>();
		for (int i = 0; i < children.size(); i++) {
			if (children.get(i).isShow())
				returnThis.add(children.get(i));
			
		}
		return returnThis.toArray(new Node[0]);
	}
	
	/**
	 * Get this node's parent.
	 * 
	 * @return the parent node, or null if this is not a child node.
	 */
	public Node getParent() {
		return parent;
	}
	
	/**
	 * Get the index of this node with its parent.
	 * 
	 * If this node is not a child, this method returns -1.
	 * 
	 * @return the index of this node with its parent, or -1 if there is no parent
	 */
	public int getParentIndex() {
		return parentIndex;
	}

	/**
	 * Get an Edge between this node and a child.
	 * 
	 * Equivalent to getChild(index).getEdge()
	 * 
	 * @param index the index of the child
	 * @return the Edge between this node and its child at given index
	 */
	public Edge getChildEdge(int index) {
		return children.get(index).getEdge();
	}
	
	/**
	 * Get the Edge between this node and its parent.
	 * 
	 * This throws a RuntimeException if this node has no parent.
	 * 
	 * @return the Edge between this node and its parent
	 */
	public Edge getEdge() {
		if (parent != null) return edge;
		else throw new RuntimeException("Cannot get edge to nonexistent parent");
	}
	
	
	/**
	 * Remove (separate) the child at the given index from its parent.
	 * Note that this will also remove the associated Edge object.
	 * 
	 * @param index the index of the child to separate
	 * @return the separated child
	 */
	public Node removeChild(int index) {
		Node child = children.remove(index);
		
		child.parent = null;
		child.parentIndex = -1;
		child.setTree(null);
		child.edge.setParent(null);
		child.updateHidden();

		// Update the other children's parent indices
		for (int i = index; i < children.size(); i++) {
			children.get(i).parentIndex = i;
		}
		
		invalidateCount();
		if (tree != null) tree.invalidate();
		return child;
	}
	
	/**
	 * Remove this node from its parent.
	 * 
	 * This throws a RuntimeException if there is no parent.
	 */
	public void removeFromParent() {
		if (parent == null) throw new RuntimeException("Cannot remove from nonexistent parent");
		parent.removeChild(parentIndex);
	}
	
	/**
	 * Hide this node.
	 * 
	 * This node will be hidden from the tree scene,
	 * even if its parents are all set to show.
	 */
	public void hide() {
		if (show) {
			invalidateCount();
			if (tree != null) tree.invalidate();
			show = false;
			updateHidden();
		}
	}
	
	/**
	 * Show this node.
	 * 
	 * This node will be visible in the tree scene
	 * if its parents are all set to show.
	 */
	public void show() {
		if (!show) {
			invalidateCount();
			if (tree != null) tree.invalidate();
			show = true;
			updateHidden();
		}
	}
	
	
	/**
	 * Is this node a child?
	 * 
	 * @return true if this node has a parent, false otherwise.
	 */
	public boolean isChild() {
		return (parent != null);	
	}
	
	/**
	 * Is this node part of a tree?
	 * 
	 * @return true if this node is part of a Tree object, false otherwise
	 */
	public boolean partOfTree() {
		return (tree != null);
	}
	
	/**
	 * Is this node visible?
	 * 
	 * This checks whether all nodes between this and the root
	 * are set to show (including this node).
	 * 
	 * If you want to check the 'show' state of this node, use isShow().
	 * 
	 * 
	 * @return true if the node is visible, false if it is hidden
	 */
	public boolean isVisible() {
		if (parent == null) return show;
		return show && parent.isVisible();
	}
	
	/**
	 * Is this node set to show?
	 * 
	 * Note that this checks the Node's own 'show' setting. If you want to
	 * know whether a Node is visible from the root, use isVisible.
	 * 
	 * 
	 * @return true if the node is set to show, false if it is hidden
	 */
	public boolean isShow() {
		return show;
	}
	
	/**
	 * Count this node's children. This includes indirect children.
	 * 
	 * When includeHidden is set to false, this method will not count
	 * nodes where isShow() evaluates to false, or any of their children.
	 * 
	 * @param includeHidden true to include nodes that are not visible
	 * @return the amount of child nodes
	 */
	public int countAllChildren(boolean includeHidden) {
		if (!countValid) {
			counted = 0;
			countedHidden = 0;
			for (int i = 0; i < children.size(); i++) {
				// Have the children give their counts
				
				
				// Now count their results to your own
				if (children.get(i).isShow()) {
					counted += 1+children.get(i).countAllChildren(false);
				}
				countedHidden += 1+children.get(i).countAllChildren(true);
				
			}
			countValid = true;
		}
		
		if (includeHidden) return countedHidden;
		else return counted;
	}
	
	
	/**
	 * Count the amount of child nodes.
	 * 
	 * If includeHidden is false, only child nodes where isShow()
	 * evaluates to true are counted. 
	 * 
	 * @param includeHidden count hidden nodes or not
	 * @return the amount of child nodes 
	 */
	public int countChildren(boolean includeHidden) {
		if (includeHidden) return children.size();
		else {
			int countThis = 0;
			for (int i = 0; i < children.size(); i++) {
				if (children.get(i).isShow()) countThis++;
			}
			return countThis;
		}
	}
	
	/**
	 * Set the current component and, if needed, add it to the tree scene.
	 * This variant makes a default component (using TreeScene.makeDefaultComponent()).
	 * 
	 * Throws RuntimeException if there is no TreeScene to use.
	 *  
	 */
	public void setComponent() {
		if ((tree == null) || (tree.getTreeScene() == null))
			throw new RuntimeException("Cannot make component, no tree to attach.");
		
		setComponent(tree.getTreeScene().makeDefaultComponent());
	}

	/**
	 * Set the current component and, if needed, add it to the tree scene.
	 * This variant takes a MTRectangle to use as component.
	 * 
	 * Throws RuntimeException if there is no TreeScene to use.
	 * 
	 * 
	 * Note that the component can be set to null, in order to remove the component without
	 * adding a new one. In that case, a default component will be generated on the next
	 * Tree update.
	 * 
	 * If this node, or its Tree, is to be detached from its TreeScene (or MTApplication),
	 * the current component should be removed by setComponent(null) and reattached/copied
	 * after the move. 
	 * 
	 * @param in the component to associate with this node
	 */
	public void setComponent(MTRectangle in) {
		if (tree == null)
			throw new RuntimeException("Cannot set component, no Tree(Scene) to attach.");
		else if (tree.getTreeScene() == null)
			throw new RuntimeException("Cannot set component, no TreeScene to attach.");
		
		// Destroy any previous component
		if (component != null) {
			// Attempt to remove from TreeScene
			tree.getTreeScene().removeChild(component);
			component.destroy();
		}
		// Note that this will cause the scene to have 'missing nodes'
		// unless you update...
		
		
		// The new component thing
		component = in;
		
		
		if (tree != null) tree.invalidate();
	}
	
	/**
	 * Get the component reference.
	 * 
	 * Can give null if no component is set.
	 * 
	 * @return the MT4j component, or null if there is none
	 */
	public MTRectangle getComponent() {
		return component;
		
	}
	
	/**
	 * This is called when adding this node's component to the TreeScene.
	 * 
	 * It is used to add event listeners (like dragging) to the component.
	 * 
	 * Subclasses that add additional listeners should override this method.
	 * 
	 * @param scene the scene to which the component is added (convenience)
	 */
	protected void setComponentListeners(TreeScene scene) {
		// Add a gesture listener for dragging, one that updates the edge lines
		
		component.registerInputProcessor(new DragProcessor(scene.getMTApplication()));
		component.addGestureListener(DragProcessor.class, new LineDragAction(this));
		
	}
	
	
	
	/**
	 * Get this node's additional object.
	 * 
	 * 
	 * @return the additional object
	 */
	public Object getObject() {
		return object;
	}
	
	
	/**
	 * Set this node's additional object.
	 * 
	 * Each node can contain a single Object that may be used for identification
	 * or storage of other application-specific data.
	 * 
	 * Note that for a more flexible way to add additional data to a node,
	 * you can make subclasses of Node.
	 * 
	 * @param in the additional object to set
	 */
	public void setObject(Object in) {
		object = in;
	}

	
	
	/**
	 * Get the Tree instance this node is a part of.
	 * 
	 * @return an instance of Tree, or null if partOfTree() is false
	 */
	public Tree getTree() {
		return this.tree;
	}
	
	
	/*
	 * Some less public methods...
	 */

	protected void setTree(Tree tree) {
		this.tree = tree;
		for (int i = 0; i < children.size(); i++) {
			children.get(i).setTree(tree);
		}
	}
	
	/*
	 * Some values used for various recursive operations.
	 */
	protected int difx;
	protected int dify;
	protected int childWidth;
	
	
	/*
	 * This uses a system for caching results
	 * When you request the children node count,
	 * the result is cached for future calls.
	 * 
	 * When anything happens in the tree, this result is invalidated.
	 */
	private boolean countValid = false;
	private int counted = 0;
	private int countedHidden = 0;
	
	protected void invalidateCount() {
		countValid = false;
		if (parent != null) parent.invalidateCount();
	}
	
	/*
	 * This is fired when the node's visibility may have changed.
	 * Use isVisible to check whether the node is actually visible.
	 * 
	 * This method should be overridden by subclasses.
	 */
	protected void updateHidden() {
		for (Node n : children) {
			if (n.show) n.updateHidden();
		}
	}
	
}
