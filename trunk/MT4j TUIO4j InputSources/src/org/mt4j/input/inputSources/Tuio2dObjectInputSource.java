package org.mt4j.input.inputSources;

import java.util.HashMap;
import java.util.Map;

import org.mt4j.MTApplication;
import org.mt4j.input.inputData.ActiveCursorPool;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputData.MTFingerInputEvt;
import org.mt4j.util.MT4jSettings;
import org.tuio4j.TuioClient;
import org.tuio4j.TuioClientListener;
import org.tuio4j.TuioEvent;
import org.tuio4j.cursor2d.Tuio2DCursorEvent;
//TODO: Complete and test
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
		this.client = new TuioClient(3333);
		this.client.connect();
		this.client.addListener(this);
	}
	@Override
	public void eventRecieved(TuioEvent tuioEvent) {
		if(tuioEvent instanceof Tuio2DCursorEvent){
			Tuio2DCursorEvent cur2DEvt = (Tuio2DCursorEvent)tuioEvent;
			float absoluteX = cur2DEvt.getXRel() * MT4jSettings.getInstance().getWindowWidth();
			float abosulteY = cur2DEvt.getYRel() * MT4jSettings.getInstance().getWindowHeight();
			
			long sessionID = cur2DEvt.getSessionId();
			System.out.println("inputsource received TuioEvent: " + tuioEvent);
			switch (tuioEvent.getEventTypeId()) {
				case TuioEvent.SESSION_DETECTED:
				{
					InputCursor c = new InputCursor();
					MTFingerInputEvt touchEvt = new MTFingerInputEvt(this, absoluteX, abosulteY, MTFingerInputEvt.INPUT_DETECTED, c);
					long cursorID = c.getId();
					ActiveCursorPool.getInstance().putActiveCursor(cursorID, c);
					tuioIDToCursorID.put(sessionID, cursorID);
					System.out.println("enque DETECT cid:" + cursorID);
					this.enqueueInputEvent(touchEvt);
				}
					break;
				case TuioEvent.SESSION_UPDATED:
				{	
					Long tuioID = tuioIDToCursorID.get(sessionID);
					if (tuioID != null){
//						logger.info("TUIO INPUT UPDATE FINGER - TUIO ID: " + sessionID);
						InputCursor c = ActiveCursorPool.getInstance().getActiveCursorByID(tuioID);
						if (c != null){
							MTFingerInputEvt touchEvt = new MTFingerInputEvt(this, absoluteX, abosulteY, MTFingerInputEvt.INPUT_UPDATED, c);
							System.out.println("enque UPDATE cid:" + c.getId());
							this.enqueueInputEvent(touchEvt);
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
							MTFingerInputEvt te = new MTFingerInputEvt(this, absoluteX, abosulteY, MTFingerInputEvt.INPUT_ENDED, c);
							tuioIDToCursorID.remove(sessionID);
							ActiveCursorPool.getInstance().removeCursor(cursorID);
							System.out.println("enque END cid:" + cursorID);
							this.enqueueInputEvent(te);
						}else{
//							logger.info("ERROR WHEN REMOVING FINGER - TUIO ID: " + cursor.getSessionID() + " - Cursor not in active cursor pool!");
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
