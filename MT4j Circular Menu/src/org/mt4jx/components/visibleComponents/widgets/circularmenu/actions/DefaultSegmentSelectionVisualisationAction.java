/***********************************************************************
 *   MT4j Extension: MTCircularMenu
 *   
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License (LGPL)
 *   as published by the Free Software Foundation, either version 3
 *   of the License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the LGPL
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 ***********************************************************************/
package org.mt4jx.components.visibleComponents.widgets.circularmenu.actions;

import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4jx.components.visibleComponents.widgets.circularmenu.MTCircularMenuSegment;
/**
 * @author Uwe Laufs
 *
 */
public class DefaultSegmentSelectionVisualisationAction implements IGestureEventListener {
	@Override
	public boolean processGestureEvent(MTGestureEvent ge) {
		IMTComponent3D eventTarget = ge.getTarget();
		if(eventTarget instanceof MTCircularMenuSegment){
			MTCircularMenuSegment segment = (MTCircularMenuSegment)eventTarget;
			switch (ge.getId()) {
			case MTGestureEvent.GESTURE_STARTED:
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
