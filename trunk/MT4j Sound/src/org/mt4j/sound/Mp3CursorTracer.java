package org.mt4j.sound;

import org.mt4j.input.inputData.MTFingerInputEvt;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputProcessors.globalProcessors.AbstractGlobalInputProcessor;

public class Mp3CursorTracer extends AbstractGlobalInputProcessor {
	private String audioFilePath;
	public static int INPUT_DETECTED = MTFingerInputEvt.INPUT_DETECTED;
	public static int INPUT_ENDED = MTFingerInputEvt.INPUT_ENDED;
	private int mode = INPUT_DETECTED;
	public Mp3CursorTracer(String audioFilePath, int mode){
		this.mode = mode;
		this.audioFilePath = audioFilePath;
	}
	public Mp3CursorTracer(String audioFilePath){
		this(audioFilePath, INPUT_DETECTED);
	}
	@Override
	public void processInputEvtImpl(MTInputEvent inputEvent) {
		if(inputEvent instanceof MTFingerInputEvt){
			MTFingerInputEvt fie = (MTFingerInputEvt)inputEvent;
			switch (fie.getId()) {
			case MTFingerInputEvt.INPUT_DETECTED:
				if(this.mode==INPUT_DETECTED){
					Mp3Player.play(this.audioFilePath);
				}
				
				break;
			case MTFingerInputEvt.INPUT_ENDED:
				if(this.mode==INPUT_ENDED){
					Mp3Player.play(this.audioFilePath);
				}
			default:
				break;
			}
		}
	}
}
