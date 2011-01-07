package org.mt4j.sound;

import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;

public class Mp3GestureAction implements IGestureEventListener {
	public static final int GESTURE_DETECTED = MTGestureEvent.GESTURE_DETECTED;
	public static final int GESTURE_ENDED = MTGestureEvent.GESTURE_ENDED;
	private int triggerType;
	private String audioFilePath;
	
	public Mp3GestureAction(int triggerType, String audioFile){
		this.triggerType = triggerType;
	}
	public Mp3GestureAction(String audioFilePath){
		this.triggerType = GESTURE_DETECTED;
		this.audioFilePath = audioFilePath;
	}
	@Override
	public boolean processGestureEvent(MTGestureEvent ge) {
		switch (ge.getId()) {
		case MTGestureEvent.GESTURE_DETECTED:
			if(this.triggerType==MTGestureEvent.GESTURE_DETECTED){
				Mp3Player.play(this.audioFilePath);
			}
			break;
//		case ge.GESTURE_UPDATED:
//			break;
		case MTGestureEvent.GESTURE_ENDED:
			if(this.triggerType==MTGestureEvent.GESTURE_ENDED){
				Mp3Player.play(this.audioFilePath);
			}
			break;
		default:
			break;
		}
		return false;
	}

}
