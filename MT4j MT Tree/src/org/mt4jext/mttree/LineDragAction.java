package org.mt4jext.mttree;

import org.mt4j.input.gestureAction.DefaultDragAction;
import org.mt4j.input.inputProcessors.MTGestureEvent;

/**
 * @author Ewoud van Pareren
 * 
 * A custom drag listener for nodes.
 * 
 * This version updates the lines between the dragged Node and its
 * parent/children.
 * 
 * Should not be used directly.
 * However, if an application requires it, it may be subclassed.
 */
public class LineDragAction extends DefaultDragAction {
	
	protected Node associatedNode;
	
	protected LineDragAction(Node in) {
		associatedNode = in;
	}
	
	@Override
	public boolean processGestureEvent(MTGestureEvent arg0) {
		boolean returnThis = super.processGestureEvent(arg0);
		if (associatedNode.isChild())
			if (associatedNode.getEdge() != null) associatedNode.getEdge().updateLines();
		
		for (Node got : associatedNode.getChildren(false))
			if (got.getEdge() != null) got.getEdge().updateLines();
		
		
		return returnThis;
	}

}
