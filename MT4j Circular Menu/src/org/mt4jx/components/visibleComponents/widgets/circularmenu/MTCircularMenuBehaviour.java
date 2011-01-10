package org.mt4jx.components.visibleComponents.widgets.circularmenu;

import java.util.ArrayList;

import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.IInputProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4jx.components.visibleComponents.widgets.circularmenu.actions.DefaultSegmentSelectionVisualisationAction;
import org.mt4jx.components.visibleComponents.widgets.circularmenu.actions.DefaultTriggerAction;

public class MTCircularMenuBehaviour {
	private Class<? extends IInputProcessor> segmentSelectionProcessor = DragProcessor.class;
	private IGestureEventListener[] segmentSelectionActions = new IGestureEventListener[]{new DefaultSegmentSelectionVisualisationAction()};
	
	private Class<? extends IInputProcessor> triggerProcessor = DragProcessor.class;
	private Class<? extends TriggerAction>[] triggerActionClasses = new Class[]{DefaultTriggerAction.class};
	
	public Class<? extends IInputProcessor> getSegmentSelectionProcessor() {
		return segmentSelectionProcessor;
	}
	public void setSegmentSelectionProcessor(Class<? extends IInputProcessor> segmentSelectionProcessor) {
		this.segmentSelectionProcessor = segmentSelectionProcessor;
	}
	public IGestureEventListener[] getSegmentSelectionActions() {
		return segmentSelectionActions;
	}
	public void setSegmentSelectionActions(
			IGestureEventListener[] segmentSelectionActions) {
		this.segmentSelectionActions = segmentSelectionActions;
	}
	public TriggerAction[] createTriggerActions(MTCircularMenuSegment segment) {
		ArrayList<TriggerAction> result = new ArrayList<TriggerAction>();
		for (int i = 0; i < this.triggerActionClasses.length; i++) {
			try {
				TriggerAction action = triggerActionClasses[i].newInstance();
				action.addActionListener(new DefaultScaleOutActionListener(segment));
				System.out.println("Added DefaultScaleOutActionListener.");
				result.add(action);
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		return result.toArray(new TriggerAction[result.size()]);
	}
	public void setTriggerActions(Class<? extends TriggerAction>[] triggerActionClasses) {
		this.triggerActionClasses = triggerActionClasses;
	}
	public Class<? extends IInputProcessor> getTriggerProcessor() {
		return triggerProcessor;
	}
	public void setTriggerProcessor(
			Class<? extends IInputProcessor> triggerProcessor) {
		this.triggerProcessor = triggerProcessor;
	}
}
