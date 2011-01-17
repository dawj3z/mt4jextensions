package org.mt4jx.components.visibleComponents.widgets.toolbar;

import org.mt4j.components.MTComponent;
import org.mt4j.components.interfaces.IclickableButton;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;

/**
 * @author Alexander Phleps
 *
 */
public class MTToolbarButtonClickAction implements IGestureEventListener {
	private AbstractShape polyButton;

	public MTToolbarButtonClickAction(AbstractShape poly) {
		this.polyButton = poly;
	}

	@Override
	public boolean processGestureEvent(MTGestureEvent g) {
		if (g instanceof TapEvent) {
			TapEvent clickEvent = (TapEvent) g;

			if (g.getTarget() instanceof MTComponent) {
				MTComponent comp = (MTComponent) g.getTarget();
				switch (clickEvent.getId()) {
				case MTGestureEvent.GESTURE_STARTED:
					if (((TapEvent) g).getTapID() == TapEvent.TAP_DOWN) {
						if (comp instanceof IclickableButton) {
							IclickableButton polyButton = (IclickableButton) g.getTarget();
							polyButton.fireActionPerformed((TapEvent) g);
						}

//						System.out.println("1");
//						try {
//						((MTToolbar2Button)comp).animate();
//						} catch(Exception e) {
//							//e.printStackTrace();							
//						}
						
						//hide any menu when invoking action on any other button
						//TODO: geht nicht wenn ListItem wegen class cast exception, außerdem nicht schön auf diese weise
						try {
						if(MTLayoutContainer.visibleOne != null && ((MTToolbarButton)polyButton).getListMenu() == null)
							MTLayoutContainer.visibleOne.setVisible(false);
						} catch(Exception e) {}						
					}

					break;
				case MTGestureEvent.GESTURE_UPDATED: // NOTE: usually click
					// gesture analyzers
					// don't send gesture
					// update events
					if (((TapEvent) g).getTapID() == TapEvent.TAP_DOWN) {
						if (comp instanceof IclickableButton) {
							IclickableButton polyButton = (IclickableButton) g
									.getTarget();
							polyButton.fireActionPerformed((TapEvent) g);
						}
//						System.out.println("2");
					}

					break;
				case MTGestureEvent.GESTURE_ENDED:
					if (((TapEvent) g).getTapID() == TapEvent.TAPPED
							|| ((TapEvent) g).getTapID() == TapEvent.TAP_UP) {

						if (comp instanceof IclickableButton) {
							IclickableButton polyButton = (IclickableButton) g
									.getTarget();
							polyButton.fireActionPerformed((TapEvent) g);
						}
//						System.out.println("3");				
					}
					break;
				default:
					break;
				}

			}
		}
		return false;
	}

}
