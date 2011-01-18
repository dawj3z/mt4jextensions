package org.mt4jx.components.visibleComponents.shapes.widgets.imageinfo;

import org.mt4j.components.visibleComponents.shapes.MTRoundRectangle;
import org.mt4jx.components.visibleComponents.layout.MTColumnLayout2D;
import org.mt4jx.util.MTColors;

import processing.core.PApplet;

public class Foo extends MTRoundRectangle {
	private MTColumnLayout2D rows;
	
	public Foo(PApplet pApplet){
		super(pApplet,0,0,0,400,300,12,12);
		this.setFillColor(MTColors.BLACK);
		this.setStrokeColor(MTColors.WHITE);
		this.rows = new MTColumnLayout2D(pApplet);
		this.addChild(rows);
	}
	
}
