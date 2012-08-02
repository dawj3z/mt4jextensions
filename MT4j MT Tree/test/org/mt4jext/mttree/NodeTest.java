/**
 * 
 */
package org.mt4jext.mttree;

import org.mt4jext.mttree.Edge;
import org.mt4jext.mttree.Node;
import org.mt4jext.mttree.Tree;

import junit.framework.TestCase;

/**
 * @author ewoud
 *
 */
public class NodeTest extends TestCase {

	// Node stuff
	Node root;
	
	// Labeled subnodes
	Node[] nNodes;
	
	Tree tree;
	
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
		root = new Node();
		nNodes = new Node[10]; // Zero is root
		nNodes[0] = root;
		for (int i = 1; i < 10; i++) {
			nNodes[i] = new Node();
		}
		root.addChild(nNodes[1]);
		root.addChild(nNodes[2]);
		root.addChild(nNodes[3]);
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
		root = null;
		nNodes = null;
	}

	/**
	 * Test method for {@link org.mt4jext.mttree.Node#addChild(org.mt4jext.mttree.Node)}.
	 */
	public void testAddChildNode() {
		
		// Tests
		// Add with siblings
		Node newbie = new Node();
		assertEquals(nNodes[3].addChild(newbie), 3);
		assertEquals(nNodes[3], newbie.getParent()); // Check if parent is set
		assertEquals(newbie, nNodes[3].getChild(3)); // Check if parent has added
		assertEquals(3, newbie.getParentIndex());
		assertEquals(tree, newbie.getTree());
		
		// Add solo
		Node newbie2 = new Node();
		assertEquals(nNodes[2].addChild(newbie2), 0);
		assertEquals(nNodes[2], newbie2.getParent()); // Check if parent is set
		assertEquals(newbie2, nNodes[2].getChild(0)); // Check if parent has added
		assertEquals(0, newbie2.getParentIndex());
		assertEquals(tree, newbie2.getTree());
		
		// Add in cycle (RuntimeException)
		try {
			nNodes[1].addChild(newbie);
			fail("Child with parent could add itself");
		} catch (RuntimeException err) {
			// It works
		}
		
		
	}

	/**
	 * Test method for {@link org.mt4jext.mttree.Node#addChild(org.mt4jext.mttree.Node, int)}.
	 */
	public void testAddChildNodeInt() {

		// Tests
		// Add with siblings
		Node newbie = new Node();
		assertEquals(nNodes[3].addChild(newbie, 1), 1);
		assertEquals(nNodes[3], newbie.getParent()); // Check if parent is set
		assertEquals(newbie, nNodes[3].getChild(1));
		assertEquals(1, newbie.getParentIndex());
		assertEquals(tree, newbie.getTree());
		
		// Add solo
		Node newbie2 = new Node();
		assertEquals(nNodes[2].addChild(newbie2), 0);
		assertEquals(nNodes[2], newbie2.getParent()); // Check if parent is set
		assertEquals(newbie2, nNodes[2].getChild(0));
		assertEquals(0, newbie2.getParentIndex());
		assertEquals(tree, newbie2.getTree());
		
		// Add subtree
		Node[] subtree = {new Node(), new Node(), new Node()};
		subtree[0].addChild(subtree[1]);
		subtree[0].addChild(subtree[2]);
		nNodes[8].addChild(subtree[0]);
		assertEquals(tree, subtree[2].getTree());
		
		
		// Add with wrong index (IndexOutOfBoundsException)
		Node newbie3 = new Node();
		// Add in cycle (RuntimeException)
		try {
			nNodes[2].addChild(newbie3, 5);
			fail("Could add child at wrong index");
		} catch (IndexOutOfBoundsException err) {
			// Works
		}
		
		
		// Add in cycle (RuntimeException)
		try {
			nNodes[1].addChild(newbie, 1);
			fail("Child with parent could add itself");
		} catch (RuntimeException err) {
			
		}
		
		// Tree inheritance checking? Maybe later.
		
	}

	/**
	 * Test method for {@link org.mt4jext.mttree.Node#getChild(int)}.
	 */
	public void testGetChild() {
		// Simple to check
		
		// Get children we know
		assertEquals(nNodes[4], nNodes[1].getChild(0));
		assertEquals(nNodes[5], nNodes[1].getChild(1));
		assertEquals(nNodes[6], nNodes[1].getChild(2));
		
		// Get nonexistent child
		try {
			nNodes[2].getChild(0);
			fail("Managed to get nonexistent child");
		} catch (IndexOutOfBoundsException err) {
			
		}
		
	}

	/**
	 * Test method for {@link org.mt4jext.mttree.Node#getChildren()}.
	 */
	public void testGetChildren() {
		// Empty list
		Node[] empty = nNodes[2].getChildren();
		assertEquals(0, empty.length);
		
		// Populated list
		Node[] listed = nNodes[0].getChildren();
		assertEquals(3, listed.length);
		
		// Check if the children match the original
		for (int i = 0; i < 3; i++) {
			assertEquals(nNodes[0].getChild(i), listed[i]);
		}
		
	}

	/**
	 * Test method for {@link org.mt4jext.mttree.Node#getChildren(boolean)}.
	 */
	public void testGetChildrenBoolean() {
		// Hide a few nodes
		nNodes[4].hide();
		nNodes[6].hide();
		
		// First check for unhidden children
		// The shownodes thing is tested elsewhere
		Node[] single = nNodes[1].getChildren(false);
		assertEquals(1, single.length);
		assertEquals(nNodes[5], single[0]);
		
		// Make sure it matches its counterpart
		Node[] trued = nNodes[1].getChildren(true);
		Node[] orig = nNodes[1].getChildren();
		assertEquals(orig.length, trued.length);
		for (int i = 0; i < 3; i++) {
			assertEquals(orig[i], trued[i]);
		}
		
	}

	/**
	 * Test method for {@link org.mt4jext.mttree.Node#getParent()}.
	 */
	public void testGetParent() {
		// Check for known
		assertNull(nNodes[0].getParent());
		assertEquals(nNodes[0], nNodes[1].getParent());
		
	}

	/**
	 * Test method for {@link org.mt4jext.mttree.Node#getChildEdge(int)}.
	 */
	public void testGetChildEdge() {
		// Huh. Dunno how to test this here.
		
		// Test if not null, and if edge has the same ideas on parent/child
		Edge got = nNodes[3].getChildEdge(1);
		assertNotNull(got);
		assertEquals(nNodes[3], got.getParent());
		assertEquals(nNodes[8], got.getChild());
		
		// Test nonexistent edge
		try {
			nNodes[2].getChildEdge(1);
			fail("Could get nonexistent edge");
		} catch (IndexOutOfBoundsException err) {
			
		}
		
	}

	/**
	 * Test method for {@link org.mt4jext.mttree.Node#getEdge()}.
	 */
	public void testGetEdge() {
		assertEquals(nNodes[3].getChildEdge(1), nNodes[8].getEdge());
		
		try {
			nNodes[0].getEdge();
			fail("Could get nonexistent edge");
		} catch (RuntimeException err) {
			
		}
	}

	/**
	 * Test method for {@link org.mt4jext.mttree.Node#removeChild(int)}.
	 */
	public void testRemoveChild() {
		// Remove known child
		Node got = nNodes[1].removeChild(1);
		
		assertEquals(nNodes[5], got);
		assertFalse(got.isChild());
		assertNull(got.getTree());
		
		// Remove nonexistent child
		try {
			nNodes[1].removeChild(3);
			fail("Could 'remove' nonexistent child");
		} catch (IndexOutOfBoundsException err) {
		}
		
		
	}

	/**
	 * Test method for {@link org.mt4jext.mttree.Node#hide()}.
	 */
	public void testHide() {
		// There is very little to test
		nNodes[1].hide();
		assertFalse(nNodes[1].isShow());
		assertFalse(nNodes[5].isVisible());
		
	}

	/**
	 * Test method for {@link org.mt4jext.mttree.Node#show()}.
	 */
	public void testShow() {

		// There is very little to test
		nNodes[1].hide();
		nNodes[1].show();
		assertTrue(nNodes[1].isShow());
		assertTrue(nNodes[5].isVisible());
		
	}

	/**
	 * Test method for {@link org.mt4jext.mttree.Node#isChild()}.
	 */
	public void testIsChild() {
		// Simplest test ever
		assertTrue(nNodes[1].isChild());
		assertTrue(nNodes[8].isChild());
		assertFalse(nNodes[0].isChild());
	}

	/**
	 * Test method for {@link org.mt4jext.mttree.Node#partOfTree()}.
	 */
	public void testPartOfTree() {
		// Cannot yet test
		assertTrue(nNodes[2].partOfTree());
		assertTrue(nNodes[7].partOfTree());
		// Remove something
		nNodes[0].removeChild(2);
		assertFalse(nNodes[3].partOfTree());
		assertFalse(nNodes[9].partOfTree());
		
	}

	/**
	 * Test method for {@link org.mt4jext.mttree.Node#isVisible()}.
	 */
	public void testIsVisible() {
		// Test for a true case
		assertTrue(nNodes[4].isVisible());
		
		// Test for a false case
		nNodes[1].hide();
		assertFalse(nNodes[4].isVisible());
		
		
		// Loose node is always visible
		Node loner = new Node();
		assertTrue(loner.isVisible());
		
		// Unless it's hiding
		loner.hide();
		assertFalse(loner.isVisible());
		
	}

	/**
	 * Test method for {@link org.mt4jext.mttree.Node#isShow()}.
	 */
	public void testIsShow() {
		assertTrue(nNodes[1].isShow());
		nNodes[1].hide();
		assertFalse(nNodes[1].isShow());
	}

	/**
	 * Test method for {@link org.mt4jext.mttree.Node#countAllChildren(boolean)}.
	 */
	public void testCountAllChildren() {
		nNodes[5].hide();
		nNodes[7].hide();
		nNodes[3].hide();
		
		assertEquals(9 ,nNodes[0].countAllChildren(true)); // Nine children
		assertEquals(4, nNodes[0].countAllChildren(false)); // Can only see 5
		
		// But below a hidden one
		assertEquals(2, nNodes[3].countAllChildren(false));
		assertEquals(3, nNodes[3].countAllChildren(true));
		
		
	}

	/**
	 * Test method for {@link org.mt4jext.mttree.Node#countChildren(boolean)}.
	 */
	public void testCountChildren() {
		nNodes[5].hide();
		nNodes[7].hide();
		nNodes[3].hide();
		
		assertEquals(3, nNodes[0].countChildren(true));
		assertEquals(2, nNodes[0].countChildren(false));
		
		assertEquals(3, nNodes[3].countChildren(true));
		assertEquals(2, nNodes[3].countChildren(false));
	}

}
