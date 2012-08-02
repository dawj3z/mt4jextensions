package org.mt4jext.mttree;

import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;

/**
 * @author Ewoud van Pareren
 * 
 * An event listener used by CollapsibleNode to listen for a double tap.
 *
 */
public class DoubleTapCollapse implements IGestureEventListener {
	CollapsibleNode node;
	
	long lastStart = 0;
	long nextStart = 0;
	int doubleThreshold = 400;
	
	/**
	 * Generate a double tap collapse listener.
	 * 
	 * @param in the CollapsibleNode to attach to the listener
	 */
	public DoubleTapCollapse(CollapsibleNode in) {
		node = in;
	}
	
	@Override
	public boolean processGestureEvent(MTGestureEvent evt) {
		if (evt.getId() == MTGestureEvent.GESTURE_DETECTED) {
			// Tap started
			lastStart = nextStart;
			nextStart = System.currentTimeMillis();
			
		} else if (evt.getId() == MTGestureEvent.GESTURE_ENDED) {
			long curMills = System.currentTimeMillis();
			if (curMills-lastStart < doubleThreshold) {
				nextStart = 0;
				lastStart = 0;
				
				// Count the amount of shown/hidden children
				if (node.isCollapsed()) node.expand();
				else node.collapse();
				
			}
		}
		return false;
	}

}
