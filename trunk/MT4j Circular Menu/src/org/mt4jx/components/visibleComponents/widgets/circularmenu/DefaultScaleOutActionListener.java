package org.mt4jx.components.visibleComponents.widgets.circularmenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DefaultScaleOutActionListener implements ActionListener {
	private MTCircularMenuSegment segment;
	public DefaultScaleOutActionListener(MTCircularMenuSegment segment){
		System.out.println("DefaultScaleOutActionListener created");
		this.segment = segment;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("DefaultScaleOutActionListener.actionPerformed called.");
		System.out.println("segment.hasChildren(): " + this.segment.hasChildren() );
		if(!this.segment.hasChildren()){
			AnimationUtil.scaleOut(this.segment.getParentMenu(), false);
		}
	}
	
}
