package org.mt4jx.components.visibleComponents.widgets.pdf;

import java.io.File;
import java.io.IOException;

import org.mt4j.MTApplication;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleEvent;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleProcessor;

import processing.core.PApplet;
import processing.core.PImage;
//TODO: test
public class MTPDF extends MTRectangle {
	private File pdf;
	private PApplet pApplet;
	private int pageNumber;
	
	/**
	 * limit in pixels for pdf rendering result to avoid memory problems
	 */
	private int sizeLimitX=1280;
	private boolean autoUpdate = true;
	public MTPDF(PApplet pApplet, File pdf){
		this(pApplet, pdf, 1);
	}
	public MTPDF(PApplet pApplet, File pdf, int pageNumber){
		super(pApplet,0,0);
		this.pdf = pdf;
		this.pApplet = pApplet;
		this.pageNumber = pageNumber;
		
		PImage img= null;
		try {
			img = new PImage(PDFRenderer.loadImage(pdf, 1d, pageNumber));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		this.setWidthLocal(img.width);
		this.setHeightLocal(img.height);
		this.setTexture(img);
		
		final PApplet pa = pApplet;
		this.addGestureListener(ScaleProcessor.class, new IGestureEventListener() {
			@Override
			public boolean processGestureEvent(MTGestureEvent ge) {
				if(ge instanceof ScaleEvent && autoUpdate){
					switch (ge.getId()) {
					case ScaleEvent.GESTURE_ENDED:
						((MTApplication)pa).invokeLater(
								new Thread(){
								public void run(){
									updateTexture();
								}
							}
						);
						updateTexture();
						break;
					default:
						break;
					}
				}
				return false;
			}
		});
	}
	public boolean isAutoUpdate() {
		return autoUpdate;
	}
	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

	private synchronized void updateTexture(){
		// round to full pixels
		int width = Math.round(this.getWidthXYGlobal());
		int height = Math.round(this.getHeightXYGlobal());
		int textureWidth,textureHeight;
		if(width>this.sizeLimitX){
			double ratio = ((double)width)/((double)height);
			textureWidth = sizeLimitX;
			textureHeight = (int)Math.round((double)sizeLimitX*(double)ratio);
		}else{
			textureWidth = width;
			textureHeight = height;
		}
		// resize to full pixels
		setSizeXYGlobal(width, height);
		PImage img= null;
		try {
			// use double image size as texture (better while scaling up) ?
			img = new PImage(PDFRenderer.loadImage(pdf, textureWidth, textureHeight, this.pageNumber));
			setTexture(img);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
