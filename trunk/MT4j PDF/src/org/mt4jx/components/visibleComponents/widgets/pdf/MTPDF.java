package org.mt4jx.components.visibleComponents.widgets.pdf;

import java.io.File;
import java.io.IOException;

import org.mt4j.components.visibleComponents.shapes.MTRectangle;

import processing.core.PApplet;
import processing.core.PImage;
//TODO: test
public class MTPDF extends MTRectangle {
	public MTPDF(PApplet pApplet, File pdf){
		super(pApplet,0,0);
		PImage img= null;
		try {
			img = new PImage(PDFRenderer.loadImage(pdf, 1d, 1));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		this.setWidthLocal(img.width);
		this.setHeightLocal(img.height);
		this.setTexture(img);
	}
}
