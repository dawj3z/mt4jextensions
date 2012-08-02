package org.mt4jext.mttree;


import junit.framework.Test;
import junit.framework.TestSuite;

public class TreeTestingSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite(TreeTestingSuite.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(NodeTest.class);
		suite.addTestSuite(TreeTest.class);
		suite.addTestSuite(CollapsibleNodeTest.class);
		//$JUnit-END$
		return suite;
	}

}
