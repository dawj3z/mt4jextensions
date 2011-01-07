package org.mt4jx.components.visibleComponents.widgets.circularmenu;

import java.awt.event.ActionListener;

import org.mt4j.input.inputProcessors.IGestureEventListener;

public interface TriggerAction extends IGestureEventListener {
	public void addActionListener(ActionListener targetListener);
	public void removeActionListener(ActionListener al);
	public void removeAllActionListeners();
}
