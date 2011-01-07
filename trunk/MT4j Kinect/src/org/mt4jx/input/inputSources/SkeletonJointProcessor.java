package org.mt4jx.input.inputSources;

import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputProcessors.globalProcessors.AbstractGlobalInputProcessor;
import org.mt4jx.input.inputData.SkeletonJointEvent;

public class SkeletonJointProcessor extends AbstractGlobalInputProcessor {
	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.globalProcessors.AbstractGlobalInputProcessor#processInputEvtImpl(org.mt4j.input.inputData.MTInputEvent)
	 */
	@Override
	public void processInputEvtImpl(MTInputEvent inputEvent) {
		if (inputEvent instanceof SkeletonJointEvent){
			SkeletonJointEvent se = (SkeletonJointEvent)inputEvent;
			this.fireInputEvent(se);
		}
	}
}
