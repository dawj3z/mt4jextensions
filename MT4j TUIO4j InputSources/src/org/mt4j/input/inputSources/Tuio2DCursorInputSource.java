package org.mt4j.input.inputSources;

import java.util.HashMap;
import java.util.Map;

import org.mt4j.AbstractMTApplication;
import org.mt4j.MTApplication;
import org.mt4j.input.inputData.ActiveCursorPool;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputData.MTFingerInputEvt;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.logging.ILogger;
import org.mt4j.util.logging.MTLoggerFactory;
import org.tuio4j.TuioClient;
import org.tuio4j.TuioClientListener;
import org.tuio4j.TuioEvent;
import org.tuio4j.profile.cursor2d.Tuio2DCursorEvent;
/**
 * See license.txt for license information.
 * @author Uwe Laufs
 * @version 1.0
 */
public class Tuio2DCursorInputSource extends AbstractInputSource implements TuioClientListener {
	/** this is needed to track which events got fired as a finger down event already. */
	private Map<Long, Long> tuioIDToCursorID = new HashMap<Long, Long>();
	private TuioClient client;
	private static final ILogger log = MTLoggerFactory.getLogger(Tuio2DCursorInputSource.class.getName());
	public Tuio2DCursorInputSource(AbstractMTApplication mtApp){
		super(mtApp);
		this.client = TUIOClientManager.getInstance().getClient();
		
	}
	@Override
	public void onRegistered() {
		super.onRegistered();
		try{
			this.client.connect();
			this.client.addListener(this);
			log.info("TUIO/2DCursor connected (port " + client.getPortNumber() + ")");
		} catch (Throwable e) {
			log.info("TUIO/2DCursor not connected: " + e.getMessage());
		}
	}

	@Override
	public void onUnregistered() {
		super.onUnregistered();
		if(this.client!=null){
		// do not disconnect client (maybe change later)
//			try {
				this.client.removeListener(this);
				log.info("TUIO/2DCursor disconnected");
//				if(this.client.hasListeners()){
//					this.client.disconnect();
//				}
//			} catch (Throwable e) {
//				// best effort
//			}
		}
	}
	
	
	@Override
	public void eventReceived(TuioEvent tuioEvent) {
		if(tuioEvent instanceof Tuio2DCursorEvent){
			Tuio2DCursorEvent cur2DEvt = (Tuio2DCursorEvent)tuioEvent;
			float absoluteX = cur2DEvt.getXRel() * MT4jSettings.getInstance().getWindowWidth();
			float abosulteY = cur2DEvt.getYRel() * MT4jSettings.getInstance().getWindowHeight();
			
			long sessionID = cur2DEvt.getSessionId();
//			System.out.println("inputsource received TuioEvent: " + tuioEvent);
			switch (tuioEvent.getEventTypeId()) {
				case TuioEvent.SESSION_DETECTED:
				{
					InputCursor c = new InputCursor();
					MTFingerInputEvt touchEvt = new MTFingerInputEvt(this, absoluteX, abosulteY, MTFingerInputEvt.INPUT_STARTED, c);
					long cursorID = c.getId();
					ActiveCursorPool.getInstance().putActiveCursor(cursorID, c);
					tuioIDToCursorID.put(sessionID, cursorID);
//					System.out.println("enque DETECT cid:" + cursorID + " (" + absoluteX + "," + abosulteY + ")");
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
//							System.out.println("enque UPDATE cid:" + c.getId() + " (" + absoluteX + "," + abosulteY + ")");
							this.enqueueInputEvent(touchEvt);
						}else{
//							logger.error("CURSOR NOT IN ACTIVE CURSOR LIST! TUIO ID: " + cursor.getSessionID());
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
							MTFingerInputEvt te = new MTFingerInputEvt(	this,
																		absoluteX,
																		abosulteY,
																		MTFingerInputEvt.INPUT_ENDED,
																		c);
							tuioIDToCursorID.remove(sessionID);
							ActiveCursorPool.getInstance().removeCursor(cursorID);
//							System.out.println("enque END cid:" + cursorID + " (" + absoluteX + "," + abosulteY + ")");
							this.enqueueInputEvent(te);
						}else{
//							logger.info("ERROR WHEN REMOVING FINGER - TUIO ID: " + cursor.getSessionID() + " - Cursor not in active cursor pool!");
							tuioIDToCursorID.remove(sessionID);
						}
					}else{
//						logger.info("ERROR WHEN REMOVING FINGER - TUIO ID: " + cursor.getSessionID() + " - Cursor not in tuioIDMap! - probably removed before an update event got fired!");
					}
				}
					break;
				default:
					break;
			}
		}
	}
}
