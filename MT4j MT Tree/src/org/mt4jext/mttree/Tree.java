package org.mt4jext.mttree;

import java.util.ArrayList;
import java.util.LinkedList;

import org.mt4j.components.TransformSpace;
import org.mt4j.sceneManagement.IPreDrawAction;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.math.Vector3D;


/**
 * @author Ewoud van Pareren
 * 
 * A tree object contains a tree and handles
 * some administration/data about it for easy/quick access.
 * 
 */
public class Tree {
	
	private Node root;
	private TreeScene treeScene = null;

	/*
	 * The update system will keep track whether a modification is needed or not.
	 * True: something has changed, the tree needs an update.
	 * False: the tree has not changed. It needs no update.
	 */
	private boolean invalidated = false;

	private Updater preUpdate;
	
	/**
	 * Constructor for an empty tree, no nodes set.
	 */
	public Tree() {
		preUpdate = new Updater();
	}
	

	/**
	 * Get the root node for this tree.
	 * 
	 * @return the root node, or null if there is none
	 */
	public Node getRoot() {
		return root;
	}

	/**
	 * Set this node as the root of the tree.
	 * 
	 * This will throw a RuntimeException if this tree already has a root,
	 * if the given node is a child or if the given node is already associated
	 * with a tree.
	 * 
	 * @param newRoot the node to be set as the tree root
	 */
	public void setRoot(Node newRoot) {
		if (root == null) {
			if (!newRoot.isChild()) {
				if (!newRoot.partOfTree()) {
					root = newRoot;
					root.setTree(this);
					root.updateHidden();
					invalidate();
				} else throw new
				RuntimeException("Cannot set root. Given node is already part of a tree.");
			} else throw new
			RuntimeException("Cannot set root. Given node is a child.");
		} else throw new
		RuntimeException("Cannot set root. This tree already has a root.");
	}

	/**
	 * Deassociate the current root node as a root.
	 * 
	 * @return the root node that was removed
	 */
	public Node unsetRoot() {
		Node returnThis = root;

		// Remove this as the container tree
		if (root != null) {
			root.setTree(null);
		}
		root = null; // Set root to null
		invalidate();
		return returnThis;
	}

	/**
	 * Count the total amount of nodes in the tree.
	 * 
	 * When includeInvisible is set to false, this method will not count
	 * nodes where isVisible() evaluates to false.
	 * 
	 * @param includeInvisible whether to include hidden nodes or not
	 * @return the amount of nodes in the tree
	 */
	public int countNodes(boolean includeInvisible) {
		if (root.isShow()) {
			return 1+root.countAllChildren(includeInvisible);
		} else return 0;
	}


	/**
	 * Return an array with all the nodes in the tree.
	 * 
	 * When includeInvisible is set to false, this method will not count
	 * nodes where isVisible() evaluates to false.
	 * 
	 * @param includeInvisible whether to include hidden nodes or not
	 * @return an array containing all the nodes
	 */
	public Node[] nodeList(boolean includeInvisible) {
		ArrayList<Node> returnList = new ArrayList<Node>();
		
		returnList.add(root);
		
		for (int i = 0; i < returnList.size(); i++) {
			Node[] received = returnList.get(i).getChildren(includeInvisible);
			for (int j = 0; j < received.length; j++) {
				returnList.add(received[j]);
			}
		}
		
		return returnList.toArray(new Node[0]);
	}

	/**
	 * Nodes at the specified level.
	 * 
	 * This returns a list of all the nodes at a certain level.
	 * The root is at level 0, its children are at level 1, their children
	 * are at level 2 etcetera.
	 * 
	 * When includeInvisible is set to false, this method will not count
	 * nodes where isVisible() evaluates to false.
	 * 
	 * @param includeInvisible whether to include hidden nodes or not
	 * @return an array containing all the nodes at the specified level
	 */
	public Node[] nodesAtLevel(int level, boolean includeInvisible) {
		if (!includeInvisible && !root.isShow())
			return new Node[0];
		
		Node[] startHere = {root};
		return nodesBelow(startHere, level, includeInvisible);
	}

	// Ugly implementation, but avoids stacks (or at least, stacks that are too large)
	private static Node[] nodesBelow(Node[] given, int level, boolean includeInvisible) {
		if (level == 0) {
			return given;
		} else {
			// First count the amount of nodes one level below
			int countThese = 0;
			for (int i = 0; i < given.length; i++) {
				countThese += given[i].countChildren(includeInvisible);
			}
			if (countThese == 0) return new Node[0];

			ArrayList<Node> useThis = new ArrayList<Node>(countThese);
			for (int i = 0; i < given.length; i++) {
				Node[] wat = given[i].getChildren(includeInvisible);
				for (int j = 0; j < wat.length; j++) {
					useThis.add(wat[j]);
				}
			}
			return nodesBelow(useThis.toArray(new Node[0]), level-1, includeInvisible);
		}
	}

	/**
	 * Count the maximum amount of levels in our tree.
	 * 
	 * When includeInvisible is set to false, this method will not count
	 * nodes where isVisible() evaluates to false.
	 * 
	 * @param includeInvisible whether to include hidden nodes or not
	 * @return the amount of levels in the tree
	 */
	public int countLevels(boolean includeInvisible) {
		return countLevelsBelow(root, includeInvisible); // Includes root as a level 
	}

	/*
	 * Unoptimized implementation. Recursive.
	 */
	private static int countLevelsBelow(Node given, boolean includeHidden) {
		Node[] list = given.getChildren(includeHidden);
		int counting = 0;
		for (int i = 0; i < list.length; i++) {
			counting = Math.max(counting, countLevelsBelow(list[i], includeHidden));
		}
		return counting+1;
	}
	
	/**
	 * Iterate through the tree using a NodeProcessor.
	 * 
	 * The root will be the first node to be processed,
	 * and parents will be processed before their children.
	 * 
	 * Other than that, there should be no assumptions about the order
	 * in which nodes are processed.
	 * 
	 * @param nodeProcessor the NodeProcessor instance to use
	 */
	public void processTree(NodeProcessor nodeProcessor) {
		// The Java API has twenty flavors of queue,
		// yet the linked list seemed the best solution. Typical, right?
		LinkedList<Node> nodeList = new LinkedList<Node>();
		nodeList.add(root);
		do {
			Node use = nodeList.poll();
			if (nodeProcessor.processNode(use)) {
				// Add its children
				for (Node n: use.getChildren(true)) {
					nodeList.add(n);
				}
			}
		} while (nodeList.peek() != null);
	}
	
	

	/*
	 * Some administrative stuff...
	 */

	/*
	 * 
	 * Note: this does the decoupling from existing scene
	 * Note: this does not automatically cause an update,
	 * so you can attach your own (non-default) components to Nodes
	 * before the update would attach defaults.
	 */
	/**
	 * Attach this tree to a TreeScene object.
	 * 
	 * This also handles decoupling from existing scene.
	 * 
	 * Note that this does not automatically cause an update,
	 * so you can attach your own (non-default) components to Nodes
	 * before the update would attach defaults.
	 * 
	 * @param set the TreeScene instance to attach this Tree to
	 */
	public void setTreeScene(TreeScene set) {
		
		if (treeScene == null) {
			// No existing tree thing
			// Invalidate, and possibly auto-update
			treeScene = set;
			treeScene.setTree(this);
			treeScene.registerPreDrawAction(preUpdate);
			invalidate();
		} else if (set == null) {
			// Decouple tree from scene
			Node[] curNodes = nodeList(true);
			for (int i = 0; i < curNodes.length; i++) {
				// Destroy components from TreeScene
				curNodes[i].setComponent(null);
			}
			// Remove everything from tree scene, including lines.
			treeScene.removeAllNodeComponents();
			treeScene.setTree(null);
			treeScene.unregisterPreDrawAction(preUpdate);
			treeScene = set;

			invalidate();
			// Invalidate?
		} else {
			setTreeScene(null);
			setTreeScene(set);
		}

	}
	
	/**
	 * Get the TreeScene instance associated with this Tree.
	 * 
	 * @return the TreeScene instance this Tree is attached to, or null if there is none
	 */
	public TreeScene getTreeScene() {
		return treeScene;
	}



	/**
	 * Return whether this Tree needs an update.
	 * 
	 * When the structure of the Tree changes, or when nodes
	 * are switched between hidden or visible, the Tree is invalidated.
	 * 
	 * The invalidation is lifted when the system has finished a visual update.
	 * 
	 * 
	 * @return whether this tree needs an update or not
	 */
	public boolean needsUpdate() {
		return invalidated;
	}


	/**
	 * Update tree administration and visibility, and lift the invalidation.
	 * 
	 * This method is to be called after structural changes to the Tree are done.
	 * 
	 * When this method is called, visible nodes will be placed on the scene,
	 * invisible nodes will be hidden, and the placement of nodes will be
	 * recalculated.
	 * Nodes that have no component are given a default
	 * component (TreeScene.makeDefaultComponent())
	 * 
	 * Applications can generate or modify a Tree structure, but the changes are not
	 * complete until this method is called.
	 * This is done to prevent the framework from internally calling update routines
	 * on each single change in structure.
	 */
	/*public void update() {
	}*/
	
	private void update() {
		if (treeScene == null) return;
		if (root == null) return;

		// Take all currently shown nodes and remove nodes we shouldn't see
		Node[] list = treeScene.getAllNodes();
		
		for (Node n: list)
			// If it is not in our tree, or if it's hidden
			if (n.getTree() != this || !n.isVisible())
				treeScene.removeChild(n.getComponent());
		
		
		// Now grab all the nodes we wish to show
		list = nodeList(false);
		
		
		
		// This is an O(n) operation
		for (Node n: list) {
			if (n.getComponent() == null)
				n.setComponent();
			if (n.isChild() && n.getEdge().component == null)
				n.getEdge().setComponent();

			if (n.isVisible()) {
				if (n.getComponent().getParent() == null)
					treeScene.addChild(n);
			}
		}
		
		/*
		// This is a O(n) operation
		for (int i = 0; i < list.length; i++) {
			if (list[i].getComponent() == null)
				list[i].setComponent();
			if (list[i].isChild() && list[i].getEdge().component == null)
				list[i].getEdge().setComponent();

			if (list[i].isVisible()) {
				if (list[i].getComponent().getParent() == null)
					treeScene.addChild(list[i]);
				if (list[i].isChild()) {
					if (list[i].getEdge().component.getParent() == null) {
						treeScene.addChild(list[i].getEdge().component);
					}
					list[i].getEdge().updateLines();
				}
			} else if (list[i].getComponent().getParent() != null) {
				treeScene.removeChild(list[i].getComponent());
				treeScene.removeChild(list[i].getEdge().getComponent());
			}
		}*/
		
		
		nodePlacements(); // Recalculate node placements
		
		/*
		 * Update all the lines
		 */
		for (int i = 0; i < list.length; i++) {
			if (list[i].isVisible() && list[i].isChild()) {
				list[i].getEdge().updateLines();
			}
			// To make sure it's drawn on the lines
			list[i].getComponent().sendToFront();
			
		}

		treeScene.invalidate();
		
		invalidated = false;
		
	}
	
	/**
	 * Internal. Invalidate the tree structure.
	 */
	protected void invalidate() {
		invalidated = true;
		if (treeScene != null)
			treeScene.invalidate();
	}

	/**
	 * Calculates the placement of nodes.
	 * This changes the position of the Nodes in the tree.
	 * 
	 * Part of the update method.
	 *  
	 */
	private void nodePlacements() {
		// Go through entire tree and determine node placements
		Node[] list = nodeList(false);

		// Now choose the nodes that have no (active) parents
		ArrayList<Node> children = new ArrayList<Node>();

		for (int i = 0; i < list.length; i++) {
			list[i].difx = 0;
			list[i].dify = 0;
			list[i].childWidth = 0; // Zero indicates not marked yet

			if (list[i].countAllChildren(false) == 0) {
				children.add(list[i]);
			}
		}
		for (int i = 0; i < children.size(); i++) {
			nodePlaceReverseCursion(children.get(i));
		}

		
		// Now place the nodes
		nodePlaceApplyPosition(root, (MT4jSettings.getInstance().getWindowWidth()/2), 30);

	}
	
	
	private void nodePlaceReverseCursion(Node in) {
		if (in.childWidth != 0) {
			System.out.println("Skipping with "+in.childWidth);
			return; // Skip what we already covered
		}

		Node[] counted = in.getChildren(false);
		int width = 0;
		for (int i = 0; i < counted.length; i++) {
			// If not all subtrees of this have been done, stop.
			if (counted[i].childWidth == 0) {
				return;
			}
			width += counted[i].childWidth;
		}
		in.childWidth = Math.max(width, (int)(in.getComponent().getWidthXY(TransformSpace.LOCAL))+10); // 50 pixel width minimum/per node
		
		// Now calculate child positions relative to this node
		int leftSide = ((-width)/2);
		for (int i = 0; i < counted.length; i++) {
			counted[i].dify = 30+((int)(in.getComponent().getHeightXY(TransformSpace.LOCAL))/2)+((int)(counted[i].getComponent().getHeightXY(TransformSpace.LOCAL))/2); // 30 pixel height between levels
			counted[i].difx = -leftSide-(counted[i].childWidth/2);
			leftSide += counted[i].childWidth;
		}
		if (in.childWidth == width) in.childWidth += 10;

		// Now try an upward recursion
		if (in.getParent() != null) nodePlaceReverseCursion(in.getParent());

	}
	
	private void nodePlaceApplyPosition(Node in, int x, int y) {
		in.getComponent().setPositionGlobal(new Vector3D(x+in.difx, y+in.dify, 0));

		Node[] counted = in.getChildren(false);
		for (int i = 0; i < counted.length; i++) {
			nodePlaceApplyPosition(counted[i], x+in.difx, y+in.dify);
		}

	}
	
	private class Updater implements IPreDrawAction {
		
		@Override
		public boolean isLoop() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public void processAction() {
			// TODO Auto-generated method stub
			if (root != null && invalidated) update();
		}
		
	}
	
}
