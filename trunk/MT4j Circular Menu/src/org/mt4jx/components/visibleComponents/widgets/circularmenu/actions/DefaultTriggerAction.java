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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4jx.components.visibleComponents.widgets.circularmenu.MTCircularMenuSegment;
import org.mt4jx.components.visibleComponents.widgets.circularmenu.TriggerAction;
/**
 * @author Uwe Laufs
 *
 */
public class DefaultTriggerAction implements TriggerAction {
	private ArrayList<ActionListener> actionListeners = new ArrayList<ActionListener>();
	
	@Override
	public boolean processGestureEvent(MTGestureEvent ge) {
		System.out.println("TriggerAction: processGestureEvent invoked.");
		IMTComponent3D eventTarget = ge.getTarget();
		System.out.println("EventTaget class: " + eventTarget.getClass().getName());
		if(eventTarget instanceof MTCircularMenuSegment){
			System.out.println("TriggerAction handled for TargetComponent: " + eventTarget.getClass().getName());
//			MTCircularMenuSegment segment = (MTCircularMenuSegment)eventTarget;
			System.out.println("Event type: " + ge.getClass().getName());
			switch (ge.getId()) {
			case MTGestureEvent.GESTURE_ENDED:
				ActionEvent ae = new ActionEvent(ge.getTarget(), ActionEvent.ACTION_PERFORMED, "action performed on ellipse segment.");
				System.out.println("# of actionlisteners to be called: " +  actionListeners.size());
				for (int i = 0; i < actionListeners.size(); i++) {
					System.out.println("SEND ACTION EVENT");
					this.actionListeners.get(i).actionPerformed(ae);
				}
			default:
				break;
			}
		}else{
			System.out.println("TriggerAction ignored TargetComponent: " + eventTarget.getClass().getName());
		}
		return false;
	}
	public void addActionListener(ActionListener al){
		System.out.println("ADDED ActionListener to TriggerAction: " + al.getClass().getName());
		if(!this.actionListeners.contains(al)){
			this.actionListeners.add(al);
		}
	}
	public void removeActionListener(ActionListener al){
		if(al!=null&&this.actionListeners.contains(al)){
			this.actionListeners.remove(al);
		}
	}
	@Override
	public void removeAllActionListeners() {
		this.actionListeners.clear();
	}
	
}
