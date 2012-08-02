/**
 * 
 */
package org.mt4jext.mttree;

import org.mt4jext.mttree.CollapsibleNode;
import org.mt4jext.mttree.Tree;

import junit.framework.TestCase;

/**
 * @author ewoud
 *
 */
public class CollapsibleNodeTest extends TestCase {

	// Node stuff
	CollapsibleNode root;
	
	// Labeled subnodes
	CollapsibleNode[] nodes;
	
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
		root = new CollapsibleNode();
		nodes = new CollapsibleNode[10]; // Zero is root
		nodes[0] = root;
		for (int i = 1; i < 10; i++) {
			nodes[i] = new CollapsibleNode();
		}
		root.addChild(nodes[1]);
		root.addChild(nodes[2]);
		root.addChild(nodes[3]);
		nodes[1].addChild(nodes[4]);
		nodes[1].addChild(nodes[5]);
		nodes[1].addChild(nodes[6]);
		
		nodes[3].addChild(nodes[7]);
		nodes[3].addChild(nodes[8]);
		nodes[3].addChild(nodes[9]);
		
		
		// Attached at first
		tree = new Tree();
		tree.setRoot(nodes[0]);
		
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		root = null;
		nodes = null;
	}

	/**
	 * Test method for {@link org.mt4jext.mttree.CollapsibleNode#addChild(org.mt4jext.mttree.Node)}.
	 */
	public void testAddChildNode() {
		// Add to noncollapsed node
		CollapsibleNode testChild = new CollapsibleNode();
		nodes[2].addChild(testChild);
		assertFalse(testChild.isHiddenByCollapse());
		
		// Add to collapsed node
		CollapsibleNode testChild2 = new CollapsibleNode();
		nodes[2].collapse();
		nodes[2].addChild(testChild2);
		assertTrue(testChild2.isHiddenByCollapse());
		
	}

	/**
	 * Test method for {@link org.mt4jext.mttree.CollapsibleNode#addChild(org.mt4jext.mttree.Node, int)}.
	 */
	public void testAddChildNodeInt() {
		// Similar to above
		
		// Add to noncollapsed node
		CollapsibleNode testChild = new CollapsibleNode();
		nodes[1].addChild(testChild, 1);
		assertFalse(testChild.isHiddenByCollapse());
		
		// Add to collapsed node
		CollapsibleNode testChild2 = new CollapsibleNode();
		nodes[1].collapse();
		nodes[1].addChild(testChild2, 1);
		assertTrue(testChild2.isHiddenByCollapse());
	}

	/**
	 * Test method for {@link org.mt4jext.mttree.CollapsibleNode#removeChild(int)}.
	 */
	public void testRemoveChild() {
		// Remove from uncollapsed
		CollapsibleNode release = (CollapsibleNode)nodes[3].removeChild(1);
		assertFalse(release.isHiddenByCollapse());
		
		// Collapse and remove
		nodes[1].collapse();
		release = (CollapsibleNode)nodes[1].removeChild(1);
		assertFalse(release.isHiddenByCollapse());
		
		// Remove collapsed and check
		nodes[0].collapse();
		release = (CollapsibleNode)nodes[0].removeChild(2);
		assertFalse(release.isCollapsed());
		assertTrue(release.isShow());
		assertFalse(release.isHiddenByCollapse());
		
	}

	/**
	 * Test method for {@link org.mt4jext.mttree.CollapsibleNode#hide()}.
	 */
	public void testHide() {
		// Hide uncollapsed
		nodes[4].hide();
		assertFalse(nodes[4].isHiddenByCollapse());
		assertFalse(nodes[4].isShow());
		
		// Hide collapsed
		nodes[1].collapse();
		assertFalse(nodes[4].isHiddenByCollapse());
		assertTrue(nodes[5].isHiddenByCollapse());
		
		// Hide while collapsing
		nodes[5].hide();
		assertFalse(nodes[5].isHiddenByCollapse());
		
	}

	/**
	 * Test method for {@link org.mt4jext.mttree.CollapsibleNode#show()}.
	 */
	public void testShow() {
		// Hide and show uncollapsed
		nodes[4].hide();
		assertFalse(nodes[4].isHiddenByCollapse());
		nodes[4].show();
		assertTrue(nodes[4].isShow());
		assertFalse(nodes[4].isHiddenByCollapse());
		
		// Hide and show collapsed
		nodes[5].hide();
		nodes[1].collapse();
		assertFalse(nodes[5].isHiddenByCollapse());
		nodes[5].show();
		assertFalse(nodes[5].isShow());
		assertTrue(nodes[5].isHiddenByCollapse());
		
	}
	
	/**
	 * Test method for {@link org.mt4jext.mttree.CollapsibleNode#isCollapsed()}.
	 */
	public void testIsCollapsed() {
		// Collapse, then test
		assertFalse(nodes[1].isCollapsed());
		
		nodes[1].collapse();
		assertTrue(nodes[1].isCollapsed());
		
		// Not much else to say here
	}

	/**
	 * Test method for {@link org.mt4jext.mttree.CollapsibleNode#isHiddenByCollapse()}.
	 */
	public void testIsHiddenByCollapse() {
		assertFalse(nodes[5].isHiddenByCollapse());
		nodes[1].collapse();
		assertTrue(nodes[5].isHiddenByCollapse());
		
	}

	/**
	 * Test method for {@link org.mt4jext.mttree.CollapsibleNode#collapse()}.
	 */
	public void testCollapse() {
		
		nodes[4].hide();
		nodes[1].collapse();
		
		for (int i = 4; i < 7; i++)
			assertFalse(nodes[i].isShow());

		assertFalse(nodes[4].isHiddenByCollapse());
		assertTrue(nodes[5].isHiddenByCollapse());
		assertTrue(nodes[6].isHiddenByCollapse());
		
		
	}

	/**
	 * Test method for {@link org.mt4jext.mttree.CollapsibleNode#expand()}.
	 */
	public void testExpand() {
		nodes[4].hide();
		nodes[6].hide();
		nodes[1].collapse();
		
		nodes[1].expand();
		
		for (int i = 4; i < 7; i++)
			assertFalse(nodes[i].isHiddenByCollapse());
		
		assertTrue(nodes[5].isShow());
		assertFalse(nodes[4].isShow());
		assertFalse(nodes[6].isShow());
		
		
	}

}
