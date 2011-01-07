package org.mt4jx.components.visibleComponents.shapes.widgets.imageinfo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.font.FontManager;
import org.mt4j.components.visibleComponents.font.IFont;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.widgets.MTImage;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.components.visibleComponents.widgets.buttons.MTImageButton;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;
import org.mt4jx.components.visibleComponents.widgets.circularmenu.AnimationUtil;

import processing.core.PApplet;
import processing.core.PImage;

public class MTInfoPanel extends MTRectangle {
	private IFont font;
	private IFont font2;
	private float strokeWeight = 2.5f;
	private MTColor textColor = new MTColor(255,255,255);
	private MTColor fillColor = new MTColor(64,64,64, 192);
	private MTColor strokeColor = new MTColor(255,255,255, 128);
	
	public MTInfoPanel(AbstractShape image, PApplet pa, String labelText, String text, float preferredWidth, float preferredHeight, boolean cloasable){
		this(image, pa, labelText, text, preferredWidth, preferredHeight);
		if(cloasable){
			MTImageButton closeButton = new MTImageButton(pa.loadImage("./data/icons/close48px.png"), pa);
			closeButton.registerInputProcessor(new TapProcessor(pa));
			final MTInfoPanel selfref = this;
			closeButton.setNoStroke(true);
			closeButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					switch (e.getID()) {
					case TapEvent.BUTTON_CLICKED:
						AnimationUtil.scaleOut(selfref, true);
						break;
					default:
						break;
					}
					
				}
			});
			closeButton.translate(new Vector3D(this.getWidthXY(TransformSpace.GLOBAL)-closeButton.getWidthXY(TransformSpace.GLOBAL)-5, 5));
			this.addChild(closeButton);
		}
	}
	
	public MTInfoPanel(AbstractShape mti_image, PApplet pa, String labelText, String text, float preferredWidth, float preferredHeight){
		super(0,0,preferredWidth,preferredHeight, pa);

		final MTInfoPanel selfRef = this;
		this.setFillColor(fillColor);
		this.setStrokeColor(strokeColor);
		this.setStrokeWeight(this.strokeWeight);
		
		this.font =  FontManager.getInstance().createFont(pa, "arial", 
				32, 	//Font size
				textColor,  //Font fill color
				textColor);	//Font outline color
		this.font2 =  FontManager.getInstance().createFont(pa, "arial", 
				16, 	//Font size
				textColor,  //Font fill color
				textColor);	//Font outline color
		MTTextArea ta_label = new MTTextArea(pa,font);
			ta_label.setNoFill(true);
			ta_label.setNoStroke(true);
			if(labelText!=null){
				ta_label.setText(labelText);
			}
			ta_label.setPositionRelativeToParent(new Vector3D(0,0));
			this.addChild(ta_label);
//		MTImage mti_image = new MTImage(image, pa);
		mti_image.setPositionRelativeToParent(new Vector3D(mti_image.getWidthXY(TransformSpace.RELATIVE_TO_PARENT)/2f,mti_image.getHeightXY(TransformSpace.RELATIVE_TO_PARENT)/2f));
		this.addChild(mti_image);
			MTTextArea ta_text = new MTTextArea(pa,font2);
			ta_text.setNoFill(true);
			ta_text.setNoStroke(true);
			ta_text.setPositionRelativeToParent(new Vector3D(0,0));
			if(text!=null){
				ta_text.setText(text);
			}
			this.addChild(ta_text);
			
			AbstractShape[] shapes = new AbstractShape[]{ta_label, ta_text, mti_image};
		{
			double heightAllocated = (double)(ta_label.getHeightXY(TransformSpace.GLOBAL) + ta_text.getHeightXY(TransformSpace.GLOBAL));
			double heightThis = this.getHeightXY(TransformSpace.GLOBAL);
			double heightAvailable = heightThis-heightAllocated;
			

			getMaxWidth(shapes);
			for (int i = 0; i < shapes.length; i++) {
				shapes[i].setPickable(false);
			}
			
			
			float scaleFactorFromHeight = (float)(heightAvailable / (double)(mti_image.getHeightXY(TransformSpace.GLOBAL)));
			float scaleFactorFromWidth = (float)(preferredWidth / (double)(mti_image.getWidthXY(TransformSpace.GLOBAL)));
			
			float scaleFactor;
			if(scaleFactorFromHeight<scaleFactorFromWidth){
				scaleFactor = scaleFactorFromHeight;
			}else{
				scaleFactor = scaleFactorFromWidth;
			}
			if(scaleFactor>1.0f){
				scaleFactor = 1.0f;
			}
			
			System.out.println("scaleFactor"+ scaleFactor);
			mti_image.scale(scaleFactor, scaleFactor, 1.0f, mti_image.getCenterPointLocal());
		}
		
		Vector3D posHead = new Vector3D(ta_label.getWidthXY(TransformSpace.RELATIVE_TO_PARENT)/2f,ta_label.getHeightXY(TransformSpace.RELATIVE_TO_PARENT)/2f);
		ta_label.setPositionRelativeToParent(posHead);
		Vector3D posImage = new Vector3D(mti_image.getWidthXY(TransformSpace.RELATIVE_TO_PARENT)/2f,posHead.y*2f+(mti_image.getHeightXY(TransformSpace.RELATIVE_TO_PARENT)/2f));
		mti_image.setPositionRelativeToOther(ta_label, posImage);
//		mti_image.setPositionRelativeToParent(new Vector3D(0,0));
		Vector3D posComment = new Vector3D(ta_text.getWidthXY(TransformSpace.RELATIVE_TO_PARENT)/2f, this.getHeightXYGlobal()- (ta_text.getHeightXY(TransformSpace.RELATIVE_TO_PARENT)/2f));
		ta_text.setPositionRelativeToParent(posComment);
		this.setComposite(false);
		this.setSizeLocal(getMaxWidth(shapes), this.getHeightXY(TransformSpace.LOCAL));
	}
	private float getMaxWidth(AbstractShape[] shapes){
		float max = Float.MIN_VALUE;
		for (int i = 0; i < shapes.length; i++) {
			float width = shapes[i].getWidthXY(TransformSpace.RELATIVE_TO_PARENT);
			if(width>max){
				max=width;
			}
		}
		return max;
	}
	private float getMaxHeight(AbstractShape[] shapes){
		float max = Float.MIN_VALUE;
		for (int i = 0; i < shapes.length; i++) {
			float height = shapes[i].getHeightXY(TransformSpace.RELATIVE_TO_PARENT);
			if(height>max){
				max=height;
			}
		}
		return max;
	}
	@Override
	public void destroy() {
		for (int i = 0; i < this.getChildren().length; i++) {
			this.getChildren()[i].destroy();
		}
		super.destroy();
	}
}
