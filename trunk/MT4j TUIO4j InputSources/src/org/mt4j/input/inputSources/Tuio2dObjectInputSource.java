package org.mt4j.input.inputSources;

import java.util.HashMap;
import java.util.Map;

import org.mt4j.MTApplication;
import org.mt4j.input.inputData.ActiveCursorPool;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputData.MTFiducialInputEvt;
import org.mt4j.input.inputData.MTFingerInputEvt;
import org.mt4j.util.MT4jSettings;
import org.tuio4j.TuioClient;
import org.tuio4j.TuioClientListener;
import org.tuio4j.TuioEvent;
import org.tuio4j.profile.obj2d.Tuio2DObjectEvent;
/**
 * See license.txt for license information.
 * @author Uwe Laufs
 * @version 1.0
 */
public class Tuio2dObjectInputSource extends AbstractInputSource implements TuioClientListener {
	/** this is needed to track which events got fired as a finger down event already. */
	private Map<Long, Long> tuioIDToCursorID = new HashMap<Long, Long>();;
	private TuioClient client;
	public Tuio2dObjectInputSource(MTApplication mtApp){
		super(mtApp);
		this.client = TUIOClientManager.getInstance().getClient();
	}
	
	@Override
	public void onRegistered() {
		super.onRegistered();
		try {
			this.client.connect();
			this.client.addListener(this);
			System.out.println("TUIO/2dObject connected (port " + client.getPortNumber() + ")");
		} catch (Throwable e) {
			System.out.println("TUIO/2dObject not connected: " + e.getMessage());
		}
	}

	@Override
	public void onUnregistered() {
		super.onUnregistered();
		this.client.disconnect();
		this.client.removeListener(this);
	}
	
	
	@Override
	public void eventReceived(TuioEvent tuioEvent) {
		if(tuioEvent instanceof Tuio2DObjectEvent){
			Tuio2DObjectEvent obj2DEvt = (Tuio2DObjectEvent)tuioEvent;
			float absoluteX = obj2DEvt.getXRel() * MT4jSettings.getInstance().getWindowWidth();
			float abosulteY = obj2DEvt.getYRel() * MT4jSettings.getInstance().getWindowHeight();
			
			long sessionID = obj2DEvt.getSessionId();
//			System.out.println("inputsource received TuioEvent: " + tuioEvent);
			switch (tuioEvent.getEventTypeId()) {
				case TuioEvent.SESSION_DETECTED:
				{
					InputCursor c = new InputCursor();
					MTFiducialInputEvt objEvt = new MTFiducialInputEvt(
							this,
							absoluteX,
							abosulteY,
							MTFingerInputEvt.INPUT_STARTED,
							c,
							obj2DEvt.getMarkerId(),
							obj2DEvt.getAngleRadians(),
							obj2DEvt.getXVelocity(),
							obj2DEvt.getYVelocity(),
							obj2DEvt.getRotationVelocityVector(),
							obj2DEvt.getMotionAcceleration(),
							obj2DEvt.getRotationAcceleration()
					);
					
					long cursorID = c.getId();
					ActiveCursorPool.getInstance().putActiveCursor(cursorID, c);
					tuioIDToCursorID.put(sessionID, cursorID);
//					System.out.println("enque DETECT cid:" + cursorID + " (" + absoluteX + "," + abosulteY + ")");
					this.enqueueInputEvent(objEvt);
				}
					break;
				case TuioEvent.SESSION_UPDATED:
				{	
					Long tuioID = tuioIDToCursorID.get(sessionID);
					if (tuioID != null){
						InputCursor c = ActiveCursorPool.getInstance().getActiveCursorByID(tuioID);
						if (c != null){
							MTFiducialInputEvt objEvt = new MTFiducialInputEvt(
									this,
									absoluteX,
									abosulteY,
									MTFingerInputEvt.INPUT_UPDATED,
									c,
									obj2DEvt.getMarkerId(),
									obj2DEvt.getAngleRadians(),
									obj2DEvt.getXVelocity(),
									obj2DEvt.getYVelocity(),
									obj2DEvt.getRotationVelocityVector(),
									obj2DEvt.getMotionAcceleration(),
									obj2DEvt.getRotationAcceleration()
							);
//							System.out.println("enque UPDATE cid:" + c.getId() + " (" + absoluteX + "," + abosulteY + ")");
							this.enqueueInputEvent(objEvt);
						}else{
							// error
						}
					}
				}
					break;
				case TuioEvent.SESSION_ENDED:
				{
//					logger.info("TUIO INPUT REMOVE FINGER - TUIO ID: " + sessionID);
					Long lCursorID = tuioIDToCursorID.get(sessionID);
					if (lCursorID != null){
						long cursorID = lCursorID;
						InputCursor c = ActiveCursorPool.getInstance().getActiveCursorByID(cursorID);
						if (c != null){
							MTFiducialInputEvt objEvt = new MTFiducialInputEvt(
									this,
									absoluteX,
									abosulteY,
									MTFingerInputEvt.INPUT_ENDED,
									c,
									obj2DEvt.getMarkerId(),
									obj2DEvt.getAngleRadians(),
									obj2DEvt.getXVelocity(),
									obj2DEvt.getYVelocity(),
									obj2DEvt.getRotationVelocityVector(),
									obj2DEvt.getMotionAcceleration(),
									obj2DEvt.getRotationAcceleration()
							);
							tuioIDToCursorID.remove(sessionID);
							ActiveCursorPool.getInstance().removeCursor(cursorID);
//							System.out.println("enque END cid:" + cursorID + " (" + absoluteX + "," + abosulteY + ")");
							this.enqueueInputEvent(objEvt);
						}else{
							tuioIDToCursorID.remove(sessionID);
						}
					}else{
						// error
					}
				}
					break;
				default:
					break;
			}
		}
	}
}
