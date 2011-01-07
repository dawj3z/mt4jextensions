package org.mt4jx.components.visibleComponents.widgets.circularmenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.input.inputProcessors.MTGestureEvent;

public class DefaultTriggerAction implements TriggerAction {
	private ArrayList<ActionListener> actionListeners = new ArrayList<ActionListener>();
	
	@Override
	public boolean processGestureEvent(MTGestureEvent ge) {
		System.out.println("TriggerAction: processGestureEvent invoked.");
		IMTComponent3D eventTarget = ge.getTargetComponent();
		System.out.println("EventTaget class: " + eventTarget.getClass().getName());
		if(eventTarget instanceof MTCircularMenuSegment){
			System.out.println("TriggerAction handled for TargetComponent: " + eventTarget.getClass().getName());
			MTCircularMenuSegment segment = (MTCircularMenuSegment)eventTarget;
			System.out.println("Event type: " + ge.getClass().getName());
			switch (ge.getId()) {
			case MTGestureEvent.GESTURE_ENDED:
				ActionEvent ae = new ActionEvent(ge.getTargetComponent(), ActionEvent.ACTION_PERFORMED, "action performed on ellipse segment.");
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
