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
package org.mt4jx.components.visibleComponents.widgets.circularmenu;

import java.util.ArrayList;

import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.IInputProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4jx.components.visibleComponents.widgets.circularmenu.actions.DefaultSegmentSelectionVisualisationAction;
import org.mt4jx.components.visibleComponents.widgets.circularmenu.actions.DefaultTriggerAction;

/**
 * @author Uwe Laufs
 *
 */
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
