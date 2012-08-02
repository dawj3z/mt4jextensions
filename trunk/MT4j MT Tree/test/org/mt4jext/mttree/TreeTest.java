/**
 * 
 */
package org.mt4jext.mttree;

import org.mt4jext.mttree.Node;
import org.mt4jext.mttree.NodeProcessor;
import org.mt4jext.mttree.Tree;

import junit.framework.TestCase;

/**
 * @author ewoud
 *
 */
public class TreeTest extends TestCase {
	
		// Node stuff
		Tree tree;
		
		// Labeled subnodes
		Node[] nNodes;
		
		// No tree-context. Pure nodes.
		
		/* (non-Javadoc)
		 * @see junit.framework.TestCase#setUp()
		 * 
		 * Create a tree structure thingy.
		 * 
		 */
		protected void setUp() throws Exception {
			super.setUp();
			
			/*
			 * Build a simple tree
			 */
			nNodes = new Node[10]; // Zero is root
			nNodes[0] = new Node();
			for (int i = 1; i < 10; i++) {
				nNodes[i] = new Node();
			}
			nNodes[0].addChild(nNodes[1]);
			nNodes[0].addChild(nNodes[2]);
			nNodes[0].addChild(nNodes[3]);
			nNodes[1].addChild(nNodes[4]);
			nNodes[1].addChild(nNodes[5]);
			nNodes[1].addChild(nNodes[6]);
			
			nNodes[3].addChild(nNodes[7]);
			nNodes[3].addChild(nNodes[8]);
			nNodes[3].addChild(nNodes[9]);
			
			// Attached at first
			tree = new Tree();
			tree.setRoot(nNodes[0]);
			
		}

		/* (non-Javadoc)
		 * @see junit.framework.TestCase#tearDown()
		 */
		protected void tearDown() throws Exception {
			super.tearDown();
			tree = null;
			nNodes = null;
		}

	/**
	 * Test method for {@link org.mt4jext.mttree.Tree#getRoot()}.
	 */
	public void testGetRoot() {
		// Get current root
		assertEquals(nNodes[0], tree.getRoot());
		
		// Detach and retry
		tree.unsetRoot();
		assertNull(tree.getRoot());
		
	}

	/**
	 * Test method for {@link org.mt4jext.mttree.Tree#setRoot(org.mt4jext.mttree.Node)}.
	 */
	public void testSetRoot() {
		// Set current root
		
		// Setup already attached it. Check this one.
		assertEquals(nNodes[0], tree.getRoot());
		assertTrue(nNodes[9].partOfTree());
		
		// Make stray node
		Node loner = new Node();
		
		// Attempt to make it work
		try {
			tree.setRoot(loner);
			fail("Could set a second root");
		} catch (RuntimeException err) { }
		
		// Detach and add stray node properly
		tree.unsetRoot();
		
		tree.setRoot(loner);
		assertEquals(loner, tree.getRoot());
		assertEquals(tree, loner.getTree());
		
		// Detach and recheck
		tree.unsetRoot();
		assertNull(loner.getTree());
		
		// Now attempt wrong attach
		try {
			tree.setRoot(nNodes[3]);
			fail("Could set child node as root");
		} catch (RuntimeException err) { }
		
		// Reattach properly
		tree.setRoot(nNodes[0]);
		
		// Make a second loner tree
		Tree lonerTree = new Tree();
		lonerTree.setRoot(loner);
		
		// Now attempt to set wrong root
		try {
			tree.setRoot(loner);
			fail("Could attach one node to two trees");
		} catch (RuntimeException err) { }
		
		
		
		
	}

	/**
	 * Test method for {@link org.mt4jext.mttree.Tree#unsetRoot()}.
	 */
	public void testUnsetRoot() {

		// Detach root from tree
		tree.unsetRoot();
		
		assertNull(tree.getRoot());
		assertFalse(nNodes[0].partOfTree());
		assertFalse(nNodes[9].partOfTree());
		
		// That was pretty easy
	}

	/**
	 * Test method for {@link org.mt4jext.mttree.Tree#countNodes(boolean)}.
	 */
	public void testCountNodes() {
		// Actually, it's already tested in NodeTest
		// But okay
		nNodes[5].hide();
		nNodes[7].hide();
		nNodes[3].hide();
		
		assertEquals(10, tree.countNodes(true)); // Nine children
		assertEquals(5, tree.countNodes(false)); // Can only see 5
	}

	/**
	 * Test method for {@link org.mt4jext.mttree.Tree#nodeList(boolean)}.
	 */
	public void testNodeList() {
		boolean[] tickOff = new boolean[10];
		nNodes[5].hide();
		nNodes[7].hide();
		nNodes[3].hide();
		
		Node[] fullList = tree.nodeList(true);
		assertEquals(nNodes.length, fullList.length);
		for (Node n : fullList) {
			for (int i = 0; i < 10; i++) {
				if (n == nNodes[i]) {
					assertFalse(tickOff[i]);
					tickOff[i] = true;
				}
			}
		}
		for (int i = 0; i < 10; i++) {
			assertTrue(tickOff[i]);
			tickOff[i] = false;
		}
		
		// Now without the shown nodes
		Node[] partList = tree.nodeList(false);
		
		Node[] checkList = {nNodes[0], nNodes[1], nNodes[2],
							nNodes[4], nNodes[6]};
		assertEquals(checkList.length, partList.length);
		for (Node n : partList) {
			for (int i = 0; i < checkList.length; i++) {
				if (n == checkList[i]) {
					assertFalse(tickOff[i]);
					tickOff[i] = true;
				}
			}
		}
		
		for (int i = 0; i < checkList.length; i++) {
			assertTrue("tickoff fail at "+i,tickOff[i]);
		}
		
		
		
	}

	/**
	 * Test method for {@link org.mt4jext.mttree.Tree#nodesAtLevel(int, boolean)}.
	 */
	public void testNodesAtLevel() {
		nNodes[5].hide();
		nNodes[7].hide();
		nNodes[3].hide();
		
		// Bit hard to do, but here it is
		Node[][] lvNodes = {{nNodes[0]},
							{nNodes[1], nNodes[2], nNodes[3]},
							{nNodes[4], nNodes[5], nNodes[6],
								nNodes[7], nNodes[8], nNodes[9]}};
		
		for (int i = 0; i < 3; i++) {
			boolean[] tickOff = new boolean[lvNodes[i].length];
			Node[] checkem = tree.nodesAtLevel(i, true);
			assertEquals(tickOff.length, checkem.length);
			for (Node n : checkem) {
				for (int j=0; j < tickOff.length; j++) {
					if (lvNodes[i][j] == n) {
						assertFalse(tickOff[j]);
						tickOff[j] = true;
					}
				}
			}
			for (int j=0; j < tickOff.length; j++) {
				assertTrue(tickOff[j]);
			}
			
		}
		Node[][] lvShowNodes = {{nNodes[0]},
				{nNodes[1], nNodes[2]},
				{nNodes[4], nNodes[6]}};

		for (int i = 0; i < 3; i++) {
			boolean[] tickOff = new boolean[lvShowNodes[i].length];
			Node[] checkem = tree.nodesAtLevel(i, false);
			assertEquals(tickOff.length, checkem.length);
			for (Node n : checkem) {
				for (int j=0; j < tickOff.length; j++) {
					if (lvShowNodes[i][j] == n) {
						assertFalse(tickOff[j]);
						tickOff[j] = true;
					}
				}
			}
			for (int j=0; j < tickOff.length; j++) {
				assertTrue(tickOff[j]);
			}

		}
	}

	/**
	 * Test method for {@link org.mt4jext.mttree.Tree#countLevels(boolean)}.
	 */
	public void testCountLevels() {
		// Count the amount of levels in the tree

		nNodes[1].hide();
		nNodes[3].hide();
		
		
		// Test entire tree
		assertEquals(3, tree.countLevels(true));
		assertEquals(2, tree.countLevels(false));
		
		nNodes[3].show();
		nNodes[8].addChild(new Node());
		assertEquals(4, tree.countLevels(false));
		
		
	}

	/**
	 * Test method for {@link org.mt4jext.mttree.Tree#processTree(org.mt4jext.mttree.NodeProcessor)}.
	 */
	public void testProcessTree() {
		nNodes[2].hide();
		nNodes[5].hide();
		nNodes[7].hide();
		
		tree.processTree(new NodeProcessor() {
			public boolean processNode(Node in) {
				if (in.isShow()) {
					in.setObject("String");
					return true;
				} else return false;
			}
		});
		
		for (int i = 0; i < nNodes.length; i++) {
			if (nNodes[i].isVisible())
				assertNotNull(nNodes[i].getObject());
			else assertNull(nNodes[i].getObject());
			
		}
	}

}
