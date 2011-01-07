package org.mt4jx.components.visibleComponents.widgets.circularmenu;

import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;

public class DefaultSegmentSelectionVisualisationAction implements IGestureEventListener {
	@Override
	public boolean processGestureEvent(MTGestureEvent ge) {
		IMTComponent3D eventTarget = ge.getTargetComponent();
		if(eventTarget instanceof MTCircularMenuSegment){
			MTCircularMenuSegment segment = (MTCircularMenuSegment)eventTarget;
			switch (ge.getId()) {
			case MTGestureEvent.GESTURE_DETECTED:
				segment.segmentDown();
				break;
			case MTGestureEvent.GESTURE_ENDED:
				segment.segmentUp();
				break;
			default:
				break;
			}
		}
		System.out.println(ge);
		return false;
	}
}
