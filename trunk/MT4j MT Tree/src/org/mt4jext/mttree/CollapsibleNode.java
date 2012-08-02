package org.mt4jext.mttree;

import org.mt4j.components.MTComponent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.util.MTColor;


/**
 * @author Ewoud van Pareren
 * 
 * This version allows the user (or code) to collapse nodes.
 * 
 * Note that CollapsibleNodes only affect other CollapsibleNodes.
 * Children that are not CollapsibleNodes will remain unaffected.
 */
public class CollapsibleNode extends Node {
	boolean collapsed = false;
	boolean collapsedByParent = false;
	
	boolean originalOutlined = false;
	MTColor originalColor;
	
	
	/**
	 * This method will take the given node, and replace it with a CollapsibleNode.
	 * 
	 * It will detach the given Node's parent and children and give these to the
	 * returned CollapsibleNode. 
	 * The CollapsibleNode will also assume the given Node's Object and Component.
	 * 
	 * After calling this, the given Node will be empty
	 * (no parent, children, component or object).
	 * 
	 * @param in the Node to replace 
	 * @return the generated CollapsibleNode
	 */
	public static CollapsibleNode makeCollapsible(Node in) {
		
		Tree tree = in.getTree();
		
		// Hold onto the child nodes
		Node[] children = in.getChildren(true);
		// Remove all children
		while (in.countChildren(true) > 0) in.removeChild(0);
		
		// Now make a new collapsible node
		CollapsibleNode newNode = new CollapsibleNode();
		
		for (int i = 0; i < children.length; i++)
			newNode.addChild(children[i]);
		
		// Detach from parent, or root
		if (in.isChild()) {
			int parentIndex = in.getParentIndex();
			Node parent = in.getParent();
			parent.removeChild(parentIndex);
			parent.addChild(newNode, parentIndex);
		} else if (tree != null) {
			// Assume this is root. Replace old root.
			tree.unsetRoot();
			tree.setRoot(newNode);
		}
		
		newNode.setComponent(in.getComponent());
		newNode.setObject(in.getObject());
		
		in.setComponent(null);
		in.setObject(null);
		
		return newNode;
	}
	
	
	/**
	 * Is this node collapsed?
	 * 
	 * That is, does this node fold its children?
	 * 
	 * @return true if the node is collapsed, false otherwise
	 */
	public boolean isCollapsed() {
		return collapsed;
	}
	
	/**
	 * Is this node hidden by a collapse by its parent?
	 * 
	 * This does not check whether the node is hidden by
	 * collapse through any parent other than its own.
	 * 
	 * @return true if the node is hidden by collapse, false if it is hidden by itself or not hidden
	 */
	public boolean isHiddenByCollapse() {
		return collapsedByParent;
	}
	
	/**
	 * Collapse this node by hiding its children. 
	 * 
	 * This will cause any non-hidden children to hide, and any children that are
	 * added in the future will be hidden as well.
	 * When "show()" is called on a child, it will remain hidden until "expand()" is
	 * called.
	 */
	public void collapse() {
		Node[] collapseThese = getChildren(false);
		for (Node in : collapseThese) {
			if (!(in instanceof CollapsibleNode)) continue;
			CollapsibleNode node = (CollapsibleNode)in;
			if (node.isShow()) {
				node.superHide();
				node.collapsedByParent = true;
			}
		}
		
		// Change the look of the component: thicken the outline
		if (!collapsed && this.getComponent() != null) {
			originalOutlined = getComponent().isNoStroke();
			originalColor = getComponent().getStrokeColor();
			getComponent().setNoStroke(false);
			getComponent().setStrokeWeight(getComponent().getStrokeWeight()+2);
			getComponent().setStrokeColor(MTColor.RED);
			getComponent().generateAndUseDisplayLists();
		}

		collapsed = true;
		
		
	}
	
	/** 
	 * Expand, aka uncollapse
	 * 
	 * Take the children of this node that were no longer visible by
	 * collapse and show them again.
	 */
	public void expand() {
		Node[] expandThese = getChildren(true);
		for (Node in : expandThese) {
			if (!(in instanceof CollapsibleNode)) continue;
			CollapsibleNode node = (CollapsibleNode)in;
			
			if (node.collapsedByParent) {
				node.superShow();
				node.collapsedByParent = false;
			}
			
		}
		// Change the look of the component: thicken the outline
		if (collapsed && this.getComponent() != null) {
			getComponent().setStrokeWeight(getComponent().getStrokeWeight()-2);
			getComponent().setStrokeColor(originalColor);
			getComponent().setNoStroke(originalOutlined);
			if (getComponent().isUseDisplayList())
				getComponent().generateAndUseDisplayLists();
		}
		
		collapsed = false;
	}
	
	private void superShow() {
		super.show();
	}
	private void superHide() {
		super.hide();
	}
	
	// If it's manually hidden/shown, reset the collapsed state
	/**
	 * Show this node.
	 * 
	 * This method has the same effect as with the regular Node class, with
	 * the exception that it takes collapsed parents into account.
	 * 
	 * If the parent is collapsed, this method will not show the Node directly,
	 * however, it does mark the node to be shown when the parent expands or when
	 * this node is removed from its parent.
	 * 
	 * @see org.mt4jext.mttree.Node#show()
	 */
	public void show() {
		
		boolean parentCollapsed = isChild() &&
				getParent() instanceof CollapsibleNode &&
				((CollapsibleNode)getParent()).isCollapsed();
		
		// Don't really show if we're collapsed/hidden by parent
		if (parentCollapsed) collapsedByParent = true;	
		else superShow();
	}
	
	/**
	 * Hide this node.
	 * 
	 * This method has the same effect as with the regular Node class, with
	 * the exception that it takes collapsed parents into account.
	 * 
	 * If the Node is hidden while its parent is collapsed, the Node will be
	 * marked, so that expanding the parent
	 * 
	 * 
	 * @see org.mt4jext.mttree.Node#hide()
	 */
	public void hide() {
		superHide();
		// It's not collapsed by parent if our hide is 'genuine'
		collapsedByParent = false;
	}
	
	/* (non-Javadoc)
	 * @see ewoudvp.uva.tree.Node#addChild(ewoudvp.uva.tree.Node)
	 */
	public int addChild(Node child) {
		int returnThis = super.addChild(child);
		// Easiest implementation: just redo collapse (or expand)
		if (collapsed) collapse();
		else expand();
		return returnThis;
	}
	
	/* (non-Javadoc)
	 * @see ewoudvp.uva.tree.Node#addChild(ewoudvp.uva.tree.Node, int)
	 */
	public int addChild(Node child, int index) {
		int returnThis = super.addChild(child, index);
		// Easiest implementation: just redo collapse (or expand)
		if (collapsed) collapse();
		else expand();
		return returnThis;
	}
	
	/* (non-Javadoc)
	 * @see ewoudvp.uva.tree.Node#removeChild(int)
	 */
	public Node removeChild(int index) {
		Node giveBack = super.removeChild(index);
		if (collapsed && giveBack instanceof CollapsibleNode) {
			CollapsibleNode colNode = (CollapsibleNode)giveBack;
			if (colNode.collapsedByParent) {
				colNode.superShow();
				colNode.collapsedByParent = false;
			}
		}
		return giveBack;
	}
	
	// Collapsible Nodes can be double tapped in order to expand/hide them
	protected void setComponentListeners(TreeScene scene) {
		super.setComponentListeners(scene);
		MTComponent component = this.getComponent();
		component.registerInputProcessor(new TapProcessor(scene.getMTApplication(), 5.0f));
		component.addGestureListener(TapProcessor.class, new DoubleTapCollapse(this));
	}
}
